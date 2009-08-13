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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Init;

import org.sca4j.host.Namespaces;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.Signature;

/**
 * @version $Rev: 4743 $ $Date: 2008-06-06 21:09:39 +0100 (Fri, 06 Jun 2008) $
 */
public class InitProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Init, I> {
    public static final QName IMPLEMENTATION_SYSTEM = new QName(Namespaces.SCA4J_NS, "implementation.system");

    public InitProcessor() {
        super(Init.class);
    }

    public void visitMethod(Init annotation, Method method, I implementation, IntrospectionContext context) {
        if (!validateAccessor(method, context)) {
            return;
        }
        implementation.getComponentType().setInitMethod(new Signature(method));
    }

    private boolean validateAccessor(Method method, IntrospectionContext context) {
        if (!Modifier.isProtected(method.getModifiers()) && !Modifier.isPublic(method.getModifiers())) {
            Class<?> clazz = method.getDeclaringClass();
            InvalidAccessor warning =
                    new InvalidAccessor("Ignoring " + method + " annotated with @Init. Initializers must be public or protected.", clazz);
            context.addWarning(warning);
            return false;
        }
        return true;
    }
}
