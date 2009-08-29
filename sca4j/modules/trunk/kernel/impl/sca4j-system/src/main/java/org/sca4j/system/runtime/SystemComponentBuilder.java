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

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.pojo.builder.PojoComponentBuilder;
import org.sca4j.pojo.instancefactory.InstanceFactoryBuilderRegistry;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.system.provision.SystemComponentDefinition;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;

/**
 * @version $Rev: 5250 $ $Date: 2008-08-21 02:18:25 +0100 (Thu, 21 Aug 2008) $
 */
@EagerInit
public class SystemComponentBuilder<T> extends PojoComponentBuilder<T, SystemComponentDefinition, SystemComponent<T>> {

    public SystemComponentBuilder(
            @Reference ComponentBuilderRegistry builderRegistry,
            @Reference ScopeRegistry scopeRegistry,
            @Reference InstanceFactoryBuilderRegistry providerBuilders,
            @Reference ClassLoaderRegistry classLoaderRegistry,
            @Reference(name = "transformerRegistry")TransformerRegistry<PullTransformer<?, ?>> transformerRegistry) {
        super(builderRegistry, scopeRegistry, providerBuilders, classLoaderRegistry, transformerRegistry);
    }

    @Init
    public void init() {
        builderRegistry.register(SystemComponentDefinition.class, this);
    }

    public SystemComponent<T> build(SystemComponentDefinition definition) throws BuilderException {
        URI componentId = definition.getComponentId();
        int initLevel = definition.getInitLevel();
        URI groupId = definition.getGroupId();
        ClassLoader classLoader = classLoaderRegistry.getClassLoader(definition.getClassLoaderId());

        // get the scope container for this component
        ScopeContainer<?> scopeContainer = scopeRegistry.getScopeContainer(Scope.COMPOSITE);

        // create the InstanceFactoryProvider based on the definition in the model
        InstanceFactoryDefinition providerDefinition = definition.getProviderDefinition();
        InstanceFactoryProvider<T> provider = providerBuilders.build(providerDefinition, classLoader);

        createPropertyFactories(definition, provider);

        return new SystemComponent<T>(componentId, provider, scopeContainer, groupId, initLevel, -1, -1);
    }
}
