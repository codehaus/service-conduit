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

import java.util.Map;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.provision.BpelPhysicalWireSourceDefinition;
import org.sca4j.bpel.spi.EmbeddedBpelServer;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Wire source attacher.
 * 
 * @author meerajk
 *
 */
public class BpelSourceWireAttacher implements SourceWireAttacher<BpelPhysicalWireSourceDefinition> {
    
    @Reference public EmbeddedBpelServer embeddedBpelServer;

    @Override
    public void attachObjectFactory(BpelPhysicalWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target) throws WiringException {
    }

    @Override
    public void attachToSource(BpelPhysicalWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        String partnerLink = source.getPartnerLinkName();
        for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            PhysicalOperationPair physicalOperationPair = entry.getKey();
            InvocationChain invocationChain = entry.getValue();
            embeddedBpelServer.addOutboundEndpoint(source.getComponentId(), partnerLink, physicalOperationPair.getSourceOperation().getName(), invocationChain);
        }
    }

    @Override
    public void detachFromSource(BpelPhysicalWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
    }

}
