// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import com.microsoft.wsman.fault.WSManFaultType;
import com.microsoft.wsman.shell.CommandLine;
import com.microsoft.wsman.shell.CommandResponse;
import com.microsoft.wsman.shell.CommandStateType;
import com.microsoft.wsman.shell.DesiredStreamType;
import com.microsoft.wsman.shell.Receive;
import com.microsoft.wsman.shell.ReceiveResponse;
import com.microsoft.wsman.shell.Signal;
import com.microsoft.wsman.shell.SignalResponse;
import com.microsoft.wsman.shell.Send;
import com.microsoft.wsman.shell.SendResponse;
import com.microsoft.wsman.shell.StreamType;
import org.dmtf.wsman.AttributableEmpty;
import org.dmtf.wsman.AttributablePositiveInteger;
import org.dmtf.wsman.OptionSet;
import org.dmtf.wsman.OptionType;
import org.dmtf.wsman.SelectorSetType;
import org.dmtf.wsman.SelectorType;
import org.w3c.soap.envelope.Fault;

import jwsmv.wsman.FaultException;
import jwsmv.wsman.Port;
import jwsmv.wsman.operation.CommandOperation;
import jwsmv.wsman.operation.DeleteOperation;
import jwsmv.wsman.operation.ReceiveOperation;
import jwsmv.wsman.operation.SendOperation;
import jwsmv.wsman.operation.SignalOperation;

