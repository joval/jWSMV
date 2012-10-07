// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.wsman;

import javax.xml.bind.JAXBElement;

import com.microsoft.wsman.fault.WSManFaultType;
import com.microsoft.wsman.fault.ProviderFaultType;
import org.w3c.soap.envelope.Detail;
import org.w3c.soap.envelope.Fault;

/**
 * A Web-Service (SOAP) Fault-derived exception class
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class FaultException extends Exception {
    Fault fault;

    public FaultException(Fault fault) {
	super();
	this.fault = fault;
    }

    public Fault getFault() {
	return fault;
    }

    /**
     * Extract the (required) detail message from the fault.
     */
    @Override
    public String getMessage() {
	Detail detail = fault.getDetail();
	JAXBElement elt = (JAXBElement)detail.getAny().get(0);
	Object obj = elt.getValue();
	if (obj instanceof String) {
	    return (String)obj;
	} else if (obj instanceof WSManFaultType) {
	    WSManFaultType wsmft = (WSManFaultType)obj;
	    Object val = wsmft.getMessage().getContent().get(0);
	    if (val instanceof JAXBElement) {
		val = ((JAXBElement)val).getValue();
	    }
	    if (val instanceof ProviderFaultType) {
		ProviderFaultType pft = (ProviderFaultType)val;
		if (pft.isSetContent()) {
		    StringBuffer sb = new StringBuffer();
		    for (Object content : pft.getContent()) {
			sb.append((String)content);
		    }
		    return sb.toString();
		} else {
		    return pft.getProviderId();
		}
	    } else {
		return (String)val;
	    }
	} else {
	    return obj.toString();
	}
    }
}
