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
package org.sca4j.system.runtime;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;
import org.sca4j.system.provision.SystemWireTargetDefinition;

/**
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
@EagerInit
public class SystemTargetWireAttacher implements TargetWireAttacher<SystemWireTargetDefinition> {

    private final ComponentManager manager;

    public SystemTargetWireAttacher(@Reference ComponentManager manager) {
        this.manager = manager;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, SystemWireTargetDefinition target, Wire wire) throws WiringException {
        URI targetId = UriHelper.getDefragmentedName(target.getUri());
        SystemComponent<?> targetComponent = (SystemComponent<?>) manager.getComponent(targetId);

        ScopeContainer<?> scopeContainer = targetComponent.getScopeContainer();
        Class<?> implementationClass = targetComponent.getImplementationClass();
        ClassLoader loader = implementationClass.getClassLoader();

        for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            PhysicalOperationDefinition operation = entry.getKey().getSourceOperation();
            InvocationChain chain = entry.getValue();

            List<String> params = operation.getParameters();
            Class<?>[] paramTypes = new Class<?>[params.size()];
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                try {
                    paramTypes[i] = getClass().getClassLoader().loadClass(param);
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
