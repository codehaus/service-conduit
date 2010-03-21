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
package org.sca4j.binding.oracle.aq.runtime.wire.interceptor;

import org.sca4j.binding.oracle.aq.common.AQException;
import org.sca4j.binding.oracle.aq.common.QueueDefinition;
import org.sca4j.binding.oracle.queue.spi.AQQueueException;
import org.sca4j.binding.oracle.queue.spi.envelope.DefaultEnvelope;
import org.sca4j.binding.oracle.queue.spi.envelope.Envelope;
import org.sca4j.binding.oracle.queue.spi.envelope.EnvelopeProperties;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;


/**
 * Interceptor dispatches the AQ Messages.
 */
public class AQTargetInterceptor  implements Interceptor{

    private Interceptor next;             
    private final QueueDefinition queueDefinition;
    private final String operationName;
    private final int opSize;
        
    /**
     * Initialise the {@link AQTargetInterceptor} by the given attributes.
     * 
     * @param queueManager the queue manager
     */
    public AQTargetInterceptor(QueueDefinition queueDefinition, String operationName, int opSize) {
        this.queueDefinition = queueDefinition;
        this.operationName = operationName;
        this.opSize = opSize;
    }

    /**
     * Sends the message onto the queue.
     *
     * @param message the message
     * @return the message
     */
    public Message invoke(Message message) {

        try {
             
            Object payload = getPayload(message);
            
            
            queueDefinition.getQueueManager().enqueue(queueDefinition.getQueueName(), 
                                                      queueDefinition.getCorrelationId(), queueDefinition.getDelay(), 
                                                      payload, queueDefinition.getDataSourceKey());

        } catch (AQQueueException ex) {
           throw new AQException("Error while trying to enque Message " + ex.getMessage(), ex);
        }
        return new MessageImpl();
    }       

    /**
     * Sets the Next Interceptor.
     * @param next the next
     */
    public void setNext(Interceptor next) {
        this.next = next;
    }

    /**
     * Gets the Next Interceptor.
     * @return the next
     */
    public Interceptor getNext() {
        return next;
    }
    
    /*
     * Gets the data from the Message
     */
    private Object getPayload(Message message) {        
       final Object[] data = (Object[]) message.getBody();      
       final Object payLoad;
       
       if(opSize > 1){
         payLoad = getEnvelope(data);       
       } else if(data.length == 1 && opSize == 1) {
           payLoad = data[0];
       }else {
           payLoad = data;
       }
       
       return payLoad;
    }
    
    /*
     * Creates the Envelope
     */
    private Envelope getEnvelope(Object data) {       
       Envelope envelope = new DefaultEnvelope(data);
       envelope.setHeader(EnvelopeProperties.SCA_OPNAME, operationName);
       return envelope;
    }
}
