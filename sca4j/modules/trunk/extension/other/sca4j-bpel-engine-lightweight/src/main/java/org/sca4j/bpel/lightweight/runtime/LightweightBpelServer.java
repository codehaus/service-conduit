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
