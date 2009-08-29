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
package org.sca4j.mock;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.IMocksControl;
import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.SingletonObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Revision$ $Date$
 */
public class MockTargetWireAttacher implements TargetWireAttacher<MockWireTargetDefinition> {

    private final ClassLoaderRegistry classLoaderRegistry;
    private final IMocksControl control;

    public MockTargetWireAttacher(@Reference ClassLoaderRegistry classLoaderRegistry, @Reference IMocksControl control) {
        this.classLoaderRegistry = classLoaderRegistry;
        this.control = control;
    }

    public void attachToTarget(PhysicalWireSourceDefinition wireSourceDefinition,
                               MockWireTargetDefinition wireTargetDefinition,
                               Wire wire) throws WireAttachException {

        Class<?> mockedInterface = loadInterface(wireTargetDefinition);
        Object mock = createMock(mockedInterface);

        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            PhysicalOperationDefinition op = entry.getKey();
            InvocationChain chain = entry.getValue();
 
            //Each invocation chain has a single physical operation associated with it. This physical operation needs a
            //single interceptor to re-direct the invocation to the mock 
            Method operationMethod = getOperationMethod(mockedInterface, op, wireSourceDefinition, wireTargetDefinition);
            chain.addInterceptor(new MockTargetInterceptor(mock, operationMethod));
        }

    }
    
    public ObjectFactory<?> createObjectFactory(MockWireTargetDefinition target) throws WiringException {
        Class<?> mockedInterface = loadInterface(target);
        Object mock = createMock(mockedInterface);
        return new SingletonObjectFactory<Object>(mock);
    }    

    private Method getOperationMethod(Class<?> mockedInterface, PhysicalOperationDefinition op, 
                                            PhysicalWireSourceDefinition wireSourceDefinition, 
                                            MockWireTargetDefinition wireTargetDefinition) throws WireAttachException {
        List<String> parameters = op.getParameters();
        for (Method method : mockedInterface.getMethods()) {
            if(method.getName().equals(op.getName())) {                    
                Class<?>[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length == parameters.size()) {
                    List<String> methodParameters = new ArrayList<String>(); 
                    for (Class<?> parameter : parameterTypes) {
                        methodParameters.add(parameter.getName());
                    }
                    
                    if(parameters.equals(methodParameters)) {
                        return method;
                    }
                }
            }
        }
        
        throw new WireAttachException("Failed to match method: " + op.getName() + " " + op.getParameters()  , wireSourceDefinition.getUri(), wireTargetDefinition.getUri(), null);
    }

    private Object createMock(Class<?> mockedInterface) {
        if (IMocksControl.class.isAssignableFrom(mockedInterface)) {
            return control;
        } else {
            return control.createMock(mockedInterface);
        }
    }

    private Class<?> loadInterface(MockWireTargetDefinition target) throws WireAttachException {
        String interfaceClass = target.getMockedInterface();
        try {
            ClassLoader classLoader = classLoaderRegistry.getClassLoader(target.getClassLoaderId());
            return classLoader.loadClass(interfaceClass);
        } catch (ClassNotFoundException e) {
            URI targetUri = target.getUri();
            throw new WireAttachException("Unable to load interface " + interfaceClass, null, targetUri, e);
        }
    }

    private class MockTargetInterceptor implements Interceptor {

        private Interceptor next;
        private Object mock;
        private Method method;

        private MockTargetInterceptor(Object mock, Method method) {
            this.mock = mock;
            this.method = method;
        }

        public Interceptor getNext() {
            return next;
        }

        public Message invoke(Message message) {

            try {

                Object[] args = (Object[]) message.getBody();
                Object ret = method.invoke(mock, args);
                Message out = new MessageImpl();
                out.setBody(ret);

                return out;

            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            } catch (InvocationTargetException e) {
                throw new AssertionError(e);
            }

        }

        public void setNext(Interceptor next) {
            this.next = next;
        }

    }
}
