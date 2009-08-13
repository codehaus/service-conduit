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
