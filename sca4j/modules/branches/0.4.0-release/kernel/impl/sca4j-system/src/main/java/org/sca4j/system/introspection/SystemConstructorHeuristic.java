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
package org.sca4j.system.introspection;

import java.lang.reflect.Constructor;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.Signature;
import org.sca4j.scdl.validation.AmbiguousConstructor;
import org.sca4j.introspection.java.NoConstructorFound;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * Heuristic that selects the constructor to use.
 *
 * @version $Rev: 4360 $ $Date: 2008-05-26 10:33:45 +0100 (Mon, 26 May 2008) $
 */
public class SystemConstructorHeuristic implements HeuristicProcessor<SystemImplementation> {

    public void applyHeuristics(SystemImplementation implementation, Class<?> implClass, IntrospectionContext context) {
        PojoComponentType componentType = implementation.getComponentType();

        // if there is already a defined constructor then do nothing
        if (componentType.getConstructor() != null) {
            return;
        }

        Signature signature = findConstructor(implClass, context);
        componentType.setConstructor(signature);
    }

    /**
     * Find the constructor to use.
     * <p/>
     * For now, we require that the class have a single constructor or one annotated with @Constructor. If there is more than one, the default
     * constructor will be selected or an org.osoa.sca.annotations.Constructor annotation must be used.
     *
     * @param implClass the class we are inspecting
     * @param context   the introspection context to report errors and warnings
     * @return the signature of the constructor to use
     */
    Signature findConstructor(Class<?> implClass, IntrospectionContext context) {
        Constructor<?>[] constructors = implClass.getDeclaredConstructors();
        Constructor<?> selected = null;
        if (constructors.length == 1) {
            selected = constructors[0];
        } else {
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(org.osoa.sca.annotations.Constructor.class)) {
                    if (selected != null) {
                        context.addError(new AmbiguousConstructor(implClass));
                        return null;
                    }
                    selected = constructor;
                }
            }
            if (selected == null) {
                try {
                    selected = implClass.getConstructor();
                } catch (NoSuchMethodException e) {
                    context.addError(new NoConstructorFound(implClass));
                    return null;
                }
            }
        }
        return new Signature(selected);
    }

}
