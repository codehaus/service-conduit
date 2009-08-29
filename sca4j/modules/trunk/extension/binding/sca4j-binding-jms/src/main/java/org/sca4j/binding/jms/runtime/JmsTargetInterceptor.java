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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.sca4j.binding.jms.common.CorrelationScheme;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.provision.PayloadType;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;

/**
 * Dispatches a service invocation to a JMS queue.
 * 
 * @version $Revision: 5322 $ $Date: 2008-09-02 20:15:34 +0100 (Tue, 02 Sep
 *          2008) $
 */
public class JmsTargetInterceptor implements Interceptor {

    /**
     * Next interceptor in the chain.
     */
    private Interceptor next;

    /**
     * Method name
     */
    private String methodName;

    private PayloadType payloadType;
    /**
     * Request destination.
     */
    private Destination destination;

    /**
     * Request connection factory.
     */
    private ConnectionFactory connectionFactory;

    /**
     * Correlation scheme.
     */
    private CorrelationScheme correlationScheme;

    /**
     * Message receiver.
     */
    private SCA4JMessageReceiver messageReceiver;

    /**
     * Classloader to use.
     */
    private ClassLoader cl;

    /**
     * @param methodName Method name.
     * @param payloadType the type of JMS message to send
     * @param destination Request destination.
     * @param connectionFactory Request connection factory.
     * @param correlationScheme Correlation scheme.
     * @param messageReceiver Message receiver for response.
     * @param cl the classloader for loading parameter types.
     */
    public JmsTargetInterceptor(String methodName, PayloadType payloadType, Destination destination, ConnectionFactory connectionFactory, CorrelationScheme correlationScheme,
            SCA4JMessageReceiver messageReceiver, ClassLoader cl) {
        this.methodName = methodName;
        this.payloadType = payloadType;
        this.destination = destination;
        this.connectionFactory = connectionFactory;
        this.correlationScheme = correlationScheme;
        this.messageReceiver = messageReceiver;
        this.cl = cl;
    }

    public Message invoke(Message message) {

        Message response = new MessageImpl();

        Connection connection = null;
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

            MessageProducer producer = session.createProducer(destination);
            Object[] payload = (Object[]) message.getBody();
            // payload = attachFramesToTail(payload,
            // message.getWorkContext().getCallFrameStack());

            javax.jms.Message jmsMessage = createMessage(session, payload);
            jmsMessage.setObjectProperty("scaOperationName", methodName);

            // CallFrame previous = message.getWorkContext().peekCallFrame();
            // if( previous.getConversation()!=null){
            // Object conversationID =
            // previous.getConversation().getConversationID();
            // if(conversationID != null){
            // jmsMessage.setStringProperty("scaConversationId",
            // String.valueOf(conversationID));
            // }
            // ConversationContext conversationContext =
            // previous.getConversationContext();
            // if(ConversationContext.NEW.equals(conversationContext)){
            // jmsMessage.setStringProperty("scaConversationStart",
            // String.valueOf(conversationID));
            // }
            // }
            producer.send(jmsMessage);

            String correlationId = null;
            switch (correlationScheme) {
            case None:
            case RequestCorrelIDToCorrelID:
                throw new UnsupportedOperationException("Correlation scheme not supported");
            case RequestMsgIDToCorrelID:
                correlationId = jmsMessage.getJMSMessageID();
            }
            session.commit();
            if (messageReceiver != null) {
                javax.jms.Message resultMessage = messageReceiver.receive(correlationId);
                Object responseMessage = MessageHelper.getPayload(resultMessage, payloadType);
                response.setBody(responseMessage);
            }

            return response;

        } catch (JMSException ex) {
            throw new SCA4JJmsException("Unable to receive response", ex);
        } finally {
            JmsHelper.closeQuietly(connection);
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }

    public Interceptor getNext() {
        return next;
    }

    public void setNext(Interceptor next) {
        this.next = next;
    }

    private javax.jms.Message createMessage(Session session, Object[] payload) throws JMSException {
        switch (payloadType) {
        case OBJECT:
            return session.createObjectMessage(payload);
        case STREAM:
            throw new UnsupportedOperationException("Not yet implemented");
        case TEXT:
            if (payload.length != 1) {
                throw new UnsupportedOperationException("Only single parameter operations are supported");
            }
            return session.createTextMessage((String) payload[0]);
        default:
            if (payload.length != 1) {
                throw new AssertionError("Bytes messages must have a single parameter");
            }
            return MessageHelper.createBytesMessage(session, payload[0], payloadType);
        }
    }

}
