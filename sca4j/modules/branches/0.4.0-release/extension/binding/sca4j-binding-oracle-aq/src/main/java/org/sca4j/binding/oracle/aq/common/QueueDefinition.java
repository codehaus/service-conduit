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
package org.sca4j.binding.oracle.aq.common;

import org.sca4j.binding.oracle.queue.spi.AQQueueManager;


/**
 * Provides Meta Information for Queues
 */
public class QueueDefinition {
    
    private final AQQueueManager queueManager;
    private final String queueName;
    private final String correlationId;
    private final String dataSourceKey;
    private final int delay;
    
    
    /**
     * Creates the Queue Definition by the following attributes 
     * @param queueManager
     * @param queueName
     * @param correlationId
     * @param dataSourceKey
     * @param delay
     */
    public QueueDefinition(AQQueueManager queueManager, String queueName, String correlationId, String dataSourceKey, int delay) {        
        this.queueManager = queueManager;
        this.queueName = queueName;
        this.correlationId = correlationId;
        this.dataSourceKey = dataSourceKey;
        this.delay = delay;
    }


    /**
     * Returns the QueueManager
     * @return
     */
    public AQQueueManager getQueueManager() {
        return queueManager;
    }

    /**
     * Return the Queue destination
     * @return
     */
    public String getQueueName() {
        return queueName;
    }


    /**
     * Return the Correlation Identifier
     * @return
     */
    public String getCorrelationId() {
        return correlationId;
    }


    /**
     * Returns the Data Source Key
     * @return
     */
    public String getDataSourceKey() {
        return dataSourceKey;
    }


    /**
     * Returns the Delay time for for Message to be dequeued
     * @return
     */
    public int getDelay() {
        return delay;
    }         
}
