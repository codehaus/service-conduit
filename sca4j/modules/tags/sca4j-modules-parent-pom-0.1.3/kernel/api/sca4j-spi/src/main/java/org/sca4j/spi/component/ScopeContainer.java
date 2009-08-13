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
package org.sca4j.spi.component;

import java.util.List;

import org.osoa.sca.Conversation;
import org.osoa.sca.ConversationEndedException;

import org.sca4j.scdl.Scope;
import org.sca4j.spi.Lifecycle;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.invocation.WorkContext;


/**
 * Manages the lifecycle and visibility of instances associated with a an {@link AtomicComponent}.
 *
 * @version $Rev: 4786 $ $Date: 2008-06-08 14:37:24 +0100 (Sun, 08 Jun 2008) $
 * @param <KEY> the type of IDs that this container uses to identify its contexts. For example, for COMPOSITE scope this could be the URI of the
 * composite component, or for HTTP Session scope it might be the HTTP session ID.
 */
public interface ScopeContainer<KEY> extends Lifecycle {

    /**
     * Returns the Scope that this container supports.
     *
     * @return the Scope that this container supports
     */
    Scope<KEY> getScope();

    /**
     * Registers a component with the scope.
     *
     * @param component the component to register
     */
    void register(AtomicComponent<?> component);

    /**
     * Unregisters a component with the scope.
     *
     * @param component the component to unregister
     */
    void unregister(AtomicComponent<?> component);

    /**
     * Registers a callback object to receive notification when a conversation has expired.
     *
     * @param conversation the conversation to listen to
     * @param callback     the callback instance that receives notifications
     */
    void registerCallback(Conversation conversation, ConversationExpirationCallback callback);

    /**
     * Start a new, non-expiring context. The context will remain active until explicitly stopped.
     *
     * @param workContext the current WorkContext
     * @throws GroupInitializationException if an exception was thrown by any eagerInit component
     */
    void startContext(WorkContext workContext) throws GroupInitializationException;

    /**
     * Start a new context which expires according to the given ExpirationPolicy. The context will remain active until it is explicitly stopped or it
     * expires.
     *
     * @param workContext the current WorkContext
     * @param policy      determines when the context expires
     * @throws GroupInitializationException if an exception was thrown by any eagerInit component
     */
    public void startContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException;

    /**
     * Joins an existing context. Since a scope context may exist accross multiple JVMs (for example, when conversational context is propagated), this
     * operation may result in the creation of a local context associated with the distributed scope context. When the scope context is contained in a
     * single JVM, a new context will not need to be created.
     *
     * @param workContext the current WorkContext
     * @throws GroupInitializationException if an exception was thrown by any eagerInit component
     */
    void joinContext(WorkContext workContext) throws GroupInitializationException;

    /**
     * Joins an existing context. Since a scope context may exist accross multiple JVMs (for example, when conversational context is propagated), this
     * operation may result in the creation of a local context associated with the distributed scope context. This variant of joinContext sets an
     * expiration policy for local contexts, if one needs to be created.
     *
     * @param workContext the current WorkContext
     * @param policy      determines when the local context expires
     * @throws GroupInitializationException if an exception was thrown by any eagerInit component
     */
    void joinContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException;

    /**
     * Stop the context associated with the current work context.
     *
     * @param workContext the current WorkContext
     */
    void stopContext(WorkContext workContext);
    
    
    void stopAllContexts(WorkContext workContext);

    /**
     * Initialise an ordered list of components. The list is traversed in order and the getWrapper() method called for each to associate an instance
     * with the supplied context.
     *
     * @param components  the components to be initialized
     * @param workContext the work context in which to initialize the components
     * @throws GroupInitializationException if one or more components threw an exception during initialization
     */
    void initializeComponents(List<AtomicComponent<?>> components, WorkContext workContext) throws GroupInitializationException;

    /**
     * Returns an instance wrapper associated with the current scope context, creating one if necessary
     *
     * @param component   the component
     * @param workContext the work context in which the instance should be obtained
     * @return the wrapper for the target instance
     * @throws InstanceLifecycleException  if there was a problem instantiating the target instance
     * @throws ConversationEndedException if the instance is conversational and the associated has ended or expired
     */
    <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, WorkContext workContext)
            throws InstanceLifecycleException, ConversationEndedException;

    /**
     * Return a wrapper after use (for example, after invoking the instance).
     *
     * @param component   the component
     * @param workContext the work context returning the instance
     * @param wrapper     the wrapper for the target instance being returned
     * @throws InstanceDestructionException if there was a problem returning the target instance
     */
    <T> void returnWrapper(AtomicComponent<T> component, WorkContext workContext, InstanceWrapper<T> wrapper) throws InstanceDestructionException;

    /**
     * Adds an object factory to references of active instances for a component.
     *
     * @param component     Component with active instances, whose references need to be updated.
     * @param factory       Object factory for the reference.
     * @param referenceName Name of the reference.
     * @param key           the component key
     */
    void addObjectFactory(AtomicComponent<?> component, ObjectFactory<?> factory, String referenceName, Object key);

    /**
     * Re-injects all live instances with updated wires.
     *
     * @throws InstanceLifecycleException if an error occurs during reinjection.
     */
    void reinject() throws InstanceLifecycleException;
}
