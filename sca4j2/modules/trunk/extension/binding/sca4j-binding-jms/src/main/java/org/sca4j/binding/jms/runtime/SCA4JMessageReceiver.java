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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;

/**
 * Message listeher for service requests.
 * 
 * @version $Revison$ $Date: 2008-03-17 21:24:49 +0000 (Mon, 17 Mar 2008) $
 */
public class SCA4JMessageReceiver {

    /**
     * Destination for receiving response.
     */
    private Destination destination;

    /**
     * Connection factory for receiving response.
     */
    private ConnectionFactory connectionFactory;

    /**
     * @param destination Destination for sending responses.
     * @param connectionFactory Connection factory for sending responses.
     */
    public SCA4JMessageReceiver(Destination destination, ConnectionFactory connectionFactory) {
        this.destination = destination;
        this.connectionFactory = connectionFactory;
    }

    /**
     * Performs a blocking receive.
     * 
     * @param correlationId Correlation Id.
     * @return Received message.
     */
    public Message receive(String correlationId) {

        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            String selector = "JMSCorrelationID = '" + correlationId + "'";
            MessageConsumer consumer = session.createConsumer(destination, selector);
            connection.start();

            Message message = consumer.receive();
            session.commit();

            return message;

        } catch (JMSException ex) {
            throw new SCA4JJmsException("Unable to receive response", ex);
        } finally {
            JmsHelper.closeQuietly(connection);
        }

    }

}
