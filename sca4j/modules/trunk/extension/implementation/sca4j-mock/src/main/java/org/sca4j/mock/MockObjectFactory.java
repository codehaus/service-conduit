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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.IMocksControl;
import org.sca4j.spi.ObjectFactory;

/**
 * @version $Revision$ $Date$
 */
public class MockObjectFactory<T> implements ObjectFactory<T> {

    private final Map<Class<?>, Object> mocks = new HashMap<Class<?>, Object>();
    private final T proxy;
    private final IMocksControl control;
    
    /**
     * Eager initiates the proxy.
     * 
     * @param interfaces Interfaces that need to be proxied.
     * @param classLoader Classloader for creating the dynamic proxies.
     */
    public MockObjectFactory(List<Class<?>> interfaces, ClassLoader classLoader, IMocksControl control) {
        
        this.control = control;
        
        for(Class<?> interfaze : interfaces) {
            if(!interfaze.getName().equals(IMocksControl.class.getName())) {
                mocks.put(interfaze, control.createMock(interfaze));
            }
        }
        
        this.proxy = createProxy(interfaces, classLoader);
        
    }

    @SuppressWarnings("unchecked")
    public T getInstance() {
        return proxy;
    }

    @SuppressWarnings("unchecked")
    private T createProxy(List<Class<?>> interfaces, ClassLoader classLoader) {

        Class<?>[] mockInterfaces = new Class[interfaces.size() + 1];
        interfaces.toArray(mockInterfaces);
        mockInterfaces[mockInterfaces.length - 1] = IMocksControl.class;
        
        return (T) Proxy.newProxyInstance(classLoader, mockInterfaces, new InvocationHandler() {
            
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                
                Class<?> interfaze = method.getDeclaringClass();
                if(interfaze.getName().equals(IMocksControl.class.getName())) {
                    return method.invoke(control, args);
                } else {
                    Object mock = mocks.get(interfaze);
                    if (mock == null) {
                        for (Class<?> intf : mocks.keySet()) {
                            if (interfaze.isAssignableFrom(intf)) {
                                mock = mocks.get(intf);
                                break;
                            }
                        }
                    }
                    assert mock != null && interfaze.isInstance(mock);
                    return method.invoke(mock, args);
                }
            }
            
        });
        
    }

}
