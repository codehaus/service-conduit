/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.binding.jms.runtime;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;

/**
 * A <CODE>ResponseMessageListener</CODE> object is used to receive
 * asynchronously delivered messages and then optionally send response
 */
public interface ResponseMessageListener {

    /**
     * Passes a message to the listener.
     * 
     * @param request the message passed to the listener
     * @param responseSession the JMSSession object which is used to send
     *            response message
     * @param responseDestination JMSDestination to which the response is sent
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public abstract void onMessage(Message request, Session responseSession, Destination responseDestination);

}
