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
package org.sca4j.spi.services.proxy;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import org.osoa.sca.CallableReference;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Creates proxies that implement Java interfaces and invocation handlers for fronting wires
 *
 * @version $Rev: 3682 $ $Date: 2008-04-19 19:12:53 +0100 (Sat, 19 Apr 2008) $
 */

public interface ProxyService {
    /**
     * Create an ObjectFactory that provides proxies for the forward wire.
     *
     * @param interfaze   the interface the proxy implements
     * @param type        the proxy type, i.e. stateless, conversational or propagates conversations
     * @param wire        the wire to proxy @return an ObjectFactory that will create proxies
     * @param callbackUri the callback URI or null if the wire is unidirectional
     * @return the factory
     * @throws ProxyCreationException if there was a problem creating the proxy
     */
    <T> ObjectFactory<T> createObjectFactory(Class<T> interfaze, InteractionType type, Wire wire, String callbackUri) throws ProxyCreationException;

    /**
     * Create an ObjectFactory that provides proxies for the callback wire.
     *
     * @param interfaze the interface the proxy implements
     * @param container the the scope container that manages component implementations where proxies created by the object factory will be injected
     * @param targetUri the callback service uri
     * @param wire      the wire to proxy
     * @return an ObjectFactory that will create proxies
     * @throws ProxyCreationException if there was a problem creating the proxy
     */
    <T> ObjectFactory<T> createCallbackObjectFactory(Class<T> interfaze, ScopeContainer<?> container, URI targetUri, Wire wire)
            throws ProxyCreationException;

    /**
     * Creates a Java proxy for the given wire.
     *
     * @param interfaze   the interface the proxy implements
     * @param type        the interaction style for the wire
     * @param callbackUri the callback URI fr the wire fronted by the proxy or null if the wire is unidirectional
     * @param mappings    the method to invocation chain mappings
     * @return the proxy
     * @throws ProxyCreationException if there was a problem creating the proxy
     */
    <T> T createProxy(Class<T> interfaze, InteractionType type, String callbackUri, Map<Method, InvocationChain> mappings)
            throws ProxyCreationException;

    /**
     * Creates a Java proxy for the callback invocations chains.
     *
     * @param interfaze the interface the proxy should implement
     * @param mappings  the invocation chain mappings keyed by target URI @return the proxy
     * @return the proxy instance
     * @throws ProxyCreationException if an error is encountered during proxy generation
     */
    <T> T createCallbackProxy(Class<T> interfaze, Map<String, Map<Method, InvocationChain>> mappings) throws ProxyCreationException;

    /**
     * Creates a callback proxy that allways returns to the same target service
     *
     * @param interfaze the service interface
     * @param mapping   the invocation chain mapping for the callback service
     * @param container the scope container that manages the implementation instance the proxy is injected on
     * @return the proxy instance
     */
    <T> T createStatefullCallbackProxy(Class<T> interfaze, Map<Method, InvocationChain> mapping, ScopeContainer<?> container);

    /**
     * Cast a proxy to a CallableReference.
     *
     * @param target a proxy generated by this implementation
     * @return a CallableReference (or subclass) equivalent to this prozy
     * @throws IllegalArgumentException if the object supplied is not a proxy
     */
    <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException;
    
    /**
     * Updates an ObjectFactory with an additional callback wire. This used for multiple clients on the call backs
     *
     * @throws ProxyCreationException if there was a problem creating the proxy
     */
     ObjectFactory<?> updateOnCallbackObjectFactory(ObjectFactory<?> factory, URI callbackUri, Wire wire) throws ProxyCreationException;	

}
