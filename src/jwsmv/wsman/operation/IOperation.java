// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.wsman.operation;

import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.Duration;

import jwsmv.wsman.Port;
import jwsmv.wsman.FaultException;

/**
 * Interface describing a SOAP operation, with generic input and output types.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public interface IOperation<I, O> {
    /**
     * Set the timeout for the operation.
     */
    void setTimeout(long millis);

    /**
     * Add a mandatory DMTF WS-Management Resource URI header field for the operation.
     */
    void addResourceURI(String uri);

    /**
     * Get the output (i.e., SOAP response message body contents) for the operation, dispatched through the specified
     * port.
     */
    O dispatch(Port port) throws IOException, JAXBException, FaultException;
}
