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
package org.sca4j.binding.aq.runtime.wire.interceptor;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.XAConnection;
import javax.jms.XAQueueConnectionFactory;
import javax.jms.XASession;

import org.sca4j.binding.aq.common.SCA4JAQException;
import org.sca4j.binding.aq.runtime.helper.JmsHelper;
import org.sca4j.binding.aq.runtime.tx.TransactionHandler;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;

/**
 * @version $Revision: 3022 $ $Date: 2007-09-17 18:06:21 +0100 (Mon, 17 Sep
 *          2007) $
 */
public class OneWayAQTargetInterceptor implements Interceptor {
   
    private Interceptor next;    
    private String methodName;
    private Destination destination;    
    private XAQueueConnectionFactory connectionFactory;            
    private TransactionHandler transactionHandler;
    private ClassLoader cl;

    /**
     * @param methodName Method name.
     * @param destination Request destination.
     * @param connectionFactory Request connection factory.
     * @param correlationScheme Correlation scheme.
     * @param messageReceiver Message receiver for response.
     */
    public OneWayAQTargetInterceptor(String methodName, XAQueueConnectionFactory connectionFactory, Destination destination, TransactionHandler transactionHandler, ClassLoader classLoader) {
        this.methodName = methodName;
        this.connectionFactory = connectionFactory;
        this.destination = destination;
        this.transactionHandler = transactionHandler;
        this.cl = classLoader;                
    }

    /**
     * @see org.sca4j.spi.wire.Interceptor#getNext()
     */
    public Interceptor getNext() {
        return next;
    }

    /**
     * @see org.sca4j.spi.wire.Interceptor#invoke(org.sca4j.spi.wire.Message)
     */
    public Message invoke(Message message) {

        final Message empty = new MessageImpl();
        XAConnection connection = null;

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();

        try {

            Thread.currentThread().setContextClassLoader(cl);

            connection = connectionFactory.createXAConnection();
            XASession session = connection.createXASession();
            MessageProducer producer = session.createProducer(destination);
            Object[] payload = (Object[]) message.getBody();
            javax.jms.Message jmsMessage = session.createObjectMessage(payload);
            jmsMessage.setStringProperty("scaOperationName", methodName);
            
            transactionHandler.enlist(session);          
            producer.send(jmsMessage);                    
            
            return empty;
        } catch (JMSException ex) {
            throw new SCA4JAQException("Unable to receive response", ex);
        } finally {
            JmsHelper.closeQuietly(connection);
            Thread.currentThread().setContextClassLoader(oldCl);
        }

    }

    /**
     * @see org.sca4j.spi.wire.Interceptor#setNext(org.sca4j.spi.wire.Interceptor)
     */
    public void setNext(Interceptor next) {
        this.next = next;
    }   

}
