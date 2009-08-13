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
package org.sca4j.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;

import org.osoa.sca.Conversation;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceUnavailableException;

import org.sca4j.pojo.ConversationImpl;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.component.ConversationExpirationCallback;
import org.sca4j.spi.component.InstanceInvocationException;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.ConversationContext;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Dispatches from a proxy to a wire.
 *
 * @version $Rev: 3021 $ $Date: 2008-03-03 19:28:04 -0800 (Mon, 03 Mar 2008) $
 */
public final class JDKInvocationHandler<B> implements ConversationExpirationCallback, InvocationHandler, ServiceReference<B> {
    private final Class<B> businessInterface;
    private final B proxy;
    private final InteractionType type;
    private final Map<Method, InvocationChain> chains;
    private final ScopeContainer<Conversation> scopeContainer;

    private Conversation conversation;
    private Object userConversationId;
    private String callbackUri;

    /**
     * Creates a stateless wire proxy
     *
     * @param interfaze   the proxy interface
     * @param callbackUri the callback uri or null if the wire is unidirectional
     * @param mapping     the method to invocation chain mappings for the wire
     * @throws NoMethodForOperationException if an error occurs creating the proxy
     */
    public JDKInvocationHandler(Class<B> interfaze, String callbackUri, Map<Method, InvocationChain> mapping)
            throws NoMethodForOperationException {
        this(interfaze, InteractionType.STATELESS, callbackUri, mapping, null);
    }

    /**
     * Creates a wire proxy.
     *
     * @param interfaze      the proxy interface
     * @param type           the interaction style for the wire
     * @param callbackUri    the callback uri or null if the wire is unidirectional
     * @param mapping        the method to invocation chain mappings for the wire
     * @param scopeContainer the conversational scope container
     * @throws NoMethodForOperationException if an error occurs creating the proxy
     */
    public JDKInvocationHandler(Class<B> interfaze,
                                InteractionType type,
                                String callbackUri,
                                Map<Method, InvocationChain> mapping,
                                ScopeContainer<Conversation> scopeContainer) throws NoMethodForOperationException {
        this.callbackUri = callbackUri;
        assert mapping != null;
        this.businessInterface = interfaze;
        ClassLoader loader = interfaze.getClassLoader();
        this.proxy = interfaze.cast(Proxy.newProxyInstance(loader, new Class[]{interfaze}, this));
        this.chains = mapping;
        this.scopeContainer = scopeContainer;
        this.type = type;
    }


    public void expire(Conversation conversation) {
        this.conversation = null;
    }

    public B getService() {
        return proxy;
    }

    public ServiceReference<B> getServiceReference() {
        return this;
    }

    public boolean isConversational() {
        return type != InteractionType.STATELESS;
    }

    public Class<B> getBusinessInterface() {
        return businessInterface;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public Object getConversationID() {
        return userConversationId;
    }

    public void setConversationID(Object conversationId) throws IllegalStateException {
        if (conversation != null) {
            throw new IllegalStateException("A conversation is already active");
        }
        userConversationId = conversationId;
    }

    public Object getCallbackID() {
        throw new UnsupportedOperationException();
    }

    public void setCallbackID(Object callbackID) {
        throw new UnsupportedOperationException();
    }

    public Object getCallback() {
        throw new UnsupportedOperationException();
    }

    public void setCallback(Object callback) {
        throw new UnsupportedOperationException();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvocationChain chain = chains.get(method);
        if (chain == null) {
            return handleProxyMethod(method);
        }

        Interceptor headInterceptor = chain.getHeadInterceptor();
        assert headInterceptor != null;

        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        CallFrame frame = initalizeCallFrame(workContext);
        Message msg = new MessageImpl();
        msg.setBody(args);
        msg.setWorkContext(workContext);
        try {
            // dispatch the invocation down the chain and get the response
            Message resp;
            try {
                resp = headInterceptor.invoke(msg);
            } catch (ServiceUnavailableException e) {
                // simply rethrow ServiceUnavailableExceptions
                throw e;
            } catch (RuntimeException e) {
                // wrap other exceptions raised by the runtime
                throw new ServiceUnavailableException(e);
            }

            // handle response from the application, returning or throwing an exception as appropriate
            Object body = resp.getBody();
            if (resp.isFault()) {
                throw (Throwable) body;
            } else {
                return body;
            }
        } finally {
            if (InteractionType.CONVERSATIONAL == type || InteractionType.PROPAGATES_CONVERSATION == type) {
                PhysicalOperationDefinition operation = chain.getPhysicalOperation();
                if (operation.isEndsConversation()) {
                    conversation = null;
                }
            }
            if (frame != null) {
                // no callframe was created as the wire is unidrectional and non-conversational 
                workContext.popCallFrame();
            }
        }

    }

    /**
     * Initializes and returns a CallFrame for the invocation if it is required. A CallFrame is required if the wire is targeted to a conversational
     * service or is bidrectional (i.e. there is a callback). It is not required if the wire is targeted to a unidirectional, non-conversational
     * service. If not required, null is returned, thereby avoiding the overhead of creating and pushing a CallFrame onto the current WorkContext.
     *
     * @param workContext the current work context
     * @return a CallFrame for the invocation or null if none is required.
     */
    private CallFrame initalizeCallFrame(WorkContext workContext) {
        CallFrame frame = null;
        if (InteractionType.CONVERSATIONAL == type && conversation == null) {
            conversation = new ConversationImpl(createConversationID(), scopeContainer);
            // register this proxy to receive notifications when the conversation ends
            scopeContainer.registerCallback(conversation, this);
            // mark the CallFrame as starting a conversation
            frame = new CallFrame(callbackUri, null, conversation, ConversationContext.NEW);
            workContext.addCallFrame(frame);
        } else if (InteractionType.PROPAGATES_CONVERSATION == type && conversation == null) {
            Conversation propagated = workContext.peekCallFrame().getConversation();
            frame = new CallFrame(callbackUri, null, propagated, ConversationContext.PROPAGATE);
            workContext.addCallFrame(frame);
        } else if (InteractionType.CONVERSATIONAL == type) {
            frame = new CallFrame(callbackUri, null, conversation, null);
            workContext.addCallFrame(frame);
        } else if (callbackUri != null) {
            // the wire is bidrectional so a callframe is required
            frame = new CallFrame(callbackUri, null, null, null);
            workContext.addCallFrame(frame);
        }
        return frame;
    }

    /**
     * Creates a new conversational id
     *
     * @return the conversational id
     */
    private Object createConversationID() {
        if (userConversationId != null) {
            return userConversationId;
        } else {
            return UUID.randomUUID().toString();
        }
    }

    private Object handleProxyMethod(Method method) throws InstanceInvocationException {
        if (method.getParameterTypes().length == 0 && "toString".equals(method.getName())) {
            return "[Proxy - " + Integer.toHexString(hashCode()) + "]";
        } else if (method.getDeclaringClass().equals(Object.class)
                && "equals".equals(method.getName())) {
            // TODO implement
            throw new UnsupportedOperationException();
        } else if (Object.class.equals(method.getDeclaringClass())
                && "hashCode".equals(method.getName())) {
            return hashCode();
            // TODO beter hash algorithm
        }
        String op = method.getName();
        throw new InstanceInvocationException("Operation not configured: " + op, op);
    }
}
