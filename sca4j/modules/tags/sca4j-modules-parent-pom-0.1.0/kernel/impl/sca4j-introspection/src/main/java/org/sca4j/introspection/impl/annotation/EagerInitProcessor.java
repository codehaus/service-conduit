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

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Scope;

import org.sca4j.host.Namespaces;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;

/**
 * @version $Rev: 4359 $ $Date: 2008-05-26 07:52:15 +0100 (Mon, 26 May 2008) $
 */
public class EagerInitProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<EagerInit, I> {

    public static final QName IMPLEMENTATION_SYSTEM = new QName(Namespaces.SCA4J_NS, "implementation.system");

    public EagerInitProcessor() {
        super(EagerInit.class);
    }

    public void visitType(EagerInit annotation, Class<?> type, I implementation, IntrospectionContext context) {
        if (!validateScope(type, implementation, context)) {
            return;
        }
        InjectingComponentType componentType = implementation.getComponentType();
        componentType.setInitLevel(50);
    }

    private boolean validateScope(Class<?> type, I implementation, IntrospectionContext context) {
        if (IMPLEMENTATION_SYSTEM.equals(implementation.getType())) {
            // system implementations are composite scoped by default
            return true;
        }
        Scope scope = type.getAnnotation(Scope.class);
        if (scope == null || !org.sca4j.scdl.Scope.COMPOSITE.getScope().equals(scope.value())) {
            EagerInitNotSupported warning = new EagerInitNotSupported(type);
            context.addWarning(warning);
            return false;
        }
        return true;
    }

}
