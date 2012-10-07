// Copyright (C) 2012 jOVAL.org.  All rights reserved.
// This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt

package jwsmv.wsman.operation;

import org.xmlsoap.ws.eventing.Subscribe;
import org.xmlsoap.ws.eventing.SubscribeResponse;

/**
 * Subscribe operation implementation class.
 *
 * @author David A. Solin
 * @version %I% %G%
 */
public class SubscribeOperation extends BaseOperation<Subscribe, SubscribeResponse> {
    public SubscribeOperation(Subscribe input) {
	super("http://schemas.xmlsoap.org/ws/2004/08/eventing/Subscribe", input);
    }
}
