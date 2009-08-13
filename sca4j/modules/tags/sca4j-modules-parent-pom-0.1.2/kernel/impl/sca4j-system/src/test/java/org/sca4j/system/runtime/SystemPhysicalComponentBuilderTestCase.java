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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.system.runtime;

import java.net.URI;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.sca4j.pojo.instancefactory.InstanceFactoryBuilderRegistry;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.scdl.Signature;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.system.runtime.SystemComponent;
import org.sca4j.system.runtime.SystemComponentBuilder;
import org.sca4j.system.provision.SystemComponentDefinition;

/**
 * @version $Rev: 5274 $ $Date: 2008-08-26 05:14:56 +0100 (Tue, 26 Aug 2008) $
 */
public class SystemPhysicalComponentBuilderTestCase<T> extends TestCase {
    private ComponentBuilderRegistry builderRegistry;
    private ScopeRegistry scopeRegistry;
    private InstanceFactoryBuilderRegistry providerBuilders;
    private InstanceFactoryDefinition providerDefinition;
    private InstanceFactoryProvider<T> instanceFactoryProvider;
    private SystemComponentBuilder<T> builder;
    private SystemComponentDefinition definition;
    private URI componentId;
    private URI groupId;
    private ClassLoaderRegistry classLoaderRegistry;
    private ClassLoader classLoader;

    public void testBuildSimplePOJO() throws Exception {
        SystemComponent<T> component = builder.build(definition);
        assertEquals(componentId, component.getUri());
        assertEquals(-1, component.getInitLevel());
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        groupId = URI.create("sca4j://composite");
        componentId = URI.create("sca4j://component");

        builderRegistry = EasyMock.createMock(ComponentBuilderRegistry.class);
        scopeRegistry = EasyMock.createMock(ScopeRegistry.class);

        classLoader = getClass().getClassLoader();
        classLoaderRegistry = EasyMock.createMock(ClassLoaderRegistry.class);
        EasyMock.expect(classLoaderRegistry.getClassLoader(groupId)).andStubReturn(classLoader);
        EasyMock.replay(classLoaderRegistry);

        providerBuilders = EasyMock.createMock(InstanceFactoryBuilderRegistry.class);
        providerDefinition = new InstanceFactoryDefinition();
        providerDefinition.setConstructor(new Signature("Foo"));
        instanceFactoryProvider = EasyMock.createMock(InstanceFactoryProvider.class);
        EasyMock.expect(providerBuilders.build(providerDefinition, classLoader)).andStubReturn(instanceFactoryProvider);
        EasyMock.replay(providerBuilders);

        builder = new SystemComponentBuilder<T>(builderRegistry,
                                                scopeRegistry,
                                                providerBuilders,
                                                classLoaderRegistry,
                                                null);

        definition = new SystemComponentDefinition();
        definition.setGroupId(groupId);
        definition.setComponentId(componentId);
        definition.setClassLoaderId(groupId);
        definition.setScope("COMPOSITE");
        definition.setInitLevel(-1);
        definition.setProviderDefinition(providerDefinition);
    }
}
