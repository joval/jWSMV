// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.http;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.security.Permission;

/**
 * An HttpURLConnection implementation that can negotiate Kerberos authentication with an HTTP proxy and/or
 * destination HTTP server, even when using streaming modes.
 *
 * @author David A. Solin
 * @version %I% %V%
 */
public class KerberosHttpURLConnection extends AbstractConnection {
    public static KerberosHttpURLConnection openConnection(URL url, PasswordAuthentication cred, boolean encrypt) {
	throw new RuntimeException("Kerberos support is TBD");
    }

    private Proxy proxy;
    private String host;

    public void setProxy(Proxy proxy) {
	this.proxy = proxy;
    }

    // Overrides for HttpURLConnection

    @Override
    public Permission getPermission() throws IOException {
	return new java.net.SocketPermission(host, "connect");
    }

    @Override
    public boolean usingProxy() {
	return proxy != null;
    }

    @Override
    public void connect() {
    }

    @Override
    public void disconnect() {
    }

    // Internal

    void getResponse() {
    }

    // Private

    private KerberosHttpURLConnection(URL u) {
	super(u);
	host = u.getHost();
    }
}
