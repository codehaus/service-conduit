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
package org.sca4j.binding.oracle.aq.scdl;

import javax.xml.namespace.QName;

import org.oasisopen.sca.Constants;
import org.sca4j.binding.oracle.aq.common.InitialState;
import org.sca4j.scdl.BindingDefinition;
import org.w3c.dom.Document;

/**
 * AQBinding Definition.
 */
public class AQBindingDefinition extends BindingDefinition {

	
	private static final long serialVersionUID = 2794070984101035255L;
	private static final QName BINDING_QNAME  =  new QName(Constants.SCA_NS, "binding.oracle.aq");    

    private final String destinationName;
    private final InitialState initialState;
    private final int consumerCount;
    private final long consumerDelay;
	private final String dataSourceKey;
    private final int delay;
    private final String correlationId;

    /**
     * Creates AQBindingDefinition.
     *
     * @param destinationName the destination name
     * @param initialState the initial state
     * @param dataSourceKey the data source key
     * @param consumerCount the consumer count
     * @param delay the delay
     * @param correlationId correlation id
     */
    public AQBindingDefinition(String destinationName, InitialState initialState, 
    		                   String dataSourceKey, int consumerCount, 
    		                   long consumerDelay, int delay,
    		                   String correlationId, 
    		                   Document documentKey) {
    	
        super(null, BINDING_QNAME, documentKey);
        
        this.destinationName = destinationName;
        this.initialState = initialState;
        this.dataSourceKey = dataSourceKey;
        this.consumerCount = consumerCount;
        this.consumerDelay = consumerDelay;
        this.delay = delay;
        this.correlationId = correlationId;
    }    

	/**
     * Gets the name of where the message will be enqueued.
     * @return destination
     */
    public String getDestinationName() {return destinationName;}

    /**
     * State the binding should be started in.
     * @return initialState}
     */
    public InitialState getInitialState() {return initialState;}

    /**
     * Gets the count of the number of consumers.
     * @return count
     */
    public int getConsumerCount() {return consumerCount;}
    
    /**
     * This is the delay related to the consumer,
     * it is different from the normal delay, as the normal
     * delay is related to the Physical Queue
     * @return Consumer Delay
     */
    public long getConsumerDelay() {return consumerDelay;}

    /**
     * Gets the DataSource Key.
     * @return dataSource Key
     */
    public String getDataSourceKey() {return dataSourceKey;}

    /**
     * Gets the timeout on dequeue.
     * @return Dequeue timeout.
     */
    public int getDelay() {return delay;}

    /**
     * Returns the correlation Id
     * @return correlation id
     */
    public String getCorrelationId() {return this.correlationId;}
}
