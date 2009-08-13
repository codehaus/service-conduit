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
package org.sca4j.rs.runtime;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

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
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 5469 $ $Date: 2008-09-22 12:02:05 +0100 (Mon, 22 Sep 2008) $
 */
@EagerInit
public class RsSourceWireAttacher implements SourceWireAttacher<RsWireSourceDefinition> {

    private final ClassLoaderRegistry classLoaderRegistry;
    private final ServletHost servletHost;
    private final Map<URI, RsWebApplication> webApplications = new ConcurrentHashMap<URI, RsWebApplication>();

    public RsSourceWireAttacher(@Reference ServletHost servletHost, @Reference ClassLoaderRegistry classLoaderRegistry) {
        this.servletHost = servletHost;
        this.classLoaderRegistry = classLoaderRegistry;
    }

    public void attachToSource(RsWireSourceDefinition sourceDefinition,
                               PhysicalWireTargetDefinition targetDefinition,
                               Wire wire) throws WireAttachException {
        
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

    private void provision(RsWireSourceDefinition sourceDefinition, Wire wire, RsWebApplication application)
            throws ClassNotFoundException {
        
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(sourceDefinition.getClassLoaderId());

        Map<String, InvocationChain> invocationChains = new HashMap<String, InvocationChain>();
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            invocationChains.put(entry.getKey().getName(), entry.getValue());
        }
        
        MethodInterceptor methodInterceptor = new RsMethodInterceptor(invocationChains);
        
        Class<?> interfaze = classLoader.loadClass(sourceDefinition.getInterfaceName());
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(interfaze);
        enhancer.setCallback(methodInterceptor);
        Object instance = enhancer.create();
        
        application.addServiceHandler(interfaze, instance);
        
    }
}
