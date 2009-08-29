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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.introspection.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;

/**
 * Interface for processors that handle annotations attached to Java declarations.
 *
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 * @param <A> the type of annotation this processor handles
 */
public interface AnnotationProcessor<A extends Annotation, I extends Implementation<? extends InjectingComponentType>> {
    /**
     * Returns the type of annotation this processor handles.
     *
     * @return the type of annotation this processor handles
     */
    Class<A> getType();

    /**
     * Visit an annotation on a package declaration. If errors or warnings are encountered, they will be collated in the IntrospectionContext.
     *
     * @param annotation     the annotation
     * @param javaPackage    the package
     * @param implementation the implementation being introspected
     * @param context        the current introspection context
     */
    void visitPackage(A annotation, Package javaPackage, I implementation, IntrospectionContext context);

    /**
     * Visit an annotation on a class or interface declaration.  If errors or warnings are encountered, they will be collated in the
     * IntrospectionContext.
     *
     * @param annotation     the annotation
     * @param type           the class or interface
     * @param implementation the implementation being introspected
     * @param context        the current introspection context
     */
    void visitType(A annotation, Class<?> type, I implementation, IntrospectionContext context);

    /**
     * Visit an annotation on a field declaration. If errors or warnings are encountered, they will be collated in the IntrospectionContext.
     *
     * @param annotation     the annotation
     * @param field          the field
     * @param implementation the implementation being introspected
     * @param context        the current introspection context
     */
    void visitField(A annotation, Field field, I implementation, IntrospectionContext context);

    /**
     * Visit an annotation on a method declaration. If errors or warnings are encountered, they will be collated in the IntrospectionContext.
     *
     * @param annotation     the annotation
     * @param method         the method declaration
     * @param implementation the implementation being introspected
     * @param context        the current introspection context
     */
    void visitMethod(A annotation, Method method, I implementation, IntrospectionContext context);

    /**
     * Visit an annotation on a method parameter declaration. If errors or warnings are encountered, they will be collated in the
     * IntrospectionContext.
     *
     * @param annotation     the annotation
     * @param method         the method declaration
     * @param index          the index of the method parameter
     * @param implementation the implementation being introspected
     * @param context        the current introspection context
     */
    void visitMethodParameter(A annotation, Method method, int index, I implementation, IntrospectionContext context);

    /**
     * Visit an annotation on a constructor declaration. If errors or warnings are encountered, they will be collated in the IntrospectionContext.
     *
     * @param annotation     the annotation
     * @param constructor    the constructor
     * @param implementation the implementation being introspected
     * @param context        the current introspection context
     */
    void visitConstructor(A annotation, Constructor<?> constructor, I implementation, IntrospectionContext context);

    /**
     * Visit an annotation on a constructor parameter declaration. If errors or warnings are encountered, they will be collated in the
     * IntrospectionContext.
     *
     * @param annotation     the annotation
     * @param constructor    the constructor
     * @param index          the index of the constructor parameter
     * @param implementation the implementation being introspected
     * @param context        the current introspection context
     */
    void visitConstructorParameter(A annotation, Constructor<?> constructor, int index, I implementation, IntrospectionContext context);
}
