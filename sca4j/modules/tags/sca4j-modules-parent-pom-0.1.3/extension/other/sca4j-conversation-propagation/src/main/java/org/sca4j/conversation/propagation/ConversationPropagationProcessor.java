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
package org.sca4j.conversation.propagation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;

import org.sca4j.api.annotation.PropagatesConversation;
import org.sca4j.host.Namespaces;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;

/**
 * @version $Rev: 3105 $ $Date: 2008-03-15 09:47:31 -0700 (Sat, 15 Mar 2008) $
 */
public class ConversationPropagationProcessor<I extends Implementation<? extends InjectingComponentType>>
        extends AbstractAnnotationProcessor<PropagatesConversation, I> {
    public static final QName PROPAGATES_CONVERSATION_INTENT = new QName(Namespaces.SCA4J_NS, "propagatesConversation");

    public ConversationPropagationProcessor() {
        super(PropagatesConversation.class);
    }

    public void visitField(PropagatesConversation annotation, Field field, I implementation, IntrospectionContext context) {
    }

    public void visitMethod(PropagatesConversation annotation, Method method, I implementation, IntrospectionContext context) {
    }

    public void visitConstructorParameter(PropagatesConversation annotation,
                                          Constructor<?> constructor,
                                          int index,
                                          I implementation,
                                          IntrospectionContext context) {
    }

    public void visitType(PropagatesConversation annotation, Class<?> type, I implementation, IntrospectionContext context) {
    }
}
