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
package org.sca4j.fabric.services.definitions;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Reference;
import org.sca4j.loader.definitions.DefinitionResourceElement;
import org.sca4j.scdl.definitions.AbstractDefinition;
import org.sca4j.scdl.definitions.BindingType;
import org.sca4j.scdl.definitions.ImplementationType;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.definitions.DefinitionActivationException;
import org.sca4j.spi.services.definitions.DefinitionsRegistry;

/**
 * Default implementation of the definitions registry.
 * 
 * @version $Revision$ $Date$
 */
public class DefaultDefinitionsRegistry implements DefinitionsRegistry {

    private MetaDataStore metaDataStore;
    private Map<Class<? extends AbstractDefinition>, Map<QName, ? extends AbstractDefinition>> cache = 
        new ConcurrentHashMap<Class<? extends AbstractDefinition>, Map<QName,? extends AbstractDefinition>>();
    
    /**
     * Initializes the cache.
     */
    public DefaultDefinitionsRegistry(@Reference MetaDataStore metaDataStore) {    
        
        this.metaDataStore = metaDataStore;
        
        cache.put(Intent.class, new ConcurrentHashMap<QName, Intent>());
        cache.put(PolicySet.class, new ConcurrentHashMap<QName, PolicySet>());
        cache.put(BindingType.class, new ConcurrentHashMap<QName, BindingType>());
        cache.put(ImplementationType.class, new ConcurrentHashMap<QName, ImplementationType>());        
    }

    public <D extends AbstractDefinition> Collection<D> getAllDefinitions(Class<D> definitionClass) {
        return getSubCache(definitionClass).values();
    }

    public <D extends AbstractDefinition> D getDefinition(QName name, Class<D> definitionClass) {
        return getSubCache(definitionClass).get(name);
    }

    public <D extends AbstractDefinition> void registerDefinition(D definition, Class<D> definitionClass) {
        getSubCache(definitionClass).put(definition.getName(), definition);
    }

    public void activateDefinitions(List<URI> contributionUris) throws DefinitionActivationException {

        for (URI uri : contributionUris) {
            Contribution contribution = metaDataStore.find(uri);
            for (Resource resource : contribution.getResources()) {
                for (DefinitionResourceElement resourceElement : resource.getResourceElements(DefinitionResourceElement.class)) {
                    AbstractDefinition value = resourceElement.getValue();
                    activate(value);
                }
            }
        }

    }

    private void activate(AbstractDefinition definition) throws DefinitionActivationException {

        if (definition instanceof Intent) {
            getSubCache(Intent.class).put(definition.getName(), (Intent) definition);
        } else if (definition instanceof PolicySet) {
            getSubCache(PolicySet.class).put(definition.getName(), (PolicySet) definition);
        } else if (definition instanceof BindingType) {
            getSubCache(BindingType.class).put(definition.getName(), (BindingType) definition);
        } else if (definition instanceof ImplementationType) {
            getSubCache(ImplementationType.class).put(definition.getName(), (ImplementationType) definition);
        }

    }
    
    @SuppressWarnings("unchecked")
    private <D extends AbstractDefinition> Map<QName, D> getSubCache(Class<D> definitionClass) {
        return (Map<QName, D>) cache.get(definitionClass);
    }

}
