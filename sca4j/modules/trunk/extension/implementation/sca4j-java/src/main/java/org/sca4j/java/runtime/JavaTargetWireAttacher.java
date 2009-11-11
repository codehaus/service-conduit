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
package org.sca4j.java.runtime;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.sca4j.java.provision.JavaWireTargetDefinition;
import org.sca4j.pojo.component.InvokerInterceptor;
import org.sca4j.pojo.provision.PojoWireSourceDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.Component;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches wires to and from components implemented using the Java programming model.
 *
 * @version $Rev: 5247 $ $Date: 2008-08-21 01:11:32 +0100 (Thu, 21 Aug 2008) $
 */
public class JavaTargetWireAttacher implements TargetWireAttacher<JavaWireTargetDefinition> {
    
    private static final Map<String, Class<?>> PRIMITIVES = new HashMap<String, Class<?>>();
    static {
        PRIMITIVES.put("byte", byte.class);
        PRIMITIVES.put("short", short.class);
        PRIMITIVES.put("int", int.class);
        PRIMITIVES.put("char", char.class);
        PRIMITIVES.put("long", long.class);
        PRIMITIVES.put("float", float.class);
        PRIMITIVES.put("double", double.class);
        PRIMITIVES.put("boolean", boolean.class);
        PRIMITIVES.put("void", void.class);
    }

    private final ComponentManager manager;

    public JavaTargetWireAttacher(@Reference ComponentManager manager) {
        this.manager = manager;
    }

    public void attachToTarget(PhysicalWireSourceDefinition sourceDefinition, JavaWireTargetDefinition targetDefinition, Wire wire)
            throws WireAttachException {
        URI targetName = UriHelper.getDefragmentedName(targetDefinition.getUri());
        Component component = manager.getComponent(targetName);
        assert component instanceof JavaComponent;

        JavaComponent<?> target = (JavaComponent<?>) component;

        ScopeContainer<?> scopeContainer = target.getScopeContainer();
        Class<?> implementationClass = target.getImplementationClass();
        ClassLoader loader = implementationClass.getClassLoader();

        // attach the invoker interceptor to forward invocation chains
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            PhysicalOperationDefinition operation = entry.getKey();
            InvocationChain chain = entry.getValue();
            List<String> params = operation.getParameters();
            Class<?>[] paramTypes = new Class<?>[params.size()];
            assert loader != null;
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                try {
                    if (PRIMITIVES.containsKey(param)) {
                        paramTypes[i] = PRIMITIVES.get(param);
                    } else {
                        paramTypes[i] = getClass().getClassLoader().loadClass(param);
                    }
                } catch (ClassNotFoundException e) {
                    URI sourceUri = sourceDefinition.getUri();
                    URI targetUri = targetDefinition.getUri();
                    throw new WireAttachException("Implementation class not found", sourceUri, targetUri, e);
                }
            }
            Method method;
            try {
                method = implementationClass.getMethod(operation.getName(), paramTypes);
            } catch (NoSuchMethodException e) {
                URI sourceUri = sourceDefinition.getUri();
                URI targetUri = targetDefinition.getUri();
                throw new WireAttachException("No matching method found", sourceUri, targetUri, e);
            }
            boolean endsConversation = operation.isEndsConversation();
            boolean callback = targetDefinition.isCallback();
            if (callback) {
                // callbacks do not expire the client (i.e. the callback target); they expire the forward implementation instance
                endsConversation = false;
            }
            InvokerInterceptor<?, ?> interceptor;
            if (sourceDefinition instanceof PojoWireSourceDefinition &&
                    targetDefinition.getClassLoaderId().equals(sourceDefinition.getClassLoaderId())) {
                // if the source is Java and target classloaders are equal, do not set the TCCL
                interceptor = createInterceptor(method, callback, endsConversation, target, scopeContainer);
            } else {
                // If the source and target classloaders are not equal, configure the interceptor to set the TCCL to the target classloader
                // when dispatching to a target instance. This guarantees when application code executes, it does so with the TCCL set to the
                // target component's classloader.
                interceptor = createInterceptor(method, callback, endsConversation, target, scopeContainer, loader);
            }
            chain.addInterceptor(interceptor);
        }
    }

    public ObjectFactory<?> createObjectFactory(JavaWireTargetDefinition target) throws WiringException {
        URI targetId = UriHelper.getDefragmentedName(target.getUri());
        JavaComponent<?> targetComponent = (JavaComponent<?>) manager.getComponent(targetId);
        return targetComponent.createObjectFactory();
    }

    private <T, CONTEXT> InvokerInterceptor<T, CONTEXT> createInterceptor(Method method,
                                                                          boolean callback,
                                                                          boolean endsConvesation,
                                                                          AtomicComponent<T> component,
                                                                          ScopeContainer<CONTEXT> scopeContainer,
                                                                          ClassLoader loader) {
        return new InvokerInterceptor<T, CONTEXT>(method, callback, endsConvesation, component, scopeContainer, loader);
    }

    private <T, CONTEXT> InvokerInterceptor<T, CONTEXT> createInterceptor(Method method,
                                                                          boolean callback,
                                                                          boolean endsConvesation,
                                                                          AtomicComponent<T> component,
                                                                          ScopeContainer<CONTEXT> scopeContainer) {
        return new InvokerInterceptor<T, CONTEXT>(method, callback, endsConvesation, component, scopeContainer);
    }

}
