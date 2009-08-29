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
package org.sca4j.fabric.component.scope;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.Conversation;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.scdl.Scope;
import org.sca4j.spi.AbstractLifecycle;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.ConversationExpirationCallback;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Implements functionality common to scope containers.
 *
 * @version $Rev: 5191 $ $Date: 2008-08-11 15:22:50 +0100 (Mon, 11 Aug 2008) $
 * @param <KEY> the type of identifier used to identify instances associated with this scope
 */
public abstract class AbstractScopeContainer<KEY> extends AbstractLifecycle implements ScopeContainer<KEY> {
    private final Scope<KEY> scope;
    protected final ScopeContainerMonitor monitor;
    private ScopeRegistry scopeRegistry;

    // the queue of instanceWrappers to destroy, in the order that their instances were created
    protected final Map<KEY, List<InstanceWrapper<?>>> destroyQueues = new ConcurrentHashMap<KEY, List<InstanceWrapper<?>>>();

    public AbstractScopeContainer(Scope<KEY> scope, ScopeContainerMonitor monitor) {
        this.scope = scope;
        this.monitor = monitor;
    }

    @Reference
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    @Init
    public synchronized void start() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        if (scopeRegistry != null) {
            scopeRegistry.register(this);
        }
        setLifecycleState(RUNNING);
    }

    @Destroy
    public synchronized void stop() {
        int lifecycleState = getLifecycleState();
        if (lifecycleState != RUNNING) {
            throw new IllegalStateException("Scope in wrong state [" + lifecycleState + "]");
        }
        setLifecycleState(STOPPED);
        if (scopeRegistry != null) {
            scopeRegistry.unregister(this);
        }
        destroyQueues.clear();
    }
    
    public void stopAllContexts(WorkContext workContext) {
         throw new UnsupportedOperationException();
	}

    public Scope<KEY> getScope() {
        return scope;
    }

    public void register(AtomicComponent<?> component) {
        checkInit();
    }

    public void unregister(AtomicComponent<?> component) {
        checkInit();
    }

    public void registerCallback(Conversation conversation, ConversationExpirationCallback callback) {
        throw new UnsupportedOperationException();
    }

    public void initializeComponents(List<AtomicComponent<?>> components, WorkContext workContext) throws GroupInitializationException {
        List<Exception> causes = null;
        for (AtomicComponent<?> component : components) {
            try {
                getWrapper(component, workContext);
            } catch (Exception e) {
                monitor.eagerInitializationError(component.getUri(), e);
                if (causes == null) {
                    causes = new ArrayList<Exception>();
                }
                causes.add(e);
            }
        }
        if (causes != null) {
            throw new GroupInitializationException(causes);
        }
    }

    public String toString() {
        return "In state [" + super.toString() + ']';
    }

    /**
     * Starts a scope context.
     *
     * @param workContext the work context associated with the start operation
     * @param contextId   the scope context id
     * @throws GroupInitializationException if an error occurs starting the context
     */
    protected void startContext(WorkContext workContext, KEY contextId) throws GroupInitializationException {
        // Allocate a queue to callback instances when they are destroyed. Instances will be called back in the order they were created.
        destroyQueues.put(contextId, new ArrayList<InstanceWrapper<?>>());
    }

    /**
     * Joins an existing context.
     *
     * @param workContext the work context associated with the start operation
     * @param contextId   the scope context id
     * @return true if a local context was created
     * @throws GroupInitializationException if an error occurs starting the context
     */
    protected boolean joinContext(WorkContext workContext, KEY contextId) throws GroupInitializationException {
        if (destroyQueues.containsKey(contextId)) {
            return false;
        }
        startContext(workContext, contextId);
        return true;
    }

    /**
     * Stops a scope context and destroys all associated instances.
     *
     * @param workContext the work context associated with the stop operation
     * @param contextId   the scope context id
     */
    protected void stopContext(WorkContext workContext, KEY contextId) {
        List<InstanceWrapper<?>> list = destroyQueues.remove(contextId);
        if (list == null) {
            throw new IllegalStateException("Context does not exist: " + contextId);
        }
        destroyInstances(list, workContext);
    }

    /**
     * Shut down an ordered list of instances. The list passed to this method is treated as a live, mutable list so any instances added to this list
     * as shutdown is occuring will also be shut down.
     *
     * @param instances the list of instances to shutdown
     */
    protected void destroyInstances(List<InstanceWrapper<?>> instances, WorkContext workContext) {
        while (true) {
            InstanceWrapper<?> toDestroy;
            synchronized (instances) {
                if (instances.size() == 0) {
                    return;
                }
                toDestroy = instances.remove(instances.size() - 1);
            }
            try {
                toDestroy.stop(workContext);
            } catch (InstanceDestructionException e) {
                // log the error from destroy but continue
                monitor.destructionError(e);
            }
        }
    }

    private void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope container not running [" + getLifecycleState() + "]");
        }
    }

}
