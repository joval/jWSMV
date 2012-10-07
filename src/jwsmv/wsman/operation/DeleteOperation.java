// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.wsman.operation;

import java.io.IOException;
import javax.xml.bind.JAXBException;

import org.xmlsoap.ws.transfer.AnyXmlOptionalType;
import org.xmlsoap.ws.transfer.ObjectFactory;

import jwsmv.wsman.Port;
import jwsmv.wsman.FaultException;

/**
 * Delete operation implementation class.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class DeleteOperation extends BaseOperation<Object, AnyXmlOptionalType> {
    public DeleteOperation() {
	super("http://schemas.xmlsoap.org/ws/2004/09/transfer/Delete", null);
    }

    @Override
    public AnyXmlOptionalType dispatch(Port port) throws IOException, JAXBException, FaultException {
	Object obj = dispatch0(port);
	if (obj instanceof AnyXmlOptionalType) {
	    return (AnyXmlOptionalType)obj;
	} else {
	    AnyXmlOptionalType any = Factories.TRANSFER.createAnyXmlOptionalType();
	    if (obj != null) {
		any.setAny(obj);
	    }
	    return any;
	}
    }
}
