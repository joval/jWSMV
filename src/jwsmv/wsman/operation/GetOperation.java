// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.wsman.operation;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.security.auth.login.FailedLoginException;

import org.xmlsoap.ws.transfer.AnyXmlOptionalType;
import org.xmlsoap.ws.transfer.AnyXmlType;

import jwsmv.wsman.Port;
import jwsmv.wsman.FaultException;

/**
 * Get operation implementation class.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class GetOperation extends BaseOperation<AnyXmlOptionalType, AnyXmlType> {
    public GetOperation(AnyXmlOptionalType input) {
	super("http://schemas.xmlsoap.org/ws/2004/09/transfer/Get", input);
    }

    @Override
    public AnyXmlType dispatch(Port port) throws IOException, JAXBException, FaultException, FailedLoginException {
	Object obj = dispatch0(port);
	if (obj instanceof AnyXmlType) {
	    return (AnyXmlType)obj;
	} else {
	    AnyXmlType any = Factories.TRANSFER.createAnyXmlType();
	    any.setAny(obj);
	    return any;
	}
    }
}
