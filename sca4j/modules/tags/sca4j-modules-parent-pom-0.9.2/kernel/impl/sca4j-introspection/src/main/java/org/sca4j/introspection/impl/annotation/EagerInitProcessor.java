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

import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Scope;
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
