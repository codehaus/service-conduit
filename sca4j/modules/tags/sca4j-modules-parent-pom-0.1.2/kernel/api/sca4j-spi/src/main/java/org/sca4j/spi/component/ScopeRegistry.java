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

import org.sca4j.scdl.Scope;

/**
 * Manages {@link ScopeContainer}s in the runtime
 *
 * @version $$Rev: 566 $$ $$Date: 2007-07-24 22:07:41 +0100 (Tue, 24 Jul 2007) $$
 */
public interface ScopeRegistry {
    /**
     * Return the scope for a given name.
     *
     * @param scopeName the name of the scope
     * @return the scope for the supplied name or null if that scope is not registered
     */
    Scope<?> getScope(String scopeName);

    /**
     * Returns the scope container for the given scope or null if one not found.
     *
     * @param scope the scope
     * @return the scope container for the given scope or null if one not found
     */
    <T> ScopeContainer<T> getScopeContainer(Scope<T> scope);

    /**
     * Register a scope container with this registry.
     *
     * @param container the container to register
     * @param <T>       the type of identifiers used by the scope
     */
    <T> void register(ScopeContainer<T> container);

    /**
     * Unregister a scope container from this registry.
     *
     * @param container the container to unregister
     * @param <T>       the type of identifiers used by the scope
     */
    <T> void unregister(ScopeContainer<T> container);
}
