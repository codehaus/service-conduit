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
package org.sca4j.binding.oracle.aq.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import oracle.AQ.AQDequeueOption;
import oracle.AQ.AQDriverManager;
import oracle.AQ.AQEnqueueOption;
import oracle.AQ.AQMessage;
import oracle.AQ.AQOracleSQLException;
import oracle.AQ.AQQueue;
import oracle.AQ.AQSession;

import org.oasisopen.sca.ServiceUnavailableException;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.oracle.aq.provision.AQWireSourceDefinition;
import org.sca4j.binding.oracle.aq.scdl.AQBindingDefinition;
import org.sca4j.host.management.ManagedAttribute;
import org.sca4j.host.management.ManagementService;
import org.sca4j.host.management.ManagementUnit;
import org.sca4j.host.runtime.RuntimeLifecycle;
import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.resource.ResourceRegistry;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Listens for Messages on Queues.
 */
public class AQSourceWireAttacher implements SourceWireAttacher<AQWireSourceDefinition> {
	
	@Reference public WorkScheduler workScheduler;
	@Reference public TransactionHandler transactionHandler;
	@Reference public ResourceRegistry resourceRegistry;
	@Reference public RuntimeLifecycle runtimeLifecycle;
    @Reference(required = false) public ManagementService managementService;
	
	@Monitor public AQMonitor monitor;

	private static final String EXLUDED_OPS = "equals|hashCode|toString|wait|notify|notifyAll|getClass";
	private static final int DEQUEUE_TIMEOUT_CODE = 25228;
	
