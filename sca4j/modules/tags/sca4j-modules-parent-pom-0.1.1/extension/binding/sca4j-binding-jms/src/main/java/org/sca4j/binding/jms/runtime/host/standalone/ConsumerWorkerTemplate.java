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
package org.sca4j.binding.jms.runtime.host.standalone;

import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.JMSRuntimeMonitor;
import org.sca4j.binding.jms.runtime.ResponseMessageListener;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;

/**
 * A thread pull message from destination and invoke Message listener.
 *
 * @version $Revision$ $Date$
 */
public class ConsumerWorkerTemplate {

    private final TransactionHandler transactionHandler;
    private final ResponseMessageListener listener;
    private final long readTimeout;
    private final TransactionType transactionType;
    private final ClassLoader cl;
    private final JMSObjectFactory responseJMSObjectFactory;
    private final JMSObjectFactory requestJMSObjectFactory;
    private JMSRuntimeMonitor monitor;

    /**
     * @param session
     * @param transactionHandler
     * @param transactionType
     * @param consumer
     * @param listener
     * @param responseJMSObjectFactory
     * @param readTimeout
     * @param cl
     * @param monitor
     */
    public ConsumerWorkerTemplate(TransactionHandler transactionHandler,
                                  TransactionType transactionType,
                                  ResponseMessageListener listener,
                                  JMSObjectFactory responseJMSObjectFactory,
                                  JMSObjectFactory requestJMSObjectFactory,
                                  long readTimeout,
                                  ClassLoader cl,
                                  JMSRuntimeMonitor monitor) {
        this.transactionHandler = transactionHandler;
        this.transactionType = transactionType;
        this.listener = listener;
        this.responseJMSObjectFactory = responseJMSObjectFactory;
        this.requestJMSObjectFactory = requestJMSObjectFactory;
        this.readTimeout = readTimeout;
        this.cl = cl;
        this.monitor = monitor;
    }

    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }

    public ResponseMessageListener getListener() {
        return listener;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public ClassLoader getCl() {
        return cl;
    }

    public JMSObjectFactory getResponseJMSObjectFactory() {
        return responseJMSObjectFactory;
    }

    public JMSRuntimeMonitor getMonitor() {
        return monitor;
    }

    public JMSObjectFactory getRequestJMSObjectFactory() {
        return requestJMSObjectFactory;
    }
    
}
