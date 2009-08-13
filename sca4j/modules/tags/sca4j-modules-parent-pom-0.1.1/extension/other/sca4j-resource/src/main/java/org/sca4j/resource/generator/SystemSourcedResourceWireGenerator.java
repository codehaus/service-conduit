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
package org.sca4j.resource.generator;

import java.net.URI;

import org.sca4j.resource.model.SystemSourcedResource;
import org.sca4j.resource.model.SystemSourcedWireTargetDefinition;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.ResourceWireGenerator;
import org.sca4j.spi.model.instance.LogicalResource;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision$ $Date$
 */
@SuppressWarnings("unchecked")
@EagerInit
public class SystemSourcedResourceWireGenerator implements ResourceWireGenerator<SystemSourcedWireTargetDefinition, SystemSourcedResource> {

    private static final String SYSTEM_URI = "sca4j://runtime/";

    private GeneratorRegistry registry;

    /**
     * @param registry Injected registry.
     */
    @Reference
    public void setRegistry(@Reference GeneratorRegistry registry) {
        this.registry = registry;
    }

    /**
     * Registers with the registry.
     */
    @Init
    public void start() {
        registry.register(SystemSourcedResource.class, this);
    }

    public SystemSourcedWireTargetDefinition generateWireTargetDefinition(LogicalResource<SystemSourcedResource> logicalResource)
            throws GenerationException {

        SystemSourcedResource resourceDefinition = logicalResource.getResourceDefinition();
        String mappedName = resourceDefinition.getMappedName();

        if (mappedName == null) {
            throw new MappedNameNotFoundException();
        }

        URI targetUri = URI.create(SYSTEM_URI + mappedName);

        SystemSourcedWireTargetDefinition wtd = new SystemSourcedWireTargetDefinition();
        wtd.setOptimizable(true);
        wtd.setUri(targetUri);

        return wtd;

    }

}
