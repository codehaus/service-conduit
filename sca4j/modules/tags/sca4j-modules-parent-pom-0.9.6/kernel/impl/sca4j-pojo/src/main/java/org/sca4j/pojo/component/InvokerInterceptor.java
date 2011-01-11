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
package org.sca4j.pojo.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.sca4j.api.scope.ConversationEndedException;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.ExpirationPolicy;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.ConversationContext;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationRuntimeException;

/**
 * Responsible for dispatching an invocation to a Java-based component implementation instance.
 *
 * @version $Rev: 5244 $ $Date: 2008-08-20 22:21:13 +0100 (Wed, 20 Aug 2008) $
 * @param <T> the implementation class for the component being invoked
 * @param <CONTEXT> the type of context id used by the ScopeContainer
 */
public class InvokerInterceptor<T, CONTEXT> implements Interceptor {
    private Method operation;
    private AtomicComponent<T> component;
    private ScopeContainer<CONTEXT> scopeContainer;
    private ClassLoader targetTCCLClassLoader;
    private boolean callback;
    private boolean endConversation;
    private boolean conversationScope;

    /**
     * Creates a new interceptor instance.
     *
     * @param operation       the method to invoke on the target instance
     * @param callback        true if the operation is a callback
     * @param endConversation if true, ends the conversation after the invocation
     * @param component       the target component
     * @param scopeContainer  the ScopeContainer that manages implementation instances for the target component
     */
    public InvokerInterceptor(Method operation,
                              boolean callback,
                              boolean endConversation,
                              AtomicComponent<T> component,
                              ScopeContainer<CONTEXT> scopeContainer) {
        this.operation = operation;
        this.callback = callback;
        this.endConversation = endConversation;
        this.component = component;
        this.scopeContainer = scopeContainer;
        conversationScope = Scope.CONVERSATION.equals(scopeContainer.getScope());
    }

    /**
     * Creates a new interceptor instance that sets the TCCL to the given classloader before dispatching an invocation.
     *
     * @param operation             the method to invoke on the target instance
     * @param callback              true if the operation is a callback
     * @param endConversation       if true, ends the conversation after the invocation
     * @param component             the target component
     * @param scopeContainer        the ScopeContainer that manages implementation instances for the target component
     * @param targetTCCLClassLoader the classloader to set the TCCL to before dispatching.
     */
    public InvokerInterceptor(Method operation,
                              boolean callback,
                              boolean endConversation,
                              AtomicComponent<T> component,
                              ScopeContainer<CONTEXT> scopeContainer,
                              ClassLoader targetTCCLClassLoader) {
        this.operation = operation;
        this.callback = callback;
        this.endConversation = endConversation;
        this.component = component;
        this.scopeContainer = scopeContainer;
        this.targetTCCLClassLoader = targetTCCLClassLoader;
        conversationScope = Scope.CONVERSATION.equals(scopeContainer.getScope());
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an target interceptor chain");
    }

    public Interceptor getNext() {
        return null;
    }

    public boolean isOptimizable() {
        return true;
    }

    public Message invoke(Message msg) {
        WorkContext workContext = msg.getWorkContext();
        InstanceWrapper<T> wrapper;
        try {
            startOrJoinContext(workContext);
            wrapper = scopeContainer.getWrapper(component, workContext);
        } catch (ConversationEndedException e) {
            msg.setBodyWithFault(e);
            return msg;
        } catch (InstanceLifecycleException e) {
            throw new InvocationRuntimeException(e);
        }

        try {
            Object instance = wrapper.getInstance();
            return invoke(msg, workContext, instance);
        } finally {
            try {
                scopeContainer.returnWrapper(component, workContext, wrapper);
                if (conversationScope && endConversation) {
                    scopeContainer.stopContext(workContext);
                }
            } catch (InstanceDestructionException e) {
                throw new InvocationRuntimeException(e);
            }
        }
    }

    /**
     * Performs the invocation on the target component instance. If a target classloader is configured for the interceptor, it will be set as the
     * TCCL.
     *
     * @param msg         the messaging containing the invocation data
     * @param workContext the current work context
     * @param instance    the target component instance
     * @return the response message
     */
    private Message invoke(Message msg, WorkContext workContext, Object instance) {
        WorkContext oldWorkContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
        Object[] body = (Object[]) msg.getBody();
        try {
            if (targetTCCLClassLoader == null) {
                msg.setBody(operation.invoke(instance, body));
            } else {
                ClassLoader old = Thread.currentThread().getContextClassLoader();
                try {
                    Thread.currentThread().setContextClassLoader(targetTCCLClassLoader);
                    msg.setBody(operation.invoke(instance, (Object[]) body));
                } finally {
                    Thread.currentThread().setContextClassLoader(old);
                }
            }
        } catch (InvocationTargetException e) {
            msg.setBodyWithFault(e.getCause());
        } catch (IllegalAccessException e) {
            throw new InvocationRuntimeException(e);
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(oldWorkContext);
        }
        return msg;
    }

    /**
     * Starts or joins a scope context.
     *
     * @param workContext the current work context
     * @throws InvocationRuntimeException if an error occurs starting or joining the context
     */
    private void startOrJoinContext(WorkContext workContext) throws InvocationRuntimeException {
        // Check if this is a callback. If so, do not start or join the conversation since it has already been done by the forward invocation
        // Also, if the target is not conversation scoped, no context needs to be started
        if (callback || !conversationScope) {
            return;
        }
        CallFrame frame = workContext.peekCallFrame();
        if (frame == null) {
            // For now tolerate callframes not being set as bindings may not be adding them for incoming service invocations
            return;
        }
        try {
            if (ConversationContext.NEW == frame.getConversationContext()) {
                // start the conversation context
                if (component.getMaxAge() > 0) {
                    ExpirationPolicy policy = new NonRenewableExpirationPolicy(System.currentTimeMillis() + component.getMaxAge());
                    scopeContainer.startContext(workContext, policy);
                } else if (component.getMaxIdleTime() > 0) {
                    long expire = System.currentTimeMillis() + component.getMaxIdleTime();
                    ExpirationPolicy policy = new RenewableExpirationPolicy(expire, component.getMaxIdleTime());
                    scopeContainer.startContext(workContext, policy);
                } else {
                    scopeContainer.startContext(workContext);
                }
            } else if (ConversationContext.PROPAGATE == frame.getConversationContext()) {
                if (component.getMaxAge() > 0) {
                    ExpirationPolicy policy = new NonRenewableExpirationPolicy(System.currentTimeMillis() + component.getMaxAge());
                    scopeContainer.joinContext(workContext, policy);
                } else if (component.getMaxIdleTime() > 0) {
                    long expire = System.currentTimeMillis() + component.getMaxIdleTime();
                    ExpirationPolicy policy = new RenewableExpirationPolicy(expire, component.getMaxIdleTime());
                    scopeContainer.joinContext(workContext, policy);
                } else {
                    scopeContainer.joinContext(workContext);
                }
            }
        } catch (GroupInitializationException e) {
            throw new InvocationRuntimeException(e);
        }
    }
}
