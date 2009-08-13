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
