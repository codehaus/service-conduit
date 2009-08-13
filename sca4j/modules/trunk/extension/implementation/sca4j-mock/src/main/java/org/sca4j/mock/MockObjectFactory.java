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
