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
package org.sca4j.tests.function.conversation;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.ConversationID;

/**
 * @version $Rev: 2946 $ $Date: 2008-02-29 07:13:44 +0000 (Fri, 29 Feb 2008) $
 */
@Scope("CONVERSATION")
public class ConversationalServiceImpl implements ConversationalService {
    private String value;
    private Object conversationId;

//    @ConversationID
//    protected Object fieldConversationId;

    // FIXME the introspection framwork does not support injecting context types (RequestContext, ComponentContext) and conversation ids on multiple
    // sites.  

    @ConversationID
    public void setConversationId(Object conversationId) {
        this.conversationId = conversationId;
    }

    public Object getConversationId() {
        return conversationId;
    }

    public Object getFieldConversationId() {
        return null;
        //return fieldConversationId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @EndsConversation
    public String end() {
        return value;
    }
}
