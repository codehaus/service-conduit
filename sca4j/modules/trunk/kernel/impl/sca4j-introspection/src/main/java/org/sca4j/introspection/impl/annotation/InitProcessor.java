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
