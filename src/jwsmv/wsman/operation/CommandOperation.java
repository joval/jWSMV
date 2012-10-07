// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.wsman.operation;

import javax.xml.bind.JAXBElement;

import com.microsoft.wsman.shell.CommandLine;
import com.microsoft.wsman.shell.CommandResponse;

/**
 * Command operation implementation class.
 *
 * @author David A. Solin
 * @version %I%, %G%
 */
public class CommandOperation extends BaseOperation<JAXBElement<CommandLine>, CommandResponse> {
    public CommandOperation(CommandLine input) {
	super("http://schemas.microsoft.com/wbem/wsman/1/windows/shell/Command", Factories.SHELL.createCommandLine(input));
    }
}
