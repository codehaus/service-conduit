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
import oracle.AQ.AQMessageProperty;
import oracle.AQ.AQQueue;
import oracle.AQ.AQSession;

import org.oasisopen.sca.ServiceUnavailableException;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.oracle.aq.provision.AQWireTargetDefinition;
import org.sca4j.binding.oracle.aq.scdl.AQBindingDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.resource.ResourceRegistry;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * The TargetWireAttacher impelementation of {@link TargetWireAttacher}.
 */
public class AQTargetWireAttacher implements TargetWireAttacher<AQWireTargetDefinition> {

    @Reference public ResourceRegistry resourceRegistry;
    
    @Monitor public AQMonitor monitor;

    /**
     * @see org.sca4j.spi.builder.component.TargetWireAttacher#attachToTarget(org.sca4j.spi.model.physical.PhysicalWireSourceDefinition, org.sca4j.spi.model.physical.PhysicalWireTargetDefinition, org.sca4j.spi.wire.Wire)
     */
    public void attachToTarget(PhysicalWireSourceDefinition sourceDefinition, AQWireTargetDefinition targetDefinition, Wire wire) throws WiringException {
        
        try {
            for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
                OperationMetadata operationMetadata = new OperationMetadata(entry.getKey().getTargetOperation(), entry.getValue());
                Interceptor interceptor =  new AQTargetInterceptor(operationMetadata, targetDefinition.bindingDefinition);
                entry.getValue().addInterceptor(interceptor);
            }
        } catch (ClassNotFoundException e) {
            throw new WiringException(e);
        } catch (JAXBException e) {
            throw new WiringException(e);
        }

    }

    /**
     * @see org.sca4j.spi.builder.component.TargetWireAttacher#createObjectFactory(org.sca4j.spi.model.physical.PhysicalWireTargetDefinition)
     */
    public ObjectFactory<?> createObjectFactory(AQWireTargetDefinition targetDefinition) throws WiringException {
        return null;
    }
    
    public class AQTargetInterceptor  implements Interceptor{

        private OperationMetadata operationMetadata;
        private AQBindingDefinition bindingDefinition;
        
        private JAXBContext jaxbContext;
        private Interceptor next; 
        
        public AQTargetInterceptor(OperationMetadata operationMetadata, AQBindingDefinition bindingDefinition) throws JAXBException {
            this.operationMetadata = operationMetadata;
            this.bindingDefinition = bindingDefinition;
            List<Class<?>> classes = new LinkedList<Class<?>>();
            classes.add(Envelope.class);
            classes.add(operationMetadata.getInputType());
            if (operationMetadata.getOutputType() != null) {
                classes.add(operationMetadata.getOutputType());
            }
            jaxbContext = JAXBContext.newInstance(classes.toArray(new Class<?>[classes.size()]));
        }

        /**
         * @see org.sca4j.spi.wire.Interceptor#invoke(org.sca4j.spi.invocation.Message)
         */
        public Message invoke(Message inputScaMessage) {
            
            Connection con = null;
            AQSession aqSession = null;
            AQQueue requestQueue = null;
            AQQueue responseQueue = null;
            
            try {
                
                DataSource dataSource = resourceRegistry.getResource(DataSource.class, bindingDefinition.dataSourceKey);
                con = dataSource.getConnection();
                aqSession = AQDriverManager.createAQSession(con);
                requestQueue = aqSession.getQueue(null, bindingDefinition.destinationName);
                
                AQMessage inputAqMessage = requestQueue.createMessage();
                AQMessageProperty aqMessageProperty = inputAqMessage.getMessageProperty();
                
                Envelope envelope = new Envelope();
                envelope.setOperationName(operationMetadata.getName());
                envelope.setBody(((Object[])inputScaMessage.getBody())[0]);
                
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jaxbContext.createMarshaller().marshal(envelope, byteArrayOutputStream);
                byte[] body = byteArrayOutputStream.toByteArray();
                
                inputAqMessage.getRawPayload().setStream(body, body.length);
                aqMessageProperty.setDelay(bindingDefinition.delay);
                if (bindingDefinition.correlationId != null) {
                    aqMessageProperty.setCorrelation(bindingDefinition.correlationId);
                }
                
                AQEnqueueOption enqueueOption = new AQEnqueueOption();
                if (operationMetadata.isTwoWay()) {
                    enqueueOption.setVisibility(AQEnqueueOption.VISIBILITY_IMMEDIATE);
                } else {
                    enqueueOption.setVisibility(AQEnqueueOption.VISIBILITY_ONCOMMIT);
                }
                requestQueue.enqueue(enqueueOption, inputAqMessage);
                
                if (operationMetadata.isTwoWay()) {
                    responseQueue = aqSession.getQueue(null, bindingDefinition.responseDestinationName);
                    AQDequeueOption aqDequeueOption = new AQDequeueOption();
                    aqDequeueOption.setCorrelation(new String(inputAqMessage.getMessageId()));
                    AQMessage outputAqMessage = responseQueue.dequeue(aqDequeueOption);
                    body = outputAqMessage.getRawPayload().getBytes();
                    envelope = (Envelope) jaxbContext.createUnmarshaller().unmarshal(new ByteArrayInputStream(body));
                    inputScaMessage.setBody(envelope.getBody());
                    return inputScaMessage;
                } else {
                    return null;
                }
                
            } catch (Exception e) {
                throw new ServiceUnavailableException(e);
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

        /**
         * @see org.sca4j.spi.wire.Interceptor#setNext(org.sca4j.spi.wire.Interceptor)
         */
        public void setNext(Interceptor next) {
            this.next = next;
        }

        /**
         * @see org.sca4j.spi.wire.Interceptor#getNext()
         */
        public Interceptor getNext() {
            return next;
        }
        
    }

}
