/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
