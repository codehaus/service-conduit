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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.sca4j.binding.jms.runtime;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.sca4j.binding.jms.common.CorrelationScheme;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.invocation.MessageImpl;

/**
 * Message listeher for service requests.
 *
 * @version $Revison$ $Date: 2008-03-17 21:24:49 +0000 (Mon, 17 Mar 2008) $
 */
public class SCA4JMessageListener implements MessageListener {

    /**
     * Destination for sending response.
     */
    private Destination destination;

    /**
     * Connection factory for sending response.
     */
    private ConnectionFactory connectionFactory;

    /**
     * Operations available on the contract.
     */
    private Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> ops;

    /**
     * Correlation scheme.
     */
    private CorrelationScheme correlationScheme;

    /**
     * Transaction handler.
     */
    private TransactionHandler transactionHandler;

    /**
     * Transaction type.
     */
    private TransactionType transactionType;

    /**
     * @param destination        Destination for sending responses.
     * @param connectionFactory  Connection factory for sending responses.
     * @param ops                Map of operation definitions.
     * @param correlationScheme  Correlation scheme.
     * @param transactionHandler Transaction handler.
     * @param transactionType    the type of transaction
     */
    public SCA4JMessageListener(Destination destination,
                                  ConnectionFactory connectionFactory,
                                  Map<String, Entry<PhysicalOperationDefinition, InvocationChain>> ops,
                                  CorrelationScheme correlationScheme,
                                  TransactionHandler transactionHandler,
                                  TransactionType transactionType) {
        this.destination = destination;
        this.connectionFactory = connectionFactory;
        this.ops = ops;
        this.correlationScheme = correlationScheme;
        this.transactionHandler = transactionHandler;
        this.transactionType = transactionType;
    }

    /**
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    public void onMessage(Message request) {

        Connection connection = null;

        try {

            String opName = request.getStringProperty("scaOperationName");
            Interceptor interceptor = getInterceptor(opName);

            ObjectMessage objectMessage = (ObjectMessage) request;
            Object[] payload = (Object[]) objectMessage.getObject();

            org.sca4j.spi.invocation.Message inMessage = new MessageImpl(payload, false, new WorkContext());
            org.sca4j.spi.invocation.Message outMessage = interceptor.invoke(inMessage);

            connection = connectionFactory.createConnection();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            if (transactionType == TransactionType.GLOBAL) {
                transactionHandler.enlist(session);
            }

            MessageProducer producer = session.createProducer(destination);
            Message response = session.createObjectMessage((Serializable) outMessage.getBody());

            switch (correlationScheme) {
            case RequestCorrelIDToCorrelID: {
                response.setStringProperty("JMSCorrelationID", request.getJMSCorrelationID());
                break;
            }
            case RequestMsgIDToCorrelID: {
                response.setStringProperty("JMSCorrelationID", request.getJMSMessageID());
                break;
            }
            }

            producer.send(response);

            if (transactionType == TransactionType.LOCAL) {
                session.commit();
            }

        } catch (JMSException ex) {
            throw new SCA4JJmsException("Unable to send response", ex);
        } finally {
            JmsHelper.closeQuietly(connection);
        }

    }

    /*
     * Finds the matching interceptor.
     */
    private Interceptor getInterceptor(String opName) {

        if (ops.size() == 1) {
            return ops.values().iterator().next().getValue().getHeadInterceptor();
        } else if (opName != null && ops.containsKey(opName)) {
            return ops.get(opName).getValue().getHeadInterceptor();
        } else if (ops.containsKey("onMessage")) {
            return ops.get("onMessage").getValue().getHeadInterceptor();
        } else {
            throw new SCA4JJmsException("Unable to match operation on the service contract");
        }

    }

}
