package org.sca4j.bpel.lightweight.runtime;

import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.sca4j.bpel.spi.EmbeddedBpelServer;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;

public class LightweightBpelServer implements EmbeddedBpelServer {

    @Reference BpelProcesRegistry bpelProcesRegistry;

    @Override
    public void addOutboundEndpoint(QName processName, QName referenceName, Interceptor invoker) {
    }

    @Override
    public Message invokeService(PhysicalOperationDefinition targetOperationDefinition, QName portTypeName, Message message) {
        return null;
    }

    @Override
    public void registerProcess(BpelPhysicalComponentDefinition physicalComponentDefinition) {
        bpelProcesRegistry.register(physicalComponentDefinition.getProcessUrl());
    }

}
