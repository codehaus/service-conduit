/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.binding.jms.runtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.sca4j.binding.jms.common.CorrelationScheme;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.provision.PayloadType;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Message listener for service requests.
 * 
 * @version $Revison$ $Date: 2008-03-18 05:24:49 +0800 (Tue, 18 Mar 2008) $
 */
public class ResponseMessageListenerImpl implements ResponseMessageListener {

    private Map<String, ChainHolder> operations;

    /**
     * Correlation scheme.
     */
    private final CorrelationScheme correlationScheme;

    /**
     * Transaction type.
     */
    private final TransactionType transactionType;

    /**
     * @param chains map of operations to interceptor chains.
     * @param correlationScheme correlation scheme.
     * @param messageTypes the JMS message type used to enqueue service
     *            invocations keyed by operation name
     * @param transactionType the type of transaction
     * @param callbackUri the callback service uri
     */
    public ResponseMessageListenerImpl(Map<PhysicalOperationDefinition, InvocationChain> chains, CorrelationScheme correlationScheme, Map<String, PayloadType> messageTypes,
            TransactionType transactionType, String callbackUri) {
        this.operations = new HashMap<String, ChainHolder>();
        for (Entry<PhysicalOperationDefinition, InvocationChain> entry : chains.entrySet()) {
            String name = entry.getKey().getName();
            PayloadType type = messageTypes.get(name);
            if (type == null) {
                throw new IllegalArgumentException("No message type for operation: " + name);
            }
            this.operations.put(name, new ChainHolder(type, entry.getValue()));
        }
        this.correlationScheme = correlationScheme;
        this.transactionType = transactionType;
    }

    public void onMessage(Message request, Session responseSession, Destination responseDestination) throws JMSException {

        String opName = request.getStringProperty("scaOperationName");
        ChainHolder holder = getInterceptorHolder(opName);
        Interceptor interceptor = holder.getHeadInterceptor();
        PayloadType payloadType = holder.getType();
        Object payload = MessageHelper.getPayload(request, payloadType);
        if (payloadType != PayloadType.OBJECT) {
            payload = new Object[] { payload };
        }
        WorkContext workContext = new WorkContext();
        org.sca4j.spi.invocation.Message inMessage = new MessageImpl(payload, false, workContext);
        org.sca4j.spi.invocation.Message outMessage = interceptor.invoke(inMessage);

        Object responsePayload = outMessage.getBody();
        Message response = createMessage(responsePayload, responseSession, payloadType);

        switch (correlationScheme) {
            case RequestCorrelIDToCorrelID: 
                response.setJMSCorrelationID(request.getJMSCorrelationID());
                break;
            case RequestMsgIDToCorrelID: 
                response.setJMSCorrelationID(request.getJMSMessageID());
                break;
        }
        MessageProducer producer = responseSession.createProducer(responseDestination);
        producer.send(response);

        if (transactionType == TransactionType.LOCAL) {
            responseSession.commit();
        }

    }

    /*
     * Finds the matching interceptor holder.
     */
    private ChainHolder getInterceptorHolder(String opName) {

        if (operations.size() == 1) {
            return operations.values().iterator().next();
        } else if (opName != null && operations.containsKey(opName)) {
            return operations.get(opName);
        } else if (operations.containsKey("onMessage")) {
            return operations.get("onMessage");
        } else {
            throw new SCA4JJmsException("Unable to match operation on the service contract");
        }

    }

    private Message createMessage(Object payload, Session session, PayloadType payloadType) throws JMSException {
        switch (payloadType) {
        case STREAM:
            throw new UnsupportedOperationException("Stream message not yet supported");
        case TEXT:
            if (!(payload instanceof String)) {
                // this should not happen
                throw new IllegalArgumentException("Response payload is not a string: " + payload);
            }
            return session.createTextMessage((String) payload);
        case OBJECT:
            if (!(payload instanceof Serializable)) {
                // this should not happen
                throw new IllegalArgumentException("Response payload is not serializable: " + payload);
            }
            return session.createObjectMessage((Serializable) payload);
        default:
            return MessageHelper.createBytesMessage(session, payload, payloadType);
        }
    }

    private class ChainHolder {
        private PayloadType type;
        private InvocationChain chain;

        private ChainHolder(PayloadType type, InvocationChain chain) {
            this.type = type;
            this.chain = chain;
        }

        public PayloadType getType() {
            return type;
        }

        public Interceptor getHeadInterceptor() {
            return chain.getHeadInterceptor();
        }
    }

}
