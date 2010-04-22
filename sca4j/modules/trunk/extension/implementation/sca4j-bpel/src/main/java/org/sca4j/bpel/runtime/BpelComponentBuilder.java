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
 */
package org.sca4j.bpel.runtime;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.sca4j.bpel.spi.EmbeddedBpelServer;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilder;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;

@EagerInit
public class BpelComponentBuilder<T> implements ComponentBuilder<BpelPhysicalComponentDefinition, BpelComponent<T>> {

    @Reference public ComponentBuilderRegistry componentBuilderRegistry;
    @Reference public EmbeddedBpelServer embeddedBpelServer;
    
    @Init
    public void start() {
        componentBuilderRegistry.register(BpelPhysicalComponentDefinition.class, this);
    }
    @Override
    public BpelComponent<T> build(BpelPhysicalComponentDefinition componentDefinition) throws BuilderException {
        embeddedBpelServer.registerProcess(componentDefinition);
        return new BpelComponent<T>(componentDefinition.getComponentId(), componentDefinition.getGroupId());
    }

}
