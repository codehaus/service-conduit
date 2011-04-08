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

import java.net.URI;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.pojo.builder.PojoSourceWireAttacher;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.Wire;
import org.sca4j.system.provision.SystemWireSourceDefinition;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;

/**
 * @version $Rev: 5287 $ $Date: 2008-08-26 19:24:18 +0100 (Tue, 26 Aug 2008) $
 */
@EagerInit
public class SystemSourceWireAttacher extends PojoSourceWireAttacher implements SourceWireAttacher<SystemWireSourceDefinition> {

    private final ComponentManager manager;
    private ProxyService proxyService;

    public SystemSourceWireAttacher(@Reference ComponentManager manager,
                                    @Reference(name = "transformerRegistry")TransformerRegistry<PullTransformer<?, ?>> transformerRegistry) {
        super(transformerRegistry);
        this.manager = manager;
    }

    /**
     * Used for lazy injection of the proxy service. Since the ProxyService is only available after extensions are loaded and this class is loaded
     * during runtime boostrap, injection of the former service must be delayed. This is achieved by setting the reference to no required. when the
     * ProxyService becomes available, it will be wired to this reference.
     *
     * @param proxyService the service used to create reference proxies
     */
    @Reference(required = false)
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    public void attachToSource(SystemWireSourceDefinition sourceDefinition, PhysicalWireTargetDefinition targetDefinition, Wire wire)
            throws WiringException {
        if (proxyService == null) {
            throw new WiringException(
                    "Attempt to inject a non-optimized wire during runtime boostrap. Non-optimizied wires are only supported in user extensions");
        }
        URI sourceUri = sourceDefinition.getUri();
        URI sourceName = UriHelper.getDefragmentedName(sourceDefinition.getUri());
        SystemComponent<?> source = (SystemComponent) manager.getComponent(sourceName);
        InjectableAttribute injectableAttribute = sourceDefinition.getValueSource();

        Class<?> type;
        try {
            type = getClass().getClassLoader().loadClass(sourceDefinition.getInterfaceName());
        } catch (ClassNotFoundException e) {
            String name = sourceDefinition.getInterfaceName();
            throw new WireAttachException("Unable to load interface class: " + name, sourceUri, null, e);
        }
        if (InjectableAttributeType.CALLBACK.equals(injectableAttribute.getValueType())) {
            throw new UnsupportedOperationException("Callbacks not supported on system components");
        } else {
            String callbackUri = null;
            URI uri = targetDefinition.getCallbackUri();
            if (uri != null) {
                callbackUri = uri.toString();
            }

            ObjectFactory<?> factory = proxyService.createObjectFactory(type, sourceDefinition.getInteractionType(), wire, callbackUri);
            Object key = getKey(sourceDefinition, source, targetDefinition, injectableAttribute);
            source.setObjectFactory(injectableAttribute, factory, key);
        }
    }

    public void detachFromSource(SystemWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new AssertionError();
    }

    public void attachObjectFactory(SystemWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target)
            throws WiringException {
        URI sourceId = UriHelper.getDefragmentedName(source.getUri());
        SystemComponent<?> sourceComponent = (SystemComponent<?>) manager.getComponent(sourceId);
        InjectableAttribute referenceSource = source.getValueSource();
        Object key = getKey(source, sourceComponent, target, referenceSource);
        sourceComponent.setObjectFactory(referenceSource, objectFactory, key);
    }
}
