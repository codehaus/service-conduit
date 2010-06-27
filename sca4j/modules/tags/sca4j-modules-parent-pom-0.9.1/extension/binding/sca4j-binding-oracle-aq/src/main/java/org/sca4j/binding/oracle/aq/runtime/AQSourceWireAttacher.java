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
import oracle.AQ.AQQueue;
import oracle.AQ.AQSession;

import org.oasisopen.sca.ServiceUnavailableException;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.oracle.aq.provision.AQWireSourceDefinition;
import org.sca4j.binding.oracle.aq.scdl.AQBindingDefinition;
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
	
	@Monitor public AQMonitor monitor;
	private List<ConsumerWorker> workers = new LinkedList<ConsumerWorker>();

	private static final String EXLUDED_OPS = "equals|hashCode|toString|wait|notify|notifyAll|getClass";
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
    		for (int i = 0; i < sourceDefinition.bindingDefinition.consumerCount; i++) {
    			ConsumerWorker consumerWorker = new ConsumerWorker(sourceDefinition.bindingDefinition, operations);
    			workScheduler.scheduleWork(consumerWorker);
    			workers.add(consumerWorker);
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

                if (exception) {
                    exception = false;
                    Thread.sleep(definition.exceptionTimeout);
                }
                
                Thread.sleep(definition.consumerDelay);
                
                if (!active.get()) {
                    return;
                }
                
                transactionHandler.begin();
                
                DataSource ds = resourceRegistry.getResource(DataSource.class, definition.dataSourceKey);
                con = ds.getConnection();
                aqSession = AQDriverManager.createAQSession(con);
                requestQueue = aqSession.getQueue(null, definition.destinationName);
                
                AQDequeueOption dequeueOption = new AQDequeueOption();
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
                
            } catch (Exception e) {
                monitor.onException(e.getMessage(), e);
                try {
                    transactionHandler.rollback();
                } catch (Exception e1) {
                    monitor.onException(e1.getMessage(), e1);
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
                    monitor.onException(e.getMessage(), e);
                }
            }
            
        }
        
    }
    
}