	/**
	 * {@inheritDoc}
	 */
	public void attachToSource(AQWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition targetDefinition, Wire wire) throws WiringException {
	    try {
    	    Map<String, OperationMetadata> operations = new HashMap<String, OperationMetadata>();
    	    for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
    	        // TODO Add more rigor here, this happens when the interface is inferred from the class
    	        if (EXLUDED_OPS.indexOf(entry.getKey().getTargetOperation().getName()) == -1) { 
        	        OperationMetadata operationMetadata = new OperationMetadata(entry.getKey().getTargetOperation(), entry.getValue());
        	        operations.put(operationMetadata.getName(), operationMetadata);
    	        }
    	    }
    	    List<ConsumerWorker> workers = new LinkedList<ConsumerWorker>();
    		for (int i = 0; i < sourceDefinition.bindingDefinition.consumerCount; i++) {
    			ConsumerWorker consumerWorker = new ConsumerWorker(sourceDefinition.bindingDefinition, operations);
    			workScheduler.scheduleWork(consumerWorker);
    			workers.add(consumerWorker);
    			monitor.generalMessage("Consumer provisioned on queue " + sourceDefinition.bindingDefinition.destinationName);
    		}
    		if (managementService != null) {
    		    ManagementUnitImpl managementUnitImpl = new ManagementUnitImpl(workers, sourceDefinition.bindingDefinition, operations);
                managementService.register(URI.create("/binding.aq/" + targetDefinition.getUri()), managementUnitImpl);
    		}
	    } catch (ClassNotFoundException e) {
	        throw new WiringException(e);
	    } catch (JAXBException e) {
            throw new WiringException(e);
        }
	}
	
	/**
     * {@inheritDoc}
     */
	public void detachFromSource(AQWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition targetDefinition, Wire wire) throws WiringException {
    }

	/**
     * {@inheritDoc}
     */
	public void attachObjectFactory(AQWireSourceDefinition sourceDefinition, ObjectFactory<?> factory, PhysicalWireTargetDefinition wireTargetDefinition) {
	}		
 
	/**
     * {@inheritDoc}
     */
    public void detachObjectFactory(AQWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition targetDefinition) {	
    }	
    
    public class ConsumerWorker extends DefaultPausableWork {
        
        private AQBindingDefinition definition;
        private Map<String, OperationMetadata> operations;

        private boolean exception;
        private JAXBContext jaxbContext;
        
        public ConsumerWorker(AQBindingDefinition definition, Map<String, OperationMetadata> operations) throws JAXBException {
            super(true);
            this.definition = definition;
            this.operations = operations;
            List<Class<?>> classes = new LinkedList<Class<?>>();
            classes.add(Envelope.class);
            for (OperationMetadata metadata : operations.values()) {
                classes.add(metadata.getInputType());
                if (metadata.getOutputType() != null) {
                    classes.add(metadata.getOutputType());
                }
            }
            jaxbContext = JAXBContext.newInstance(classes.toArray(new Class<?>[classes.size()]));
        }
        
        @Override
        protected void execute() {
            
            Connection con = null;
            AQSession aqSession = null;
            AQQueue requestQueue = null;
            AQQueue responseQueue = null;
            
            try {
                
                if (runtimeLifecycle.isShutdown()) {
                    return;
                }

                if (exception) {
                    exception = false;
                    Thread.sleep(definition.exceptionTimeout);
                }
                
                transactionHandler.begin();
                
                DataSource ds = resourceRegistry.getResource(DataSource.class, definition.dataSourceKey);
                con = ds.getConnection();
                aqSession = AQDriverManager.createAQSession(con);
                requestQueue = aqSession.getQueue(null, definition.destinationName);
                
                AQDequeueOption dequeueOption = new AQDequeueOption();
                dequeueOption.setWaitTime(definition.consumerDelay);
                AQMessage inputAqMessage = requestQueue.dequeue(dequeueOption);
                if (inputAqMessage == null) {
                    transactionHandler.commit();
                    return;
                }
                
                byte[] payload = inputAqMessage.getRawPayload().getBytes();
                
                Envelope envelope = (Envelope) jaxbContext.createUnmarshaller().unmarshal(new ByteArrayInputStream(payload));
                String operationName = envelope.getOperationName();
                Object body = envelope.getBody();
                
                OperationMetadata operationMetadata = operations.get(operationName);
                if (operationMetadata == null) {
                    throw new ServiceUnavailableException("Requested operation not found on service " + operationName);
                }
                Interceptor interceptor = operationMetadata.getInvocationChain().getHeadInterceptor();
                Message inputScaMessage = new MessageImpl(new Object[] {body}, false, new WorkContext());
                Message outputScaMessage = interceptor.invoke(inputScaMessage);
                
                if (operationMetadata.isTwoWay()) {
                    Object result = outputScaMessage.getBody();
                    envelope.setBody(result);
                    responseQueue = aqSession.getQueue(null, definition.responseDestinationName);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    AQMessage outputAqMessage = responseQueue.createMessage();
                    jaxbContext.createMarshaller().marshal(envelope, byteArrayOutputStream);
                    payload = byteArrayOutputStream.toByteArray();
                    outputAqMessage.getRawPayload().setStream(payload, payload.length);
                    outputAqMessage.getMessageProperty().setCorrelation(new String(inputAqMessage.getMessageId()));
                    responseQueue.enqueue(new AQEnqueueOption(), outputAqMessage);
                }
                
                transactionHandler.commit();
                
            } catch (AQOracleSQLException e) {
                if (DEQUEUE_TIMEOUT_CODE != e.getErrorCode()) {
                    reportException(e);
                }
                try {
                    transactionHandler.rollback();
                } catch (Exception e1) {
                    reportException(e);
                }
            } catch (Exception e) {
                reportException(e);
                try {
                    transactionHandler.rollback();
                } catch (Exception e1) {
                    reportException(e);
                }
            } finally {
                if (requestQueue != null) {
                    requestQueue.close();
                }
                if (responseQueue != null) {
                    responseQueue.close();
                }
                if (aqSession != null) {
                    aqSession.close();
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    reportException(e);
                }
            }
            
        }

        private void reportException(Exception e) {
            if (!runtimeLifecycle.isShutdown()) {
                monitor.onException(e.getMessage(), e);
            }
        }
        
    }
    
    public class ManagementUnitImpl implements ManagementUnit {
        
        private List<ConsumerWorker> consumerWorkers;
        private boolean started = true;
        private AQBindingDefinition bindingDefinition;
        private Map<String, OperationMetadata> operations;
        
        public ManagementUnitImpl(List<ConsumerWorker> consumerWorkers, AQBindingDefinition bindingDefinition, Map<String, OperationMetadata> operations) {
            this.consumerWorkers = consumerWorkers;
            this.bindingDefinition = bindingDefinition;
            this.operations = operations;
        }

        @Override
        public String getDescription() {
            return "AQ service endpoint";
        }
        
        @ManagedAttribute("Blocking wait on the queue (seconds)")
        public int getPollingInterval() {
            return bindingDefinition.consumerDelay;
        }
        
        public void setPollingInterval(int pollingInterval) {
            bindingDefinition.consumerDelay = pollingInterval;
        }
        
        @ManagedAttribute("Exception timeout in seconds")
        public long getExceptionTimeout() {
            return bindingDefinition.exceptionTimeout;
        }
        
        public void setExceptionTimeout(long exceptionTimeout) {
            bindingDefinition.exceptionTimeout = exceptionTimeout;
        }
        
        @ManagedAttribute("Number of workers in the pool")
        public int getSize() {
            return consumerWorkers.size();
        }
        
        public synchronized void setSize(int size) throws InterruptedException, JAXBException {
            int currentSize = consumerWorkers.size();
            if (currentSize == size) {
                return;
            }
            if (currentSize < size) {
                for (int i = 0;i < size - currentSize;i++) {
                    ConsumerWorker consumerWorker = new ConsumerWorker(bindingDefinition, operations);
                    if (started) {
                        workScheduler.scheduleWork(consumerWorker);
                    }
                }
            } else if (currentSize > size) {
                List<ConsumerWorker> removedWorkers = new LinkedList<ConsumerWorker>();
                for (int i = 0;i < currentSize - size;i++) {
                    ConsumerWorker consumerWorker = consumerWorkers.get(i);
                    consumerWorker.start(false);
                    removedWorkers.add(consumerWorker);
                }
                consumerWorkers.removeAll(removedWorkers);
            }
        }
        
        @ManagedAttribute("Whether the endpoint is active")
        public boolean isStarted() {
            return started;
        }
        
        public synchronized void setStarted(boolean started) throws InterruptedException {
            if (this.started && !started) {
                for (ConsumerWorker consumerWorker : consumerWorkers) {
                    consumerWorker.start(false);
                }
                this.started = false;
            } else if (!this.started && started) {
                for (ConsumerWorker consumerWorker : consumerWorkers) {
                    consumerWorker.start(true);
                    workScheduler.scheduleWork(consumerWorker);
                }
                this.started = true;
            }
        }
        
    }
    
}
