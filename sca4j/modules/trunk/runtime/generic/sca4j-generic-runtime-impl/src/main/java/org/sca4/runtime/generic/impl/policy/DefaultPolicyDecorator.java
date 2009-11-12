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
package org.sca4.runtime.generic.impl.policy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.annotations.Intent;
import org.osoa.sca.annotations.Reference;
import org.sca4j.fabric.wire.InvocationChainImpl;
import org.sca4j.scdl.definitions.PolicyPhase;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.interceptor.InterceptorBuilder;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorNotFoundException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.InterceptorDefinitionGenerator;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;
import org.sca4j.spi.policy.PolicyResult;
import org.sca4j.spi.services.definitions.DefinitionsRegistry;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.w3c.dom.Element;

/**
 * Default implementation of the policy decorator.
 *
 */
public class DefaultPolicyDecorator implements PolicyDecorator {
    
    @Reference protected DefinitionsRegistry definitionsRegistry;
    @Reference protected GeneratorRegistry generatorRegistry;
    @Reference protected Map<Class<? extends PhysicalInterceptorDefinition>, InterceptorBuilder<?, ?>> interceptorBuilders;

    /**
     * @see org.sca4.runtime.generic.impl.policy.PolicyDecorator#getDecoratedService(java.lang.Object, javax.xml.namespace.QName[])
     */
    @SuppressWarnings("unchecked")
    public <T> T getDecoratedService(T instance, String serviceClassName, List<QName> intents) {
        
        try {
            
            Class<?> serviceClass = Class.forName(serviceClassName);
            
            Map<Method, Interceptor> interceptors = new HashMap<Method, Interceptor>();
            for (Method method : serviceClass.getDeclaredMethods()) {
                InvocationChain invocationChain = new InvocationChainImpl(null);
                for (QName intent : intents) {
                    for (PolicySet policySet : definitionsRegistry.getAllDefinitions(PolicySet.class)) {
                        if (policySet.doesProvide(intent) && policySet.getPhase() == PolicyPhase.INTERCEPTION) {
                            Element policy = policySet.getExtension();
                            InterceptorDefinitionGenerator generator = generatorRegistry.getInterceptorDefinitionGenerator(policySet.getExtensionName());
                            PhysicalInterceptorDefinition pid = generator.generate(policy, null, null);
                            Interceptor interceptor = getBuilder(pid).build(pid);
                            invocationChain.addInterceptor(interceptor);
                        }
                    }
                }
                InvokerIterceptor invokerIterceptor = new InvokerIterceptor();
                invokerIterceptor.method = method;
                invokerIterceptor.instance = instance;
                invocationChain.addInterceptor(invokerIterceptor);
                interceptors.put(method, invocationChain.getHeadInterceptor());
            }
            
            PolicyHandler handler = new PolicyHandler();
            handler.interceptors = interceptors;
            
            return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { serviceClass}, handler);
            
        } catch (GeneratorNotFoundException e) {
            throw new AssertionError(e);
        } catch (GenerationException e) {
            throw new AssertionError(e);
        } catch (BuilderException e) {
            throw new AssertionError(e);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
        
    }
    
    /*
     * Get the builder.
     */
    @SuppressWarnings("unchecked")
    private <PID extends PhysicalInterceptorDefinition> InterceptorBuilder<PID, ?> getBuilder(PID definition) {
        return (InterceptorBuilder<PID, ?>) interceptorBuilders.get(definition.getClass());
    }
    
    private class PolicyHandler implements InvocationHandler {
        
        private Map<Method, Interceptor> interceptors;
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MessageImpl messageImpl = new MessageImpl();
            messageImpl.setBody(args);
            return interceptors.get(method).invoke(messageImpl).getBody();
        }
        
    }
    
    private class InvokerIterceptor implements Interceptor {
        private Object instance;
        private Method method;
        
        public Interceptor getNext() {
            return null;
        }
        
        public Message invoke(Message msg) {
            try {
                Object ret = method.invoke(instance, (Object[]) msg.getBody());
                msg.setBody(ret);
                return msg;
            } catch (Throwable th) {
                throw new ServiceUnavailableException(th);
            }
        }
        
        public void setNext(Interceptor next) {
        }
    }

}
