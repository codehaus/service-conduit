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

import org.osoa.sca.annotations.Scope;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import static org.sca4j.scdl.Scope.COMPOSITE;
import static org.sca4j.scdl.Scope.CONVERSATION;
import static org.sca4j.scdl.Scope.REQUEST;
import static org.sca4j.scdl.Scope.STATELESS;

/**
 * @version $Rev: 4359 $ $Date: 2008-05-26 07:52:15 +0100 (Mon, 26 May 2008) $
 */
public class ScopeProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Scope, I> {

    public ScopeProcessor() {
        super(Scope.class);
    }

    public void visitType(Scope annotation, Class<?> type, I implementation, IntrospectionContext context) {
        String scopeName = annotation.value();
        if (!COMPOSITE.getScope().equals(scopeName)
                && !CONVERSATION.getScope().equals(scopeName)
                && !REQUEST.getScope().equals(scopeName)
                && !STATELESS.getScope().equals(scopeName)) {
            InvalidScope failure = new InvalidScope(type, scopeName);
            context.addError(failure);
            return;
        }
        implementation.getComponentType().setScope(scopeName);
    }
}
