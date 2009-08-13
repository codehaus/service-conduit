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
package org.sca4j.binding.aq.runtime.transport;

import java.util.Map;
import java.util.Map.Entry;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.sca4j.binding.aq.common.SCA4JAQException;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Message listener for service requests.
 * @version $Revison$ $Date: 2008-03-04 15:33:37 +0000 (Tue, 04 Mar 2008) $
 */
public class OneWayMessageListener implements MessageListener {
   
    /* Operations available on the contract.*/
    private Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> opertaions;   

    /**
     * Constructor    
     * @param ops Map of operation definitions.      
     */
    public OneWayMessageListener(final Map<String, Entry<PhysicalOperationDefinition, InvocationChain>> ops) {        
        opertaions = ops;        
    }

    /**
     * When the message
     */
    public void onMessage(final Message request) {       
        try {
            handleInboundMessage(request);                     
        } catch (JMSException je) {
            throw new SCA4JAQException("Error on Reading Message", je);
        }
    }
    
    /**
     * Handle in bound Message
     * @param request
     * @return {@link org.sca4j.spi.invocation.Message}
     * @throws JMSException
     */
    private org.sca4j.spi.invocation.Message handleInboundMessage(final Message request) throws JMSException {
        final String opName = request.getStringProperty("scaOperationName");
        final Interceptor interceptor = getInterceptor(opName);

        final ObjectMessage objectMessage = (ObjectMessage) request;
        final Object[] payload = (Object[]) objectMessage.getObject();

        return invokeOnService(interceptor, payload);
    }

    /**
     * Invoke On Service     
     * @param interceptor
     * @param payload
     * @return
     */
    private org.sca4j.spi.invocation.Message invokeOnService(final Interceptor interceptor, final Object[] payload) {
        final org.sca4j.spi.invocation.Message inMessage = new MessageImpl(payload, false, new WorkContext());
        return interceptor.invoke(inMessage);
    }

    /*
     * Finds the matching intercepter.
     */
    private Interceptor getInterceptor(String opName) {

        if (opertaions.size() == 1) {
            return opertaions.values().iterator().next().getValue().getHeadInterceptor();
        } else if (opName != null && opertaions.containsKey(opName)) {
            return opertaions.get(opName).getValue().getHeadInterceptor();
        } else if (opertaions.containsKey("onMessage")) {
            return opertaions.get("onMessage").getValue().getHeadInterceptor();
        } else {
            throw new SCA4JAQException("Unable to match operation on the service contract");
        }
    }
}
