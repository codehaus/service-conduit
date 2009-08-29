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
package org.sca4j.pojo;

import java.io.Serializable;

import org.osoa.sca.Conversation;

import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Implementation of specification Conversation interface.
 *
 * @version $Rev: 2939 $ $Date: 2008-02-28 23:03:30 -0800 (Thu, 28 Feb 2008) $
 */
public class ConversationImpl implements Conversation, Serializable {
    private static final long serialVersionUID = 8249514203064252385L;
    private final Object conversationId;
    private transient ScopeContainer<Conversation> scopeContainer;

    /**
     * Constructor defining the conversation id.
     *
     * @param conversationID the conversation id
     * @param scopeContainer the scope container that manages instances associated with this conversation
     */
    public ConversationImpl(Object conversationID, ScopeContainer<Conversation> scopeContainer) {
        this.conversationId = conversationID;
        this.scopeContainer = scopeContainer;
    }

    public Object getConversationID() {
        return conversationId;
    }

    public void end() {
        if (scopeContainer == null) {
            throw new UnsupportedOperationException("Remote conversation end not supported");
        }
        WorkContext workContext = PojoWorkContextTunnel.getThreadWorkContext();
        try {
            // Ensure that the conversation context is placed on the stack
            // This may not be the case if end() is called from a client component intending to end the conversation with a reference target
            CallFrame frame = new CallFrame(null, null, this, null);
            workContext.addCallFrame(frame);
            scopeContainer.stopContext(workContext);
        } finally {
            workContext.popCallFrame();
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConversationImpl that = (ConversationImpl) o;
        return conversationId.equals(that.conversationId);
    }

    public int hashCode() {
        return conversationId.hashCode();
    }

    public String toString() {
        if (conversationId == null) {
            return "";
        }
        return conversationId.toString();
    }
}
