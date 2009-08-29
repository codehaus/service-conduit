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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ServerSessionPool;
import javax.jms.Session;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.ResponseMessageListener;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.host.work.WorkScheduler;

/**
 * 
 * A container class used to support MessageListener with ServerSessionPool.
 * 
 */
public class JMSMessageListenerInvoker implements MessageListener {
    /** Request JMS object factory */
    private JMSObjectFactory requestJMSObjectFactory = null;
    /** Response JMS object factory */
    private JMSObjectFactory responseJMSObjectFactory;
    /** ResponseMessageListenerImpl invoked by this invoker */
    private ResponseMessageListener messageListener = null;
    /** Transaction Type */
    private TransactionType transactionType;
    /** Transaction Handler */
    private TransactionHandler transactionHandler;
    /** WorkScheduler passed to serverSessionPool */
    private WorkScheduler workScheduler;

    public JMSMessageListenerInvoker(JMSObjectFactory requestJMSObjectFactory, JMSObjectFactory responseJMSObjectFactory, ResponseMessageListener messageListener,
            TransactionType transactionType, TransactionHandler transactionHandler, WorkScheduler workScheduler) {
        this.requestJMSObjectFactory = requestJMSObjectFactory;
        this.responseJMSObjectFactory = responseJMSObjectFactory;
        this.messageListener = messageListener;
        this.transactionType = transactionType;
        this.transactionHandler = transactionHandler;
        this.workScheduler = workScheduler;
    }

    public void start(int receiverCount) {
        ServerSessionPool serverSessionPool = createServerSessionPool(receiverCount);
        try {
            Connection connection = requestJMSObjectFactory.getConnection();
            connection.createConnectionConsumer(requestJMSObjectFactory.getDestination(), null, serverSessionPool, 1);
            connection.start();
        } catch (JMSException e) {
            throw new SCA4JJmsException("Error when register Listener", e);

        }
    }

    private StandaloneServerSessionPool createServerSessionPool(int receiverCount) {
        return new StandaloneServerSessionPool(requestJMSObjectFactory, transactionHandler, this, transactionType, workScheduler, receiverCount);
    }

    public void stop() {
        requestJMSObjectFactory.close();
        responseJMSObjectFactory.close();
    }

    public void onMessage(Message message) {
        try {
            Session responseSession = responseJMSObjectFactory.createSession();
            if (transactionType == TransactionType.GLOBAL) {
                transactionHandler.enlist(responseSession);
            }
            Destination responseDestination = responseJMSObjectFactory.getDestination();
            messageListener.onMessage(message, responseSession, responseDestination);
            if (transactionType == TransactionType.GLOBAL) {
                transactionHandler.commit();
            } else if (transactionType == TransactionType.LOCAL) {
                responseSession.commit();
            }
            responseJMSObjectFactory.recycle();
        } catch (JMSException e) {
            throw new SCA4JJmsException("Error when invoking Listener", e);
        } catch (RuntimeException e) {
            try {
                if (transactionType == TransactionType.GLOBAL) {
                    transactionHandler.rollback();
                }
            } catch (Exception ne) {
                // ignore
            }
            throw e;
        }
    }

}
