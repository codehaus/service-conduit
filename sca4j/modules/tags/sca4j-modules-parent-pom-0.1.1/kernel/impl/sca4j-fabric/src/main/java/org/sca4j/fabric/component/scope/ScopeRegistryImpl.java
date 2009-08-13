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
package org.sca4j.fabric.component.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.scdl.Scope;

/**
 * The default implementation of a scope registry
 *
 * @version $Rev: 566 $ $Date: 2007-07-24 22:07:41 +0100 (Tue, 24 Jul 2007) $
 */
public class ScopeRegistryImpl implements ScopeRegistry {
    private final Map<String, ScopeContainer<?>> scopes = new ConcurrentHashMap<String, ScopeContainer<?>>();

    public synchronized <T> void register(ScopeContainer<T> container) {
        Scope scope = container.getScope();
        scopes.put(scope.getScope(), container);
    }

    public synchronized <T> void unregister(ScopeContainer<T> container) {
        scopes.remove(container.getScope().getScope());
    }

    @SuppressWarnings("unchecked")
    public <T> ScopeContainer<T> getScopeContainer(Scope<T> scope) {
        return (ScopeContainer<T>) scopes.get(scope.getScope());
    }


    public Scope<?> getScope(String scopeName) {
        ScopeContainer<?> container = scopes.get(scopeName);
        return container == null ? null : container.getScope();
    }
}
