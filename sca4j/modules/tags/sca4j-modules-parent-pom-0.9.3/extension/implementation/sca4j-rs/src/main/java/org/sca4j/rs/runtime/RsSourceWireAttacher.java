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
package org.sca4j.rs.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.rs.provision.RsWireSourceDefinition;
import org.sca4j.rs.runtime.rs.RsWebApplication;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.host.ServletHost;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Rev: 5469 $ $Date: 2008-09-22 12:02:05 +0100 (Mon, 22 Sep 2008) $
 */
@EagerInit
public class RsSourceWireAttacher implements SourceWireAttacher<RsWireSourceDefinition> {

    private final ServletHost servletHost;
    private final Map<URI, RsWebApplication> webApplications = new ConcurrentHashMap<URI, RsWebApplication>();

    public RsSourceWireAttacher(@Reference ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    public void attachToSource(RsWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition targetDefinition, Wire wire) throws WireAttachException {
        
        URI sourceUri = sourceDefinition.getUri();

        RsWebApplication application = webApplications.get(sourceUri);
        if (application == null) {
            application = new RsWebApplication(getClass().getClassLoader());
            webApplications.put(sourceUri, application);
            String servletMapping = sourceUri.getPath();
            if (!servletMapping.endsWith("/*")) {
                servletMapping = servletMapping + "/*";
            }
            servletHost.registerMapping(servletMapping, application);
        }

        try {
            provision(sourceDefinition, wire, application);
        } catch (ClassNotFoundException e) {
            String name = sourceDefinition.getInterfaceName();
            throw new WireAttachException("Unable to load interface class [" + name + "]", sourceUri, null, e);
        }
        
    }

    public void detachFromSource(RsWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new AssertionError();
    }

    public void attachObjectFactory(RsWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }

    private void provision(RsWireSourceDefinition sourceDefinition, final Wire wire, RsWebApplication application) throws ClassNotFoundException {
        
        ClassLoader classLoader = getClass().getClassLoader();
        Class<?> serviceInterface = classLoader.loadClass(sourceDefinition.getInterfaze());

        Map<String, InvocationChain> invocationChains = new HashMap<String, InvocationChain>();
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            PhysicalOperationPair operation = chain.getPhysicalOperation();
            invocationChains.put(operation.getSourceOperation().getName(), chain);
        }

        MethodInterceptor methodInterceptor = new RsMethodInterceptor(invocationChains);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceInterface);
        enhancer.setCallback(methodInterceptor);
        
        application.addServiceHandler(serviceInterface, enhancer.create());
        
    }    
    
    private class RsMethodInterceptor implements MethodInterceptor {

        private Map<String, InvocationChain> invocationChains;

        private RsMethodInterceptor(Map<String, InvocationChain> invocationChains) {
            this.invocationChains = invocationChains;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Message message = new MessageImpl(args, false, new WorkContext());
            InvocationChain invocationChain = invocationChains.get(method.getName());
            if (invocationChain != null) {
                Interceptor headInterceptor = invocationChain.getHeadInterceptor();
                Message ret = headInterceptor.invoke(message);
                if (ret.isFault()) {
                    throw (Throwable) ret.getBody();
                } else {
                    return ret.getBody();
                }
            } else {
                return null;
            }
        }

    }

}
