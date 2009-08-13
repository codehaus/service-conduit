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
