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
package org.sca4j.binding.oracle.aq.provision;

import java.net.URI;

import org.sca4j.binding.oracle.aq.common.InitialState;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;



/**
 * Target definition that is used by the AQSourceWireAttacher.
 */
public class AQWireSourceDefinition extends PhysicalWireSourceDefinition {
   

    private final String destination;
    private final InitialState initialState;
    private final int consumerCount;
    private final String dataSourceKey;
    private final URI classLoaderId;
    private final int delay;
    private final String correlationId;
    


    /**
     * Create a AQWireSoureDefinition.
     *
     * @param destination the destination
     * @param initialState the initial state
     * @param consumerCount the consumer count
     * @param dataSourceKey the data source key
     * @param classLoaderId the class loader id
     * @param delay the delay
     * @param correlationId correlation id
     */
    public AQWireSourceDefinition(String destination,
                                  InitialState initialState,
                                  String dataSourceKey,
                                  int consumerCount,
                                  URI classLoaderId,
                                  int delay,
                                  String correlationId) {
        this.destination = destination;
        this.initialState = initialState;
        this.dataSourceKey = dataSourceKey;
        this.consumerCount = consumerCount;
        this.classLoaderId = classLoaderId;
        this.delay = delay;
        this.correlationId = correlationId;
    }


    /**
     * return the Destination.
     *
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }


    /**
     * State the Binding should be started in.
     *
     * @return the initial state
     */
    public InitialState getInitialState() {
        return initialState;
    }

    /**
     * return data source key.
     *
     * @return the data source key
     */
    public String getDataSourceKey() {
        return dataSourceKey;
    }

    /**
     * The count of the number of consumers to be used.
     *
     * @return the consumer count
     */
    public int getConsumerCount() {
        return consumerCount;
    }

    /**
     * return the URI of the classloader.
     *
     * @return the class loader id
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }


    /**
     * Gets the timeout on dequeue.
     *
     * @return Dequeue timeout.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Returns the correlation Id.
     * @return correlation id
     */
    public String getCorrelationId() {
    	return this.correlationId;
    }
}
