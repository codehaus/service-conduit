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
package org.sca4j.introspection.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.java.AnnotationProcessor;
import org.sca4j.introspection.java.ClassWalker;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;

/**
 * @version $Rev: 5000 $ $Date: 2008-07-10 01:37:40 +0100 (Thu, 10 Jul 2008) $
 */
public class DefaultClassWalker<I extends Implementation<? extends InjectingComponentType>> implements ClassWalker<I> {

    private Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation, I>> processors;

    /**
     * Constructor used from the bootstrapper.
     *
     * @param processors
     */
    public DefaultClassWalker(Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation, I>> processors) {
        this.processors = processors;
    }

    /**
     * Constructor used from the system SCDL.
     * <p/>
     * TODO This needs to be working once the re-injection is working properly.
     */
    @org.osoa.sca.annotations.Constructor
    public DefaultClassWalker() {
    }

    @Reference
    public void setProcessors(Map<Class<? extends Annotation>, AnnotationProcessor<? extends Annotation, I>> processors) {
        this.processors = processors;
    }

    public void walk(I implementation, Class<?> clazz, IntrospectionContext context) {
        walk(implementation, clazz, false, context);
    }

    public void walk(I implementation, Class<?> clazz, boolean isSuperClass, IntrospectionContext context) {
        if (!clazz.isInterface()) {
            walkSuperClasses(implementation, clazz, context);
        }

        walkInterfaces(implementation, clazz, context);

        walkClass(implementation, clazz, context);

        walkFields(implementation, clazz, context);

        walkMethods(implementation, clazz, context);

        if (!isSuperClass) {
            // If a superclass is being evaluated, ignore its constructors.
            // Otherwise references, properties, or resources may be incorrectly introspected.
            walkConstructors(implementation, clazz, context);
        }
    }

    private void walkSuperClasses(I implementation, Class<?> clazz, IntrospectionContext context) {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            walk(implementation, superClass, true, context);
        }
    }

    private void walkInterfaces(I implementation, Class<?> clazz, IntrospectionContext context) {
        for (Class<?> interfaze : clazz.getInterfaces()) {
            walk(implementation, interfaze, context);
        }
    }

    private void walkClass(I implementation, Class<?> clazz, IntrospectionContext context) {
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            visitType(annotation, clazz, implementation, context);
        }
    }

    private void walkFields(I implementation, Class<?> clazz, IntrospectionContext context) {
        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                visitField(annotation, field, implementation, context);
            }
        }
    }

    private void walkMethods(I implementation, Class<?> clazz, IntrospectionContext context) {
        for (Method method : clazz.getDeclaredMethods()) {
            for (Annotation annotation : method.getDeclaredAnnotations()) {
                visitMethod(annotation, method, implementation, context);
            }

            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                for (Annotation annotation : annotations) {
                    visitMethodParameter(annotation, method, i, implementation, context);
                }
            }
        }
    }

    private void walkConstructors(I implementation, Class<?> clazz, IntrospectionContext context) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            for (Annotation annotation : constructor.getDeclaredAnnotations()) {
                visitConstructor(annotation, constructor, implementation, context);
            }

            Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                Annotation[] annotations = parameterAnnotations[i];
                for (Annotation annotation : annotations) {
                    visitConstructorParameter(annotation, constructor, i, implementation, context);
                }
            }
        }
    }

    private <A extends Annotation> void visitType(A annotation, Class<?> clazz, I implementation, IntrospectionContext context) {
        AnnotationProcessor<A, I> processor = getProcessor(annotation);
        if (processor != null) {
            processor.visitType(annotation, clazz, implementation, context);
        }
    }

    private <A extends Annotation> void visitField(A annotation, Field field, I implementation, IntrospectionContext context) {
        AnnotationProcessor<A, I> processor = getProcessor(annotation);
        if (processor != null) {
            processor.visitField(annotation, field, implementation, context);
        }
    }

    private <A extends Annotation> void visitMethod(A annotation, Method method, I implementation, IntrospectionContext context) {
        AnnotationProcessor<A, I> processor = getProcessor(annotation);
        if (processor != null) {
            processor.visitMethod(annotation, method, implementation, context);
        }
    }

    private <A extends Annotation> void visitMethodParameter(A annotation, Method method, int index, I implementation, IntrospectionContext context) {
        AnnotationProcessor<A, I> processor = getProcessor(annotation);
        if (processor != null) {
            processor.visitMethodParameter(annotation, method, index, implementation, context);
        }
    }

    private <A extends Annotation> void visitConstructor(A annotation, Constructor<?> constructor, I implementation, IntrospectionContext context) {
        AnnotationProcessor<A, I> processor = getProcessor(annotation);
        if (processor != null) {
            processor.visitConstructor(annotation, constructor, implementation, context);
        }
    }

    private <A extends Annotation> void visitConstructorParameter(A annotation,
                                                                  Constructor<?> constructor,
                                                                  int index,
                                                                  I implementation,
                                                                  IntrospectionContext context) {
        AnnotationProcessor<A, I> processor = getProcessor(annotation);
        if (processor != null) {
            processor.visitConstructorParameter(annotation, constructor, index, implementation, context);
        }
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> AnnotationProcessor<A, I> getProcessor(A annotation) {
        return (AnnotationProcessor<A, I>) processors.get(annotation.annotationType());
    }
}
