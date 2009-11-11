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
package org.sca4j.groovy.runtime;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.groovy.provision.GroovyWireSourceDefinition;
import org.sca4j.pojo.builder.PojoSourceWireAttacher;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.spi.util.UriHelper;
import org.sca4j.spi.wire.Wire;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;

/**
 * The component builder for Java implementation types. Responsible for creating the Component runtime artifact from a physical component definition
 *
 * @version $Rev: 5248 $ $Date: 2008-08-21 01:33:22 +0100 (Thu, 21 Aug 2008) $
 */
@EagerInit
public class GroovySourceWireAttacher extends PojoSourceWireAttacher implements SourceWireAttacher<GroovyWireSourceDefinition> {
    private final ComponentManager manager;
    private final ProxyService proxyService;

    public GroovySourceWireAttacher(@Reference ComponentManager manager,
                                    @Reference ProxyService proxyService,
                                    @Reference(name = "transformerRegistry")TransformerRegistry<PullTransformer<?, ?>> transformerRegistry) {
        super(transformerRegistry);
        this.manager = manager;
        this.proxyService = proxyService;
    }

    public void attachToSource(GroovyWireSourceDefinition sourceDefinition,
                               PhysicalWireTargetDefinition targetDefinition,
                               Wire wire) throws WiringException {
        URI sourceUri = sourceDefinition.getUri();
        URI sourceName = UriHelper.getDefragmentedName(sourceDefinition.getUri());
        GroovyComponent<?> source = (GroovyComponent<?>) manager.getComponent(sourceName);
        InjectableAttribute injectableAttribute = sourceDefinition.getValueSource();

        Class<?> type;
        try {
            type = getClass().getClassLoader().loadClass(sourceDefinition.getInterfaceName());
        } catch (ClassNotFoundException e) {
            String name = sourceDefinition.getInterfaceName();
            throw new WireAttachException("Unable to load interface class [" + name + "]", sourceUri, null, e);
        }
        if (InjectableAttributeType.CALLBACK.equals(injectableAttribute.getValueType())) {
            URI targetUri = targetDefinition.getUri();
            ScopeContainer<?> container = source.getScopeContainer();
            ObjectFactory<?> factory = proxyService.createCallbackObjectFactory(type, container, targetUri, wire);
            // JFM TODO inject updates to object factory as this does not support a proxy fronting multiple callback wires
            if(factory == null){
            	factory = proxyService.createCallbackObjectFactory(type, container, targetUri, wire);
        	 } else {
        	    factory = proxyService.updateOnCallbackObjectFactory(factory, targetUri, wire);
        	 }   
            	
            source.setObjectFactory(injectableAttribute, factory);
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

    public void detachFromSource(GroovyWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new AssertionError();
    }

    public void attachObjectFactory(GroovyWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition definition)
            throws WiringException {
        URI sourceId = UriHelper.getDefragmentedName(source.getUri());
        GroovyComponent<?> sourceComponent = (GroovyComponent<?>) manager.getComponent(sourceId);
        InjectableAttribute attribute = source.getValueSource();

        Object key = getKey(source, sourceComponent, definition, attribute);
        sourceComponent.setObjectFactory(attribute, objectFactory, key);
    }
}
