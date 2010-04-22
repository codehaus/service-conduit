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
package org.sca4j.binding.oracle.aq.runtime.listener;

import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.sca4j.binding.oracle.aq.common.QueueDefinition;
import org.sca4j.binding.oracle.aq.runtime.monitor.AQMonitor;
import org.sca4j.binding.oracle.queue.spi.AQQueueException;
import org.sca4j.binding.oracle.queue.spi.QueuePayload;
import org.sca4j.binding.oracle.queue.spi.envelope.Envelope;
import org.sca4j.binding.oracle.queue.spi.envelope.EnvelopeProperties;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Listens for Incoming Messages
 */
public class OneWayMessageListener implements MessageListener {

    private final Map<String, Map.Entry<PhysicalOperationPair, InvocationChain>> ops;
    private final QueueDefinition queueDefinition;
    private final AQMonitor monitor;

    /**
     * Initialise by the given operations
     * 
     * @param ops
     * @param QueueDefinition
     */
    public OneWayMessageListener(Map<String, Map.Entry<PhysicalOperationPair, InvocationChain>> ops, QueueDefinition queueDefinition,
                                 AQMonitor monitor) {
        this.ops = ops;
        this.queueDefinition = queueDefinition;
        this.monitor = monitor;
    }

    /**
     * Handle the Message
     */
    public void onMessage() throws MessageServiceException  {
        
        try {
            QueuePayload receivedMessage = queueDefinition.getQueueManager().dequeue(queueDefinition.getQueueName(),
                                                                               queueDefinition.getCorrelationId(), 
                                                                               queueDefinition.getDelay(), 
                                                                               queueDefinition.getDataSourceKey());
            Object payload = receivedMessage.getPayload();
            
			if (payload != null) {
				handleInboundMessage(payload);
			}
            
        } catch (AQQueueException sq) {
            monitor.onException(" Please CHECK QUEUES " + sq.getMessage(), ExceptionUtils.getFullStackTrace(sq));
            throw new MessageListenerException();
        }      
    }

    /*
     * Gets the valid intercepter
     */
    private void handleInboundMessage(Object payloadData) throws MessageServiceException {
        if (payloadData instanceof Envelope) {
            handleOnEnvelope((Envelope) payloadData);
        } else {
            handleOnRaw(payloadData, ops.keySet().iterator().next());
        }
    }

    /*
     * Handles the Message Data that is envelope wrapped
     */
   private void handleOnEnvelope(Envelope payload) throws MessageServiceException {
        final String opName = payload.getHeaderValue(EnvelopeProperties.SCA_OPNAME);
        final Object payloadData = payload.getPayload();
        invokeOnService(opName, payloadData);
    }

    /*
     * Handles a raw Message basically a message without an evnvelope
     */
    private void handleOnRaw(Object payloadData, String opName) throws MessageServiceException {

        if (!(payloadData instanceof Object[])) {
            payloadData = new Object[] { payloadData };
        }
        invokeOnService(opName, payloadData);
    }

    /*
     * Invokes on the service
     */
    private void invokeOnService(final String opName, final Object payload) throws MessageServiceException {
        Interceptor interceptor = getInterceptor(opName);
        Message inMessage = new MessageImpl(payload, false, new WorkContext());
        Message outMessage = interceptor.invoke(inMessage);
        if (outMessage.isFault()) {
            handleFault(outMessage);
        }
    }

    /*
     * Finds the correct Interceptor
     */
    private Interceptor getInterceptor(String opName) {

        if (ops.size() == 1) {
            return ops.values().iterator().next().getValue().getHeadInterceptor();
        } else if (opName != null && ops.containsKey(opName)) {
            return ops.get(opName).getValue().getHeadInterceptor();
        } else {
            throw new IllegalStateException("Unable to match operation on the service contract");
        }
    }

    /*
     * handles the Fault from the out Message
     */
    private void handleFault(Message outMessage) throws MessageServiceException {
        final Throwable fault = (Throwable) outMessage.getBody();
        monitor.onException("There is a Fault in the underlying service : The Fault is :-  "+fault.getMessage(), ExceptionUtils.getFullStackTrace(fault));
        throw new MessageServiceException();
    }
    
}
