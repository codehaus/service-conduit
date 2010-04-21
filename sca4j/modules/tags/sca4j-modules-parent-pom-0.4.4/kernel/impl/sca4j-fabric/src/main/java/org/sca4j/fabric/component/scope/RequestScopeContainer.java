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
 */
package org.sca4j.fabric.component.scope;

import org.osoa.sca.ConversationEndedException;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.ExpirationPolicy;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.WorkContext;

/**
 * 
 * Implements the request scope container using thread local.
 * 
 * @author meerajk
 *
 */
@Service(ScopeContainer.class)
@EagerInit
public class RequestScopeContainer extends AbstractScopeContainer<Thread> {
    
    private ThreadLocal<RequestContext> requestContextBinding = new ThreadLocal<RequestContext>();

    public RequestScopeContainer(@Monitor ScopeContainerMonitor monitor) {
        super(Scope.REQUEST, monitor);
    }

    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, WorkContext workContext) throws InstanceLifecycleException, ConversationEndedException {
        RequestContext requestContext = requestContextBinding.get();
        return requestContext.getWrapper(component, workContext);
    }

    public void startContext(WorkContext workContext) {
        requestContextBinding.set(new RequestContext(monitor));
    }

    public void joinContext(WorkContext workContext) {
        if (requestContextBinding.get() == null) {
            startContext(workContext);
        }
    }

    public void stopContext(WorkContext workContext) {
        try {
            RequestContext requestContext = requestContextBinding.get();
            requestContext.stopContext(workContext);
            requestContext = null;
        } finally {
            requestContextBinding.set(null);
        }
    }
    
    public void reinject() throws InstanceLifecycleException {
    }

    public void addObjectFactory(AtomicComponent<?> component, ObjectFactory<?> factory, String referenceName, Object key) {
    }

    public <T> void returnWrapper(AtomicComponent<T> component, WorkContext workContext, InstanceWrapper<T> wrapper) throws InstanceDestructionException {
    }

    public void startContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException {
    }

    public void joinContext(WorkContext workContext, ExpirationPolicy policy) throws GroupInitializationException {
    }

}
