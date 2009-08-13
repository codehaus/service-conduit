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
package org.sca4j.introspection.impl.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.ConversationID;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.MethodInjectionSite;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class ConversationIDProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<ConversationID, I> {

    public ConversationIDProcessor() {
        super(ConversationID.class);
    }

    public void visitField(ConversationID annotation, Field field, I implementation, IntrospectionContext context) {
        InjectionSite site = new FieldInjectionSite(field);
        implementation.getComponentType().addInjectionSite(InjectableAttribute.CONVERSATION_ID, site);
    }

    public void visitMethod(ConversationID annotation, Method method, I implementation, IntrospectionContext context) {
        InjectionSite site = new MethodInjectionSite(method, 0);
        implementation.getComponentType().addInjectionSite(InjectableAttribute.CONVERSATION_ID, site);
    }
}
