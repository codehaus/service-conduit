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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.oracle.aq.common.QueueDefinition;
import org.sca4j.binding.oracle.aq.provision.AQWireSourceDefinition;
import org.sca4j.binding.oracle.aq.runtime.listener.MessageListener;
import org.sca4j.binding.oracle.aq.runtime.listener.OneWayMessageListener;
import org.sca4j.binding.oracle.aq.runtime.monitor.AQMonitor;
import org.sca4j.binding.oracle.aq.runtime.transaction.TransactionHandler;
import org.sca4j.binding.oracle.queue.spi.AQQueueManager;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Listens for Messages on Queues.
 */
public class AQSourceWireAttacher implements SourceWireAttacher<AQWireSourceDefinition> {
	
	private final WorkScheduler workScheduler;
	private final  TransactionHandler transactionHandler;
	private final AQQueueManager queueManager;
	private final List<ConsumerWorker> consumers;
	private AQMonitor monitor;

	/**
	 * Initializes the required references.
	 *
	 * @param workScheduler Work scheduler.
	 * @param transactionHandler {@link TransactionHandler}
	 * @param queueManager Queue manager.
	 */
	public AQSourceWireAttacher(@Reference WorkScheduler workScheduler,
			                    @Reference TransactionHandler transactionHandler,			                
			                    @Reference AQQueueManager queueManager) {
		this.workScheduler = workScheduler;
		this.transactionHandler = transactionHandler;
		this.queueManager = queueManager;
		consumers = new LinkedList<ConsumerWorker>();
	}

	/**
	 * {@inheritDoc}
	 */

	public void attachToSource(AQWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition targetDefinition, Wire wire) throws WiringException {

	    Map<String, Map.Entry<PhysicalOperationPair, InvocationChain>> ops = getWireOpertaions(wire);
	    int consumerCount = sourceDefinition.getConsumerCount();
		QueueDefinition queueDefinition = new QueueDefinition(queueManager, sourceDefinition.getDestination(),
		                                                      sourceDefinition.getCorrelationId(),
		                                                      sourceDefinition.getDataSourceKey(),
		                                                      sourceDefinition.getDelay());

		MessageListener messageListener = new OneWayMessageListener(ops, queueDefinition, monitor);
		ClassLoader classLoader = getClass().getClassLoader();


		for (int i = 0; i < consumerCount; i++) {
			ConsumerWorker consumerWorker = new ConsumerWorker(messageListener, sourceDefinition.getConsumerDelay(),
					                                           classLoader, transactionHandler, 
					                                           monitor);

			workScheduler.scheduleWork(consumerWorker);
			consumers.add(consumerWorker);
		}
	}
	
	/**
     * Not Used
     */
	public void detachFromSource(AQWireSourceDefinition sourceDefinition, 
                                 PhysicalWireTargetDefinition targetDefinition, Wire wire) throws WiringException{
		throw new AssertionError();		
    }

	/**
	 * Not Used
	 */
	public void attachObjectFactory(AQWireSourceDefinition sourceDefinition, ObjectFactory<?> factory, PhysicalWireTargetDefinition wireTargetDefinition) throws WiringException {
		throw new AssertionError();
	}		
 
	/**
	 * Not Used
	 */
    public void detachObjectFactory(AQWireSourceDefinition sourceDefinition, 
                                    PhysicalWireTargetDefinition targetDefinition)throws WiringException {	
       throw new AssertionError();
    }	   
	
	/**
	 * Stops the processing.
	 */
	@Destroy
	public void destroy() {
		monitor.stopConsumption("Stopping Processing");
		for (ConsumerWorker worker : consumers) {
			worker.stop();
		}
	}

	/**
	 * Injects the monitor.
	 *
	 * @param monitor the monitor
	 */
	@Monitor
	public void setMonitor(AQMonitor monitor) {
		this.monitor = monitor;
	}

	  /*
     * Gets the operational Methods for the service
     */
    private Map<String, Map.Entry<PhysicalOperationPair, InvocationChain>> getWireOpertaions(Wire wire) {
        Map<String, Map.Entry<PhysicalOperationPair, InvocationChain>> operations = new HashMap<String, Map.Entry<PhysicalOperationPair, InvocationChain>>();

        /* Get the operation names */
        for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            operations.put(entry.getKey().getSourceOperation().getName(), entry);
        }
        return operations;
    }	
}
