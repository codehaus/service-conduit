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
package org.sca4j.binding.oracle.aq.runtime.wire;

import java.util.Map;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.oracle.aq.common.QueueDefinition;
import org.sca4j.binding.oracle.aq.provision.AQWireTargetDefinition;
import org.sca4j.binding.oracle.aq.runtime.wire.interceptor.AQTargetInterceptor;
import org.sca4j.binding.oracle.queue.spi.AQQueueManager;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * The TargetWireAttacher impelementation of {@link TargetWireAttacher}.
 */
public class AQTargetWireAttacher implements TargetWireAttacher<AQWireTargetDefinition> {

    private final AQQueueManager queueManager;

    /**
     * Initializes the datasource registry.
     *
     * @param queueManager the queue manager
     */
    public AQTargetWireAttacher(@Reference AQQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    /**
     * Dispatches the message to AQ.
     *
     * @param sourceDefinition the source definition
     * @param targetDefinition the target definition
     * @param wire the wire
     *
     * @throws WiringException the wiring exception
     */
    public void attachToTarget(PhysicalWireSourceDefinition sourceDefinition, AQWireTargetDefinition targetDefinition, 
                               Wire wire) throws WiringException {
        final int numberOfOperations = wire.getInvocationChains().size();
        QueueDefinition queueDefinition = new QueueDefinition(queueManager, targetDefinition.getDestination(),
                                                              targetDefinition.getCorrelationId(),
                                                              targetDefinition.getDataSourceKey(), 
                                                              0);
        
        
        for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            PhysicalOperationDefinition op = entry.getKey().getSourceOperation();
            InvocationChain chain = entry.getValue();            
            Interceptor interceptor =  new AQTargetInterceptor(queueDefinition, op.getName(), numberOfOperations);             
            chain.addInterceptor(interceptor);
        }        


    }

    /**
     * Not used.
     */
    public ObjectFactory<?> createObjectFactory(AQWireTargetDefinition targetDefinition) throws WiringException {
    	throw new AssertionError();
    }

    /**
     * Not Used 
     */
	public void detachFromTarget(PhysicalWireSourceDefinition wireSource, AQWireTargetDefinition targetDefinition) throws WiringException {
		throw new AssertionError();		
	}

}
