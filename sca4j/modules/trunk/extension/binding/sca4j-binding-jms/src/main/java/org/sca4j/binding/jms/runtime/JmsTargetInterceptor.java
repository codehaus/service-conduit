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
package org.sca4j.binding.jms.runtime;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.transaction.xa.XAResource;

import org.sca4j.binding.jms.common.CorrelationScheme;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.binding.jms.runtime.wireformat.DataBinder;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

/**
 * Dispatches a service invocation to a JMS queue.
 * 
 * @version $Revision: 5322 $ $Date: 2008-09-02 20:15:34 +0100 (Tue, 02 Sep
 *          2008) $
 * 
 * TODO Enable global transactions will two-way reference.
 * TODO Do rollback properly on local transactions.
 */
public class JmsTargetInterceptor implements Interceptor {

    private Interceptor next;
    
    private CorrelationScheme correlationScheme;
    private JMSObjectFactory requestFactory;
    private JMSObjectFactory responseFactory;
    private TransactionType transactionType;
    private TransactionHandler transactionHandler;
    private boolean twoWay;
    
    private Class<?> inputType;
    private Class<?> outputType;
    private DataBinder dataBinder = new DataBinder();
    

    public JmsTargetInterceptor(JMSObjectFactory requestFactory, 
                                JMSObjectFactory responseFactory, 
                                TransactionType transactionType,
                                TransactionHandler transactionHandler,
                                CorrelationScheme correlationScheme,
                                Wire wire) {
        try {
            PhysicalOperationDefinition pod = wire.getInvocationChains().entrySet().iterator().next().getKey().getTargetOperation();
            inputType = Class.forName(pod.getParameters().get(0));
            String outputTypeName = pod.getReturnType();
            if (outputTypeName != null) {
                outputType = Class.forName(outputTypeName);
            }
            twoWay = outputType != null;
            this.requestFactory = requestFactory;
            this.responseFactory = responseFactory;
            this.correlationScheme = correlationScheme;
            this.transactionType = transactionType;
            this.transactionHandler = transactionHandler;
        } catch (ClassNotFoundException e) {
            throw new SCA4JJmsException("Unable to load operation types", e);
        }
    }

    public Message invoke(Message sca4jRequest) {

        Message sca4jResponse = new MessageImpl();
        
        Connection requestConnection = null;
        Session requestSession = null;
        MessageProducer requestProducer = null;

        Connection responseConnection = null;
        Session responseSession = null;
        MessageConsumer responseConsumer = null;
        
        boolean txStarted = false;
        
        try {
            
            requestConnection = requestFactory.getConnection();
            requestSession = requestFactory.getSession(requestConnection, transactionType);
            if (transactionType == TransactionType.GLOBAL) {
                if (transactionHandler.getTransaction() != null) {
                    transactionHandler.begin();
                    txStarted = true;
                }
                transactionHandler.enlist(requestSession);
            }

            requestProducer = requestSession.createProducer(requestFactory.getDestination());
            Object[] payload = (Object[]) sca4jRequest.getBody();
            

            javax.jms.Message jmsRequest = dataBinder.marshal(payload[0], inputType, requestSession);
            requestProducer.send(jmsRequest);
            
            if (twoWay && transactionType == TransactionType.LOCAL) {
                requestSession.commit();
                responseConnection = responseFactory.getConnection();
                responseSession = responseFactory.getSession(responseConnection, transactionType);
                String selector = null;
                switch (correlationScheme) {
                    case messageID: 
                        selector = "JMSCorrelationID = '" + jmsRequest.getJMSMessageID() + "'";
                        break;
                    case correlationID: 
                        selector = "JMSCorrelationID = '" + jmsRequest.getJMSCorrelationID() + "'";
                        break;
                }
                responseConsumer = responseSession.createConsumer(responseFactory.getDestination(), selector);
                javax.jms.Message jmsResponse = responseConsumer.receive();
                sca4jResponse.setBody(dataBinder.unmarshal(jmsResponse, outputType));
                responseSession.commit();
            }
            
            if (transactionType == TransactionType.GLOBAL) {
                if (txStarted) {
                    transactionHandler.commit();
                } 
                transactionHandler.delist(requestSession, XAResource.TMSUCCESS);
            }

            return sca4jResponse;

        } catch (JMSException ex) {
            if (transactionType == TransactionType.GLOBAL) {
                if (txStarted) {
                    transactionHandler.rollback();
                } 
                transactionHandler.delist(requestSession, XAResource.TMFAIL);
            }
            throw new SCA4JJmsException("Unable to receive response", ex);
        } finally {
            JmsHelper.closeQuietly(requestProducer);
            JmsHelper.closeQuietly(requestSession);
            JmsHelper.closeQuietly(requestConnection);
            JmsHelper.closeQuietly(responseConsumer);
            JmsHelper.closeQuietly(responseSession);
            JmsHelper.closeQuietly(responseConnection);
        }
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

}
