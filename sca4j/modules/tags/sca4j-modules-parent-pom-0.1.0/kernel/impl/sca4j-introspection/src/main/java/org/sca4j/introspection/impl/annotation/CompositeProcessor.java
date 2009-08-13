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

import org.sca4j.api.annotation.scope.Composite;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;


public class CompositeProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Composite, I> {

    public CompositeProcessor() {
        super(Composite.class);
    }

    public void visitType(Composite annotation, Class<?> type, I implementation, IntrospectionContext context) {
        Scope scopeMetaAnnotation = annotation.annotationType().getAnnotation(Scope.class);
        String scopeName = scopeMetaAnnotation.value();
        implementation.getComponentType().setScope(scopeName);
    }
}
