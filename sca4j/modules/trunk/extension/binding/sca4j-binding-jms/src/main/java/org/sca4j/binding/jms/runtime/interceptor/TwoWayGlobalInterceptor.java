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
package org.sca4j.binding.jms.runtime.interceptor;

import static javax.transaction.xa.XAResource.TMFAIL;
import static javax.transaction.xa.XAResource.TMSUCCESS;
import static javax.transaction.xa.XAResource.TMSUSPEND;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.sca4j.binding.jms.common.Correlation;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.binding.jms.runtime.tx.JtaTransactionHandler;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.binding.jms.runtime.wireformat.DataBinder;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

/**
 * Dispatches a service invocation to a JMS queue.
 */
public class TwoWayGlobalInterceptor implements Interceptor {

    private Interceptor next;

    private Correlation correlation;
    private JMSObjectFactory jmsFactory;
    private TransactionManager transactionManager;

    private Class<?> inputType;
    private Class<?> outputType;
    private DataBinder dataBinder = new DataBinder();


    public TwoWayGlobalInterceptor(JMSObjectFactory jmsFactory, TransactionManager transactionManager, Correlation correlation, Wire wire) {
        try {
            PhysicalOperationDefinition pod = wire.getInvocationChains().entrySet().iterator().next().getKey().getTargetOperation();
            inputType = Class.forName(pod.getParameters().get(0));
            outputType = Class.forName(pod.getReturnType());
            this.jmsFactory = jmsFactory;
            this.correlation = correlation;
            this.transactionManager = transactionManager;
        } catch (ClassNotFoundException e) {
            throw new SCA4JJmsException("Unable to load operation types", e);
        }
    }

    public Message invoke(Message sca4jRequest) {

        Message sca4jResponse = new MessageImpl();

        Connection connection = null;
        Session session = null;
        MessageProducer messageProducer = null;
        MessageConsumer messageConsumer = null;

        Transaction transaction = null;
        TransactionHandler transactionHandler = new JtaTransactionHandler(transactionManager);

        try {

            connection = jmsFactory.getConnection();
            session = jmsFactory.getSession(connection, TransactionType.GLOBAL);
            connection.start();

            transaction = transactionHandler.getTransaction();
            if (transaction != null) {
                transactionHandler.delist(session, TMSUSPEND);
                transactionHandler.suspend();
            }
            transactionHandler.begin();
            transactionHandler.enlist(session);

            messageProducer = session.createProducer(jmsFactory.getDestination());
            Object[] payload = (Object[]) sca4jRequest.getBody();

            javax.jms.Message jmsRequest = dataBinder.marshal(payload[0], inputType, session);
            messageProducer.send(jmsRequest);
            transactionHandler.delist(session, TMSUCCESS);
            transactionHandler.commit();

            String selector = null;
            switch (correlation) {
                case messageID:
                    selector = "JMSCorrelationID = '" + jmsRequest.getJMSMessageID() + "'";
                    break;
                case correlationID:
                    selector = "JMSCorrelationID = '" + jmsRequest.getJMSCorrelationID() + "'";
                    break;
            }
            messageConsumer = session.createConsumer(jmsFactory.getResponseDestination(), selector);
            transactionHandler.begin();
            transactionHandler.enlist(session);
            javax.jms.Message jmsResponse = messageConsumer.receive();
            sca4jResponse.setBody(dataBinder.unmarshal(jmsResponse, outputType));
            transactionHandler.delist(session, TMSUCCESS);
            transactionHandler.commit();

            if (transaction != null) {
                transactionHandler.resume(transaction);
            }

            return sca4jResponse;

        } catch (JMSException ex) {
            transactionHandler.delist(session, TMFAIL);
            transactionHandler.rollback();
            throw new SCA4JJmsException("Unable to receive response", ex);
        } finally {
            JmsHelper.closeQuietly(messageProducer);
            JmsHelper.closeQuietly(messageConsumer);
            JmsHelper.closeQuietly(session);
            JmsHelper.closeQuietly(connection);
        }

    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

}
