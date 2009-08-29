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
package org.sca4j.spi.invocation;

import java.io.Serializable;

import org.osoa.sca.Conversation;

/**
 * Encapsulates information for a specific invocation that is made as part of a request entering the domain. Requests may have multiple associated
 * invocations as component implementations may invoke services on other components as a request is processed.
 *
 * @version $Revision$ $Date$
 */
public class CallFrame implements Serializable {
    /**
     * A frame for stateless, unidirectional invocations which can be used to avoid new object allocation
     */
    public static final CallFrame STATELESS_FRAME = new CallFrame();

    private static final long serialVersionUID = -6108279393891496098L;

    private String callbackUri;
    private Object correlationId;
    private ConversationContext conversationContext;
    private Conversation conversation;

    /**
     * Default constructor. Creates a CallFrame for an invocation on a stateless, unidirectional service.
     */
    public CallFrame() {
    }

    /**
     * Creates a CallFrame for an invocation to a stateful unidirectional service
     *
     * @param correlationId the correlation id
     */
    public CallFrame(Object correlationId) {
        this(null, correlationId, null, null);
    }

    /**
     * Constructor. Creates a CallFrame for an invocation to a stateful bidirectional service.
     *
     * @param callbackUri         the URI the caller of the current service can be called back on
     * @param correlationId       the key used to correlate the forward invocation with the target component implementation instance. For stateless
     *                            targets, the id may be null.
     * @param conversation        the conversaation associated with the invocation or null
     * @param conversationContext the type of conversational context
     */
    public CallFrame(String callbackUri, Object correlationId, Conversation conversation, ConversationContext conversationContext) {
        this.callbackUri = callbackUri;
        this.correlationId = correlationId;
        this.conversation = conversation;
        this.conversationContext = conversationContext;
    }

    /**
     * Returns the URI of the callback service for the current invocation.
     *
     * @return the callback service URI or null if the invocation is to a unidirectional service.
     */
    public String getCallbackUri() {
        return callbackUri;
    }

    /**
     * Returns the key used to correlate the forward invocation with the target component implementation instance or null if the target is stateless.
     *
     * @param type the correlation id type.
     * @return the correlation id or null.
     */
    public <T> T getCorrelationId(Class<T> type) {
        return type.cast(correlationId);
    }

    /**
     * Returns the conversation associated with this CallFrame or null if the invocation is non-conversational.
     *
     * @return the conversation associated with this CallFrame or null if the invocation is non-conversational
     */
    public Conversation getConversation() {
        return conversation;
    }

    public ConversationContext getConversationContext() {
        return conversationContext;
    }

    /**
     * Performs a deep copy of the CallFrame.
     *
     * @return the copied frame
     */
    public CallFrame copy() {
        // data is immutable, return shallow copy
        return new CallFrame(callbackUri, correlationId, conversation, conversationContext);
    }

    public String toString() {
        StringBuilder s =
                new StringBuilder().append("CallFrame [Callback URI: ").append(callbackUri).append(" Correlation ID: ").append(correlationId);
        if (conversation != null) {
            s.append(" Conversation ID:").append(conversation.getConversationID());
            switch (conversationContext) {
            case PROPAGATE:
                s.append(" Propagate conversation");
                break;
            case NEW:
                s.append(" New conversation");
                break;
            }
        }
        return s.append("]").toString();
    }
}
