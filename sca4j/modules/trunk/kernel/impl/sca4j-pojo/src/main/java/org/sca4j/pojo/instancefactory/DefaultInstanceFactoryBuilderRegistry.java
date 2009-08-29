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
package org.sca4j.pojo.instancefactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;

/**
 * Default implementation of the registry.
 *
 * @version $Revison$ $Date: 2008-08-20 22:25:50 +0100 (Wed, 20 Aug 2008) $
 */
public class DefaultInstanceFactoryBuilderRegistry implements InstanceFactoryBuilderRegistry {

    // Internal cache
    private Map<Class<?>, InstanceFactoryBuilder<? extends InstanceFactoryProvider,
            ? extends InstanceFactoryDefinition>> registry =
            new ConcurrentHashMap<Class<?>, InstanceFactoryBuilder<? extends InstanceFactoryProvider,
                    ? extends InstanceFactoryDefinition>>();

    /**
     * Builds an instnace factory provider from a definition.
     *
     * @param providerDefinition Provider definition.
     * @param cl                 Clasloader to use.
     * @return Instance factory provider.
     */
    @SuppressWarnings("unchecked")
    public InstanceFactoryProvider build(InstanceFactoryDefinition providerDefinition, ClassLoader cl) throws InstanceFactoryBuilderException {
        Class<? extends InstanceFactoryDefinition> type = providerDefinition.getClass();
        InstanceFactoryBuilder builder = registry.get(type);
        if (builder == null) {
            String id = type.toString();
            throw new NoRegisteredIFBuilderException("No registered builder for: " + id, id);
        }
        return builder.build(providerDefinition, cl);
    }

    /**
     * Registers the builder.
     */
    public <IFPD extends InstanceFactoryDefinition> void register(Class<?> ifpdClass, InstanceFactoryBuilder<?, IFPD> builder) {
        registry.put(ifpdClass, builder);
    }

}
