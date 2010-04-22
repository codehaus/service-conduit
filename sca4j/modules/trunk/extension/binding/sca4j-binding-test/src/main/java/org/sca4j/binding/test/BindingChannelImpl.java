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
package org.sca4j.binding.test;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.oasisopen.sca.ServiceRuntimeException;
import org.oasisopen.sca.ServiceUnavailableException;
import org.oasisopen.sca.annotation.EagerInit;
import org.sca4j.api.scope.Conversation;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.ConversationContext;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.wire.InvocationChain;
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
        for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            if (entry.getKey().getSourceOperation().getName().equals(operation)) {
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
