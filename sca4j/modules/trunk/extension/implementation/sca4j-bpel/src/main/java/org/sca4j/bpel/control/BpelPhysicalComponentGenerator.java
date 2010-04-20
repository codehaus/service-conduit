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
package org.sca4j.bpel.control;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.model.BpelComponentType;
import org.sca4j.bpel.model.BpelImplementation;
import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.sca4j.bpel.provision.BpelPhysicalWireSourceDefinition;
import org.sca4j.bpel.provision.BpelPhysicalWireTargetDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

@EagerInit
public class BpelPhysicalComponentGenerator implements ComponentGenerator<LogicalComponent<BpelImplementation>> {

    @Reference public GeneratorRegistry generatorRegistry;
    
    @Init
    public void start() {
        generatorRegistry.register(BpelImplementation.class, this);
    }
    
    @Override
    public PhysicalComponentDefinition generate(LogicalComponent<BpelImplementation> component) {
        
        BpelImplementation implementation = component.getDefinition().getImplementation();
        BpelComponentType componentType = implementation.getComponentType();
        
        BpelPhysicalComponentDefinition physicalComponentDefinition = new BpelPhysicalComponentDefinition();
        physicalComponentDefinition.setProcessName(componentType.getProcessName());
        physicalComponentDefinition.setProcessUrl(componentType.getProcessUrl());
        physicalComponentDefinition.setComponentId(component.getUri());
        physicalComponentDefinition.setGroupId(component.getParent().getUri());
        physicalComponentDefinition.setScope(componentType.getScope());
        
        return physicalComponentDefinition;
        
    }

    @Override
    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<BpelImplementation> source, ServiceContract serviceContract, Policy policy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<BpelImplementation> source, LogicalResource<?> resource) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<BpelImplementation> source, LogicalReference reference, Policy policy) {
        return new BpelPhysicalWireSourceDefinition();
    }

    @Override
    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service, LogicalComponent<BpelImplementation> target, Policy policy) {
        return new BpelPhysicalWireTargetDefinition();
    }

}
