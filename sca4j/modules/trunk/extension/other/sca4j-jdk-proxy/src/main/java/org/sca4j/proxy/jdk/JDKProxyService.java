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
package org.sca4j.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.CallableReference;
import org.osoa.sca.Conversation;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.services.proxy.ProxyCreationException;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * the default implementation of a wire service that uses JDK dynamic proxies
 *
 * @version $$Rev: 3150 $$ $$Date: 2008-03-21 14:12:51 -0700 (Fri, 21 Mar 2008) $$
 */
public class JDKProxyService implements ProxyService {
    
    private static Map<String, Class<?>> primitives = new HashMap<String, Class<?>>();
    static {
        primitives.put("int", int.class);
        primitives.put("float", float.class);
        primitives.put("double", double.class);
        primitives.put("short", short.class);
        primitives.put("boolean", boolean.class);
        primitives.put("long", long.class);
    }
    
    private ScopeRegistry scopeRegistry;
    private ScopeContainer<Conversation> conversationalContainer;

    /**
     * Default Constructor
     */
    public JDKProxyService() {
    }

    /**
     * Initializes by the given attributes
     * @param classLoaderRegistry
     * @param scopeRegistry
     */
    @Constructor
    public JDKProxyService(@Reference ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    public <T> ObjectFactory<T> createObjectFactory(Class<T> interfaze, InteractionType type, Wire wire, String callbackUri)
            throws ProxyCreationException {
        Map<Method, InvocationChain> mappings = createInterfaceToWireMapping(interfaze, wire);
        return new WireObjectFactory<T>(interfaze, type, callbackUri, this, mappings);
    }

    public <T> ObjectFactory<T> createCallbackObjectFactory(Class<T> interfaze, ScopeContainer<?> container, URI targetUri, Wire wire)
            throws ProxyCreationException {
        Map<Method, InvocationChain> operationMappings = createInterfaceToWireMapping(interfaze, wire);
        Map<String, Map<Method, InvocationChain>> mappings = new HashMap<String, Map<Method, InvocationChain>>();
        mappings.put(targetUri.toString(), operationMappings);
        return new CallbackWireObjectFactory<T>(interfaze, container, this, mappings);
    }

    public <T> T createProxy(Class<T> interfaze, InteractionType type, String callbackUri, Map<Method, InvocationChain> mappings)
            throws ProxyCreationException {
        JDKInvocationHandler<T> handler;
        if (InteractionType.CONVERSATIONAL == type || InteractionType.PROPAGATES_CONVERSATION == type) {
            // create a conversational proxy
            ScopeContainer<Conversation> scopeContainer = getContainer();
            handler = new JDKInvocationHandler<T>(interfaze, type, callbackUri, mappings, scopeContainer);
        } else {
            // create a non-conversational proxy
            handler = new JDKInvocationHandler<T>(interfaze, callbackUri, mappings);
        }
        return handler.getService();
    }

    public <T> T createCallbackProxy(Class<T> interfaze, Map<String, Map<Method, InvocationChain>> mappings) throws ProxyCreationException {
        ClassLoader cl = interfaze.getClassLoader();
        MultiThreadedCallbackInvocationHandler<T> handler = new MultiThreadedCallbackInvocationHandler<T>(interfaze, mappings);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    public <T> T createStatefullCallbackProxy(Class<T> interfaze, Map<Method, InvocationChain> mapping, ScopeContainer<?> container) {
        ClassLoader cl = interfaze.getClassLoader();
        StatefulCallbackInvocationHandler<T> handler = new StatefulCallbackInvocationHandler<T>(interfaze, container, mapping);
        return interfaze.cast(Proxy.newProxyInstance(cl, new Class[]{interfaze}, handler));
    }

    @SuppressWarnings("unchecked")
    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        InvocationHandler handler = Proxy.getInvocationHandler(target);
        if (handler instanceof JDKInvocationHandler) {
            JDKInvocationHandler<B> jdkHandler = (JDKInvocationHandler<B>) handler;
            return (R) jdkHandler.getServiceReference();
        } else if (handler instanceof MultiThreadedCallbackInvocationHandler) {
            // TODO return a CallbackReference
            throw new UnsupportedOperationException();
        } else {
            throw new IllegalArgumentException("Not a SCA4J SCA proxy");
        }
    }
    
    
    public ObjectFactory<?> updateOnCallbackObjectFactory(ObjectFactory<?> factory, URI callbackUri, Wire wire) throws ProxyCreationException {
    	validateFactoryforCallabck(factory);
    	
        CallbackWireObjectFactory<?> callbackFactory = (CallbackWireObjectFactory<?>) factory;
     	final Class<?> interfaze = callbackFactory.getInterface();
     	Map<Method, InvocationChain> operationMappings = createInterfaceToWireMapping(interfaze, wire);
     	callbackFactory.updateMappings(callbackUri.toString(), operationMappings);
     	return callbackFactory;
    }

    
    private void validateFactoryforCallabck(ObjectFactory<?> factory) {
    	if (!(factory instanceof CallbackWireObjectFactory)) {  	 	  
   	 	  throw new IllegalArgumentException("ObjectFactory must be an instance of " + CallbackWireObjectFactory.class.getName() + ": This Class " + factory.getClass().getName() + " Is not that instance");
      }
		
	}

	private Map<Method, InvocationChain> createInterfaceToWireMapping(Class<?> interfaze, Wire wire) throws NoMethodForOperationException {

        Map<PhysicalOperationDefinition, InvocationChain> invocationChains = wire.getInvocationChains();

        Map<Method, InvocationChain> chains = new HashMap<Method, InvocationChain>(invocationChains.size());
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : invocationChains.entrySet()) {
            PhysicalOperationDefinition operation = entry.getKey();
            try {
                Method method = findMethod(interfaze, operation);
                chains.put(method, entry.getValue());
            } catch (NoSuchMethodException e) {
                throw new NoMethodForOperationException(operation.getName());
            } catch (ClassNotFoundException e) {
                throw new ProxyCreationException(e);
            }
        }
        return chains;
    }

    /**
     * Returns the matching method from the class for a given operation.
     *
     * @param clazz     the class to introspect
     * @param operation the operation to match
     * @return a matching method
     * @throws NoSuchMethodException  if a matching method is not found
     * @throws ClassNotFoundException if a parameter type specified in the operation is not found
     */
    private Method findMethod(Class<?> clazz, PhysicalOperationDefinition operation) throws NoSuchMethodException, ClassNotFoundException {
        String name = operation.getName();
        List<String> params = operation.getParameters();
        Class<?>[] types = new Class<?>[params.size()];
        for (int i = 0; i < params.size(); i++) {
            if (primitives.containsKey(params.get(i))) {
                types[i] = primitives.get(params.get(i));
            } else {
                types[i] = getClass().getClassLoader().loadClass(params.get(i));
            }
        }
        return clazz.getMethod(name, types);
    }

    private ScopeContainer<Conversation> getContainer() {
        if (conversationalContainer == null) {
            conversationalContainer = scopeRegistry.getScopeContainer(Scope.CONVERSATION);
        }
        return conversationalContainer;
    }
}
