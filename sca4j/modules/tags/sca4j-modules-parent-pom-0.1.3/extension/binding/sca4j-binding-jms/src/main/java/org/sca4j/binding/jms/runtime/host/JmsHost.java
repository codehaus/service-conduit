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
package org.sca4j.binding.jms.runtime.host;

import java.net.URI;

import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.ResponseMessageListener;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;

/**
 * @version $Revision$ $Date$
 */
public interface JmsHost {

    /**
     * Register a <code>ResponseMessageListener<code> which handle inbound message and send response.
     * 
     * @param requestJMSObjectFactory Factory for creating JMS objects for request.
     * @param responseJMSObjectFactory Factory for creating JMS objects for response.
     * @param messageListener Message listener.
     * @param transactionType Transaction type.
     * @param transactionHandler Transaction handler.
     * @param cl Classloader to use.
     * @param serviceUri URI of the service to which the binding is attached.
     */
    public void registerResponseListener(JMSObjectFactory requestJMSObjectFactory,
                                         JMSObjectFactory responseJMSObjectFactory,
                                         ResponseMessageListener messageListener,
                                         TransactionType transactionType,
                                         TransactionHandler transactionHandler,
                                         ClassLoader cl,
                                         URI serviceUri);
    /**
     * Unregister message listener at the endpoint at serviceUri
     * 
     * @param serviceUri URI of the service to which the binding is attached.
     */
    public void unregisterListener(URI serviceUri);
}
