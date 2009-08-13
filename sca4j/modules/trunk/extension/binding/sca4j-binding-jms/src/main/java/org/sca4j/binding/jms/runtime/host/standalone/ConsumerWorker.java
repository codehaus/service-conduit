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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.JMSRuntimeMonitor;
import org.sca4j.binding.jms.runtime.ResponseMessageListener;
import org.sca4j.binding.jms.runtime.tx.JmsTxException;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.host.work.DefaultPausableWork;

/**
 * A thread pull message from destination and invoke Message listener.
 * 
 * @version $Revision$ $Date$
 */
public class ConsumerWorker extends DefaultPausableWork {

    private final Session session;
    private final TransactionHandler transactionHandler;
    private final MessageConsumer consumer;
    private final ResponseMessageListener listener;
    private final long readTimeout;
    private final TransactionType transactionType;
    private final ClassLoader cl;
    private final JMSObjectFactory responseJMSObjectFactory;
    private final JMSObjectFactory requestJMSObjectFactory;
    private JMSRuntimeMonitor monitor;

    /**
     * @param session Session used to receive messages.
     * @param transactionHandler Transaction handler.
     * @param consumer Message consumer.
     * @param listener Delegate message listener.
     * @param readTimeout Read timeout.
     */
    public ConsumerWorker(ConsumerWorkerTemplate template) {

        super(true);

        try {

            transactionHandler = template.getTransactionHandler();
            transactionType = template.getTransactionType();
            listener = template.getListener();
            responseJMSObjectFactory = template.getResponseJMSObjectFactory();
            requestJMSObjectFactory = template.getRequestJMSObjectFactory();
            session = requestJMSObjectFactory.createSession();
            consumer = session.createConsumer(requestJMSObjectFactory.getDestination());
            readTimeout = template.getReadTimeout();
            cl = template.getCl();
            monitor = template.getMonitor();

        } catch (JMSException e) {
            throw new SCA4JJmsException("Unale to create consumer", e);
        }

    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void execute() {
        Session responseSession = null;
        Destination responseDestination = null;

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {

            Thread.currentThread().setContextClassLoader(cl);
            if (transactionType == TransactionType.GLOBAL) {
                transactionHandler.enlist(session);
            }
            Message message = consumer.receive(readTimeout);
            try {
                if (message != null) {
                    if (responseJMSObjectFactory != null) {
                        responseSession = responseJMSObjectFactory.createSession();
                        if (transactionType == TransactionType.GLOBAL) {
                            transactionHandler.enlist(responseSession);
                        }
                        responseDestination = responseJMSObjectFactory.getDestination();
                    }
                    listener.onMessage(message, responseSession, responseDestination);
                    if (transactionType == TransactionType.GLOBAL) {
                        transactionHandler.commit();
                        transactionHandler.enlist(session);
                    } else {
                        session.commit();
                    }
                }
            } catch (JmsTxException e) {
                if (transactionType == TransactionType.GLOBAL) {
                    transactionHandler.rollback();
                } else {
                    try {
                        session.rollback();
                    } catch (JMSException ne) {
                        reportException(ne);
                    }
                    reportException(e);
                }
            } catch (SCA4JJmsException se) {
                if (transactionType == TransactionType.GLOBAL) {
                    transactionHandler.rollback();
                } else {
                    try {
                        session.rollback();
                    } catch (JMSException ne) {
                        reportException(ne);
                    }
                    reportException(se);
                }
            }
        } catch (JMSException ex) {
            if (transactionType == TransactionType.GLOBAL) {
                transactionHandler.rollback();
            } else {
                try {
                    session.rollback();
                } catch (JMSException ne) {
                    reportException(ne);
                }
                reportException(ex);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

    }

    /**
     * Report an exception.
     */
    private void reportException(Exception e) {
        if (monitor != null) {
            monitor.jmsListenerError(e);
        }
    }

}
