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
package org.sca4j.system.runtime;

import java.net.URI;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.EagerInit;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.Wire;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.system.provision.SystemWireTargetDefinition;

/**
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
@EagerInit
public class SystemTargetWireAttacher implements TargetWireAttacher<SystemWireTargetDefinition> {

    private final ComponentManager manager;
    private final ClassLoaderRegistry classLoaderRegistry;

    public SystemTargetWireAttacher(@Reference ComponentManager manager,
                                    @Reference ClassLoaderRegistry classLoaderRegistry) {
        this.manager = manager;
        this.classLoaderRegistry = classLoaderRegistry;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, SystemWireTargetDefinition target, Wire wire) throws WiringException {
        URI targetId = UriHelper.getDefragmentedName(target.getUri());
        SystemComponent<?> targetComponent = (SystemComponent<?>) manager.getComponent(targetId);

        ScopeContainer<?> scopeContainer = targetComponent.getScopeContainer();
        Class<?> implementationClass = targetComponent.getImplementationClass();
        ClassLoader loader = implementationClass.getClassLoader();

        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            PhysicalOperationDefinition operation = entry.getKey();
            InvocationChain chain = entry.getValue();

            List<String> params = operation.getParameters();
            Class<?>[] paramTypes = new Class<?>[params.size()];
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                try {
                    paramTypes[i] = classLoaderRegistry.loadClass(loader, param);
                } catch (ClassNotFoundException e) {
                    URI sourceUri = source.getUri();
                    URI targetUri = target.getUri();
                    throw new WireAttachException("Implementation class not found", sourceUri, targetUri, e);
                }
            }
            Method method;
            try {
                method = implementationClass.getMethod(operation.getName(), paramTypes);
            } catch (NoSuchMethodException e) {
                URI sourceUri = source.getUri();
                URI targetUri = target.getUri();
                throw new WireAttachException("No matching method found", sourceUri, targetUri, e);
            }

            chain.addInterceptor(createInterceptor(method, targetComponent, scopeContainer));
        }
    }

    <T> SystemInvokerInterceptor<T> createInterceptor(Method method, SystemComponent<T> component, ScopeContainer<?> scopeContainer) {
        return new SystemInvokerInterceptor<T>(method, scopeContainer, component);
    }

    public ObjectFactory<?> createObjectFactory(SystemWireTargetDefinition target) throws WiringException {
        URI targetId = UriHelper.getDefragmentedName(target.getUri());
        SystemComponent<?> targetComponent = (SystemComponent<?>) manager.getComponent(targetId);
        return targetComponent.createObjectFactory();
    }
}