/**
 * Simple implementation of a WinRM Shell-based Process.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class ShellCommand extends Process implements Constants {
    /**
     * An enumeration of codes that can be issued to a running process using a signal.
     */
    public enum SignalCode {
	TERMINATE("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/signal/terminate"),
	CTL_C("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/signal/ctrl_c"),
	CTL_BREAK("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/signal/ctrl_break");

	private String value;

	private SignalCode(String value) {
	    this.value = value;
	}

	String value() {
	    return value;
	}
    }

    /**
     * An enumeration of the possible states of a running process, embedding their URI values.
     */
    public enum State {
	DONE("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Done"),
	PENDING("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Pending"),
	RUNNING("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/CommandState/Running");

	private String value;

	private State(String value) {
	    this.value = value;
	}

	String value() {
	    return value;
	}

	static State fromValue(String s) throws IllegalArgumentException {
	    for (State state : values()) {
		if (state.value().equals(s)) {
		    return state;
		}
	    }
	    throw new IllegalArgumentException(s);
	}
    }

    private Port port;
    private String shellId, id;
    private State state;
    private int exitCode;
    private InputStream stdout, stderr;
    private PipedOutputStream stderrPipe;
    private OutputStream stdin;
    private String cmd;
    private String[] args;
    private boolean disposable;
    private long lastDispatch;

    /**
     * Create a command for the specified Shell.
     */
    public ShellCommand(Shell shell, String cmd, String[] args) {
	this.shellId = shell.id;
	this.port = shell.port;
	this.cmd = cmd;
	this.args = args;
	stdin = null;
	stderr = null;
	stdout = null;
	exitCode = -1;
	disposable = false;
    }

    /**
     * Get the ID of the command.
     */
    public String getId() {
	return id;
    }

    public long lastDispatch() {
	return lastDispatch;
    }

    public String getCommand() {
	StringBuffer sb = new StringBuffer(cmd);
	if (args != null) {
	    for (String arg : args) {
		sb.append(" ");
		if (arg.indexOf(" ") == -1) {
		    sb.append(arg);
		} else {
		    sb.append("\"").append(arg.replace("\"","\\\"")).append("\"");
		}
	    }
	}
	return sb.toString();
    }

    public void setInteractive(boolean interactive) {
	// no-op
    }

    /**
     * WARNING: If no Thread is reading the process output or error streams, this method will never return.  We
     * previously attempted to read the streams from another thread, but asynchronous reads cause major performance
     * problems.
     */
    public void waitFor(long millis) throws InterruptedException {
	long endTime = System.currentTimeMillis() + millis;
	while (state != State.DONE && System.currentTimeMillis() < endTime) {
	    Thread.sleep(100);
	}
    }

    /**
     * Test to see if the process is running.
     */
    public boolean isRunning() {
	return state == State.RUNNING;
    }

    public void start() throws JAXBException, IOException, FaultException {
	CommandLine cl = Factories.SHELL.createCommandLine();
	cl.setCommand(cmd);
	if (args != null) {
	    for (String arg : args) {
		cl.getArguments().add(arg);
	    }
	}

	CommandOperation commandOperation = new CommandOperation(cl);
	commandOperation.addResourceURI(SHELL_URI);
	commandOperation.addSelectorSet(getSelectorSet());

	//
	// The client-side mode for standard input is console if TRUE and pipe if FALSE. This does not
	// have an impact on the wire protocol. This option name MUST be used by the client of the Text-based
	// Command Shell when starting the execution of a command using rsp:Command request to indicate that
	// the client side of the standard input is console; the default implies pipe.
	//
	OptionType winrsStdin = Factories.WSMAN.createOptionType();
	winrsStdin.setName("WINRS_CONSOLEMODE_STDIN");
	winrsStdin.setValue("FALSE");

	//
	// If set to TRUE, this option requests that the server runs the command without using cmd.exe; if
	// set to FALSE, the server is requested to use cmd.exe. By default the value is FALSE. This does
	// not have any impact on the wire protocol.
	//
	OptionType winrsSkipCmd = Factories.WSMAN.createOptionType();
	winrsSkipCmd.setName("WINRS_SKIP_CMD_SHELL");
	winrsSkipCmd.setValue("FALSE");

	OptionSet options = Factories.WSMAN.createOptionSet();
	options.getOption().add(winrsStdin);
	options.getOption().add(winrsSkipCmd);
	commandOperation.addOptionSet(options);

	CommandResponse response = commandOperation.dispatch(port);
	lastDispatch = System.currentTimeMillis();
	state = State.RUNNING;
	disposable = true;
	id = response.getCommandId();

	stderrPipe = new PipedOutputStream();
	stderr = new PipedInputStream(stderrPipe);
    }

    // Overrides of Process methods

    @Override
    public int waitFor() throws InterruptedException {
	waitFor(360000L);
	if (isRunning()) {
	    destroy();
	}
	return exitValue();
    }

    @Override
    public void destroy() {
	finalize();
	state = State.DONE;
    }

    @Override
    public OutputStream getOutputStream() {
	if (stdin == null) {
	    stdin = new CommandOutputStream(1024);
	}
	return stdin;
    }

    @Override
    public InputStream getInputStream() {
	if (stdout == null) {
	    stdout = new CommandInputStream();
	}
	return stdout;
    }

    @Override
    public InputStream getErrorStream() {
	return stderr;
    }

    @Override
    public int exitValue() throws IllegalThreadStateException {
	switch(state) {
	  case DONE:
	    return exitCode;
	  default:
	    throw new IllegalThreadStateException(state.toString());
	}
    }

    // Internal

    /**
     * Delete the ShellCommand on the target machine (idempotent).
     */
    @Override
    protected void finalize() {
	if (disposable) {
	    try {
		Signal signal = Factories.SHELL.createSignal();
		signal.setCommandId(id);
		signal.setCode(SignalCode.TERMINATE.value());
		SignalOperation signalOperation = new SignalOperation(signal);
		signalOperation.addResourceURI(SHELL_URI);
		signalOperation.addSelectorSet(getSelectorSet());
		SignalResponse response = signalOperation.dispatch(port);
		stderrPipe.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    disposable = false;
	}
    }

    /**
     * An OutputStream implementation that is triggered by flush() to send data upstream to the process.
     */
    class CommandOutputStream extends ByteArrayOutputStream {
	CommandOutputStream(int size) {
	    super(size);
	}

	@Override
	public synchronized void flush() throws IOException {
	    try {
		StreamType stream = Factories.SHELL.createStreamType();
		stream.setName(Shell.STDIN);
		stream.setCommandId(id);
		stream.setValue(toByteArray());
		reset();
		Send send = Factories.SHELL.createSend();
		send.getStream().add(stream);
		SendOperation sendOperation = new SendOperation(send);
		sendOperation.addResourceURI(SHELL_URI);
		sendOperation.addSelectorSet(getSelectorSet());

		SendResponse response = sendOperation.dispatch(port);
		lastDispatch = System.currentTimeMillis();
		if (response.isSetDesiredStream()) {
		    StreamType rs = response.getDesiredStream();
		    if (rs.getName().equals(Shell.STDIN) && rs.getEnd()) {
			close();
		    }
		}
	    } catch (JAXBException e) {
		throw new IOException(e);
	    } catch (FaultException e) {
		throw new IOException(e);
	    }
	}
    }

    /**
     * An InputStream implementation fills an internal buffer, when required, by issuing a receive operation
     * to the process.
     */
    class CommandInputStream extends InputStream {
	byte[] buff;
	int pos;

	CommandInputStream() {
	    buff = new byte[0];
	    pos = 0;
	}

	@Override
	public int read(byte[] bytes) throws IOException {
	    return read(bytes, 0, bytes.length);
	}

	@Override
	public int read(byte[] bytes, int offset, int len) throws IOException {
	    int maxToRead = Math.min(bytes.length - offset, len);
	    if (available() > 0) {
		int bytesToRead = Math.min(maxToRead, available());
		System.arraycopy(buff, pos, bytes, offset, bytesToRead);
		pos = pos + bytesToRead;
		return bytesToRead;
	    } else if (maxToRead > 0) {
		int ch = read();
		if (ch == -1) {
		    return -1;
		} else {
		    bytes[offset] = (byte)(ch & 0xFF);
		    return 1;
		}
	    } else {
		return 0;
	    }
	}

	@Override
	public int read() throws IOException {
	    if (pos < buff.length) {
		// return from buffer
		return buff[pos++];
	    } else {
		switch(state) {
		  case RUNNING:
		  case PENDING:
		    refill();
		    return read();
		  case DONE:
		  default:
		    return -1;
		}
	    }
	}

	@Override
	public int available() {
	    return buff.length - pos;
	}

	// Private

	private void refill() throws IOException {
	    try {
		DesiredStreamType desired = Factories.SHELL.createDesiredStreamType();
		desired.setCommandId(id);
		desired.getValue().add(Shell.STDOUT);
		desired.getValue().add(Shell.STDERR);
		Receive receive = Factories.SHELL.createReceive();
		receive.setDesiredStream(desired);
		ReceiveOperation receiveOperation = new ReceiveOperation(receive);
		receiveOperation.addResourceURI(SHELL_URI);
		receiveOperation.addSelectorSet(getSelectorSet());
		OptionType keepAlive = Factories.WSMAN.createOptionType();
		keepAlive.setName("WSMAN_CMDSHELL_OPTION_KEEPALIVE");
		keepAlive.setValue("TRUE");
		OptionSet options = Factories.WSMAN.createOptionSet();
		options.getOption().add(keepAlive);
		receiveOperation.addOptionSet(options);

		ReceiveResponse response = receiveOperation.dispatch(port);
		lastDispatch = System.currentTimeMillis();
		buff = null;
		pos = 0;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (StreamType stream : response.getStream()) {
		    if (stream.isSetValue()) {
			byte[] val = stream.getValue();
			if (val.length > 0) {
			    String streamName = stream.getName();
			    if (Shell.STDOUT.equals(streamName)) {
				buffer.write(val);
			    } else if (Shell.STDERR.equals(streamName)) {
				stderrPipe.write(val);
				stderrPipe.flush();
			    }
			}
		    }
		}
		buff = buffer.toByteArray();
		if (response.isSetCommandState()) {
		    CommandStateType state = response.getCommandState();
		    ShellCommand.this.state = State.fromValue(state.getState());
		    if (state.isSetExitCode()) {
			exitCode = state.getExitCode().intValue();
			//
			// Per section section 3.1.4.14, point 4 of MS-WSMV specification client MUST send
			// signal message with Terminate code after receiving final response from server.
			//
			ShellCommand.this.finalize();
		    }
		}
	    } catch (JAXBException e) {
		throw new IOException(e);
	    } catch (FaultException e) {
		boolean retry = false;
		Fault fault = e.getFault();
		if (fault.isSetDetail()) {
		    for (Object obj : fault.getDetail().getAny()) {
			if (obj instanceof JAXBElement) {
			    obj = ((JAXBElement)obj).getValue();
			}
			if (obj instanceof WSManFaultType) {
			    WSManFaultType wsFault = (WSManFaultType)obj;
			    if (wsFault.getCode() == 2150858793L) {
				retry = true;
			    }
			}
		    }
		}
		if (retry) {
		    refill();
		} else {
		    throw new IOException(e);
		}
	    }
	}
    }

    /**
     * Get a SelectorSetType with the ShellId.
     */
    private SelectorSetType getSelectorSet() {
	SelectorSetType set = Factories.WSMAN.createSelectorSetType();
	SelectorType sel = Factories.WSMAN.createSelectorType();
	sel.setName("ShellId");
	sel.getContent().add(shellId);
	set.getSelector().add(sel);
	return set;
    }
}
