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

import java.util.LinkedList;
import java.util.List;

import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.InstanceDestructionException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.invocation.WorkContext;

public class RequestContext {
    
    private List<AtomicComponent<?>> components = new LinkedList<AtomicComponent<?>>();
    private List<InstanceWrapper<?>> instanceWrappers = new LinkedList<InstanceWrapper<?>>();
    
    private ScopeContainerMonitor monitor;
    
    public RequestContext(ScopeContainerMonitor monitor) {
        this.monitor = monitor;
    }
    
    @SuppressWarnings("unchecked")
    public <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, WorkContext workContext) throws InstanceLifecycleException { 
        
        int index = components.indexOf(component);
        if (index != -1) {
            return (InstanceWrapper<T>) instanceWrappers.get(index);
        } else {
            try {
                InstanceWrapper<T> instanceWrapper = component.createInstanceWrapper(workContext);
                components.add(component);
                instanceWrappers.add(instanceWrapper);
                instanceWrapper.start(workContext);
                return instanceWrapper;
            } catch (ObjectCreationException e) {
                throw new InstanceLifecycleException(e.getMessage(), component.getUri().toString(), e);
            }
        }
        
    }

    public void stopContext(WorkContext workContext) {
        for (InstanceWrapper<?> instanceWrapper : instanceWrappers) {
            try {
                instanceWrapper.stop(workContext);
            } catch (InstanceDestructionException e) {
                // log the error from destroy but continue
                monitor.destructionError(e);
            }
        }
        components.clear();
        instanceWrappers.clear();
    }

}
