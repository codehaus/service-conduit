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
package org.sca4j.binding.test;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.Conversation;
import org.osoa.sca.annotations.EagerInit;

import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.ConversationContext;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Rev: 3560 $ $Date: 2008-04-04 18:32:49 +0100 (Fri, 04 Apr 2008) $
 */
@EagerInit
public class BindingChannelImpl implements BindingChannel {
    private Map<URI, Holder> wires = new ConcurrentHashMap<URI, Holder>();

    public void registerDestinationWire(URI uri, Wire wire, URI callbackUri) {
        wires.put(uri, new Holder(wire, callbackUri));
    }

    public Message send(URI destination, String operation, Message msg) {
        Holder holder = wires.get(destination);
        if (holder == null) {
            throw new ServiceUnavailableException("No destination registered for [" + destination + "]");
        }
        Wire wire = holder.getWire();
        InvocationChain chain = null;
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            if (entry.getKey().getName().equals(operation)) {
                chain = entry.getValue();
            }
        }
        if (chain == null) {
            throw new ServiceRuntimeException("Operation on " + destination + " not found [" + operation + "]");
        }
        WorkContext workContext = msg.getWorkContext();
        try {
            CallFrame previous = workContext.peekCallFrame();
            // copy correlation information from incoming frame
            Object id = previous.getCorrelationId(Object.class);
            ConversationContext context = previous.getConversationContext();
            Conversation conversation = previous.getConversation();
            String callbackUri = holder.getCallbackUri();
            CallFrame frame = new CallFrame(callbackUri, id, conversation, context);
            workContext.addCallFrame(frame);
            return chain.getHeadInterceptor().invoke(msg);
        } finally {
            workContext.popCallFrame();
        }
    }

    private class Holder {
        private Wire wire;
        private String callbackUri;

        public Wire getWire() {
            return wire;
        }

        public String getCallbackUri() {
            return callbackUri;
        }

        private Holder(Wire wire, URI callbackUri) {
            this.wire = wire;
            if (callbackUri != null) {
                this.callbackUri = callbackUri.toString();
            }
        }
    }
}
