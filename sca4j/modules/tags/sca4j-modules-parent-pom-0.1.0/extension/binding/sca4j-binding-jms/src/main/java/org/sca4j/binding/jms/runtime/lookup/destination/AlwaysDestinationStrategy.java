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

package org.sca4j.binding.jms.runtime.lookup.destination;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.jms.TopicConnection;

import org.sca4j.binding.jms.common.DestinationDefinition;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;

/**
 * The destination is never looked up, it is always created.
 *
 */
public class AlwaysDestinationStrategy implements DestinationStrategy {

    /**
     * @see org.sca4j.binding.jms.runtime.lookup.destination.DestinationStrategy#getDestination(org.sca4j.binding.jms.common.DestinationDefinition, javax.jms.ConnectionFactory, java.util.Hashtable)
     */
    public Destination getDestination(DestinationDefinition definition,
                                      ConnectionFactory cf,
                                      Hashtable<String, String> env) {
        
        Connection connection = null;
        
        try {
            
            String name = definition.getName();
            connection = cf.createConnection();
            
            switch(definition.getDestinationType()) {
                case queue:
                    QueueConnection qc = (QueueConnection) connection;
                    return qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE).createQueue(name);
                case topic:
                    TopicConnection tc = (TopicConnection) connection;
                    return tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE).createQueue(name);
                default:
                    throw new IllegalArgumentException("Unknown destination type");
            }
            
        } catch(JMSException ex) {
            throw new SCA4JJmsException("Unable to create destination", ex);
        } finally {
            JmsHelper.closeQuietly(connection);
        }
        
    }

}
