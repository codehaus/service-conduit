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
package org.sca4j.fabric.component.scope;

import java.util.List;

import org.sca4j.scdl.Scope;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.InstanceWrapperStore;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.ObjectCreationException;

/**
 * Scope container that manages instances in association with a backing store that is able to persist them across invocations.
 *
 * @version $Rev: 4786 $ $Date: 2008-06-08 14:37:24 +0100 (Sun, 08 Jun 2008) $
 * @param <KEY> the type of identifier used to identify instances of this scope
 */
public abstract class StatefulScopeContainer<KEY> extends AbstractScopeContainer<KEY> {
    private final InstanceWrapperStore<KEY> store;

    public StatefulScopeContainer(Scope<KEY> scope, ScopeContainerMonitor monitor, InstanceWrapperStore<KEY> store) {
        super(scope, monitor);
        this.store = store;
    }

    public <T> void returnWrapper(AtomicComponent<T> component, WorkContext workContext, InstanceWrapper<T> wrapper)
            throws InstanceDestructionException {
    }

    protected void startContext(WorkContext workContext, KEY contextId) throws GroupInitializationException {
        store.startContext(contextId);
        super.startContext(workContext, contextId);
    }

    protected void stopContext(WorkContext workContext, KEY contextId) {
        super.stopContext(workContext, contextId);
        store.stopContext(contextId);
    }

    /**
     * Return an instance wrapper containing a component implementation instance associated with the correlation key, optionally creating one if not
     * found.
     *
     * @param component   the component the implementation instance belongs to
     * @param workContext the current WorkContext
     * @param contextId   the correlation key for the component implementation instance
     * @param create      true if an instance should be created
     * @return an instance wrapper or null if not found an create is set to false
     * @throws org.sca4j.spi.component.InstanceLifecycleException if an error occurs returning the wrapper
     */
    protected <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, WorkContext workContext, KEY contextId, boolean create)
            throws InstanceLifecycleException {
        assert contextId != null;
        InstanceWrapper<T> wrapper = store.getWrapper(component, contextId);
        if (wrapper == null && create) {
            try {
                wrapper = component.createInstanceWrapper(workContext);
            } catch (ObjectCreationException e) {
                throw new InstanceLifecycleException(e.getMessage(), component.getUri().toString(), e);
            }
            wrapper.start(workContext);
            store.putWrapper(component, contextId, wrapper);
            List<InstanceWrapper<?>> queue = destroyQueues.get(contextId);
            if (queue == null) {
                throw new IllegalStateException("Instance context not found");
            }
            queue.add(wrapper);
        }
        return wrapper;
    }

}
