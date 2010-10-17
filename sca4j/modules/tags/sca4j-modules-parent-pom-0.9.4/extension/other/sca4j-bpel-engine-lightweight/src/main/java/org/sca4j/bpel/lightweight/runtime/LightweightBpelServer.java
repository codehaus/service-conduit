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
package org.sca4j.bpel.lightweight.runtime;

import java.net.URI;

import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.lightweight.model.BpelProcessDefinition;
import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.sca4j.bpel.spi.EmbeddedBpelServer;
import org.sca4j.idl.wsdl.spi.WsdlTypeMapper;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;

public class LightweightBpelServer implements EmbeddedBpelServer {

    @Reference public BpelProcessRegistry bpelProcessRegistry;
    @Reference public WsdlTypeMapper wsdlTypeMapper;

    @Override
    public void addOutboundEndpoint(URI componentId, String partnerLink, String operation, InvocationChain invocationChain) {
        BpelProcessDefinition bpelProcessDefinition = bpelProcessRegistry.getDefinition(componentId);
        bpelProcessDefinition.addInvoker(partnerLink, operation, invocationChain);
    }

    @Override
    public Message invokeService(PhysicalOperationDefinition targetOperationDefinition, URI componentId, QName portTypeName, String partnerLinkName, Message message) {
        BpelProcessDefinition bpelProcessDefinition = bpelProcessRegistry.getDefinition(componentId);
        BpelProcess bpelProcess = new BpelProcess(bpelProcessDefinition, wsdlTypeMapper);
        return bpelProcess.invoke(partnerLinkName, targetOperationDefinition.getName(), message);
    }

    @Override
    public void registerProcess(BpelPhysicalComponentDefinition physicalComponentDefinition) {
        bpelProcessRegistry.register(physicalComponentDefinition.getComponentId(), physicalComponentDefinition.getProcessUrl());
    }

}
