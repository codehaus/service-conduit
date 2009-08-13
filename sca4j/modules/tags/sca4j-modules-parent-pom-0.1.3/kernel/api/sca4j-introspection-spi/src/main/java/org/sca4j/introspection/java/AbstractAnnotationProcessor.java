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
 * Abstract base class for annotation processors that provides default implementations of the interface methods that simply return.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractAnnotationProcessor<A extends Annotation, I extends Implementation<? extends InjectingComponentType>> implements AnnotationProcessor<A, I> {
    private final Class<A> type;

    /**
     * Constructor binding the annotation type.
     *
     * @param type the annotation type
     */
    protected AbstractAnnotationProcessor(Class<A> type) {
        this.type = type;
    }

    public Class<A> getType() {
        return type;
    }

    public void visitPackage(A annotation, Package javaPackage, I implementation, IntrospectionContext context) {
    }

    public void visitType(A annotation, Class<?> type, I implementation, IntrospectionContext context) {
    }

    public void visitField(A annotation, Field field, I implementation, IntrospectionContext context) {
    }

    public void visitMethod(A annotation, Method method, I implementation, IntrospectionContext context) {
    }

    public void visitMethodParameter(A annotation, Method method, int index, I implementation, IntrospectionContext context){
    }

    public void visitConstructor(A annotation, Constructor<?> constructor, I implementation, IntrospectionContext context){
    }

    public void visitConstructorParameter(A annotation, Constructor<?> constructor, int index, I implementation, IntrospectionContext context){
    }
}
