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
package org.sca4j.binding.aq.runtime.host;

import java.net.URI;

import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.jms.XAQueueConnectionFactory;

import org.sca4j.binding.aq.runtime.tx.TransactionHandler;

/**
 * @version $Revision: 4817 $ $Date: 2008-06-11 20:01:35 +0100 (Wed, 11 Jun 2008) $
 */
public interface AQHost {
    
    
    /**
     * Register Listeners to start consuming messages
     * @param connectionFactory
     * @param destination
     * @param listener
     * @param transactionHandler
     * @param classloader
     */
    void registerListener(XAQueueConnectionFactory connectionFactory,
                          Destination destination,                                              
                          MessageListener listener,
                          TransactionHandler transactionHandler,
                          ClassLoader classloader,
                          URI namespace, int receiverCount);
    
    
    /**
     * Unregisters the listeners for this service
     * @param serviceName
     */
    void unRegisterListener(URI serviceName);

}
