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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;

import org.osoa.sca.annotations.Reference;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.Property;

/**
 * @version $Rev: 4359 $ $Date: 2008-05-26 07:52:15 +0100 (Mon, 26 May 2008) $
 */
public class PropertyProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<org.osoa.sca.annotations.Property, I> {
    private final IntrospectionHelper helper;

    public PropertyProcessor(@Reference IntrospectionHelper helper) {
        super(org.osoa.sca.annotations.Property.class);
        this.helper = helper;
    }

    public void visitField(org.osoa.sca.annotations.Property annotation, Field field, I implementation, IntrospectionContext context) {
        validate(annotation, field, context);
        String name = helper.getSiteName(field, annotation.name());
        Type type = field.getGenericType();
        FieldInjectionSite site = new FieldInjectionSite(field);
        Property property = createDefinition(name, annotation.required(), type, context.getTypeMapping());
        implementation.getComponentType().add(property, site);
    }

    public void visitMethod(org.osoa.sca.annotations.Property annotation, Method method, I implementation, IntrospectionContext context) {
        validate(annotation, method, context);
        String name = helper.getSiteName(method, annotation.name());
        Type type = helper.getGenericType(method);
        MethodInjectionSite site = new MethodInjectionSite(method, 0);
        Property property = createDefinition(name, annotation.required(), type, context.getTypeMapping());
        implementation.getComponentType().add(property, site);
    }

    private void validate(org.osoa.sca.annotations.Property annotation, Field field, IntrospectionContext context) {
        if (!Modifier.isProtected(field.getModifiers()) && !Modifier.isPublic(field.getModifiers())) {
            Class<?> clazz = field.getDeclaringClass();
            if (annotation.required()) {
                InvalidAccessor error =
                        new InvalidAccessor("Invalid required property. The field " + field.getName() + " on " + clazz.getName()
                                + " is annotated with @Property but properties must be public or protected.", clazz);
                context.addError(error);
            } else {
                InvalidAccessor warning =
                        new InvalidAccessor("Ignoring the field " + field.getName() + " annotated with @Property on " + clazz.getName()
                                + ". Properties must be public or protected.", clazz);
                context.addWarning(warning);
            }
        }
    }

    private void validate(org.osoa.sca.annotations.Property annotation, Method method, IntrospectionContext context) {
        if (!Modifier.isProtected(method.getModifiers()) && !Modifier.isPublic(method.getModifiers())) {
            Class<?> clazz = method.getDeclaringClass();
            if (annotation.required()) {
                InvalidAccessor error =
                        new InvalidAccessor("Invalid required property. The method " + method
                                + " is annotated with @Property and must be public or protected.", clazz);
                context.addError(error);
            } else {
                InvalidAccessor warning =
                        new InvalidAccessor("Ignoring " + method + " annotated with @Property. Property " + "must be public or protected.", clazz);
                context.addWarning(warning);
            }
        }
    }

    public void visitConstructorParameter(org.osoa.sca.annotations.Property annotation,
                                          Constructor<?> constructor,
                                          int index,
                                          I implementation,
                                          IntrospectionContext context) {
        String name = helper.getSiteName(constructor, index, annotation.name());
        Type type = helper.getGenericType(constructor, index);
        ConstructorInjectionSite site = new ConstructorInjectionSite(constructor, index);
        Property property = createDefinition(name, annotation.required(), type, context.getTypeMapping());
        implementation.getComponentType().add(property, site);
    }

    Property createDefinition(String name, boolean required, Type type, TypeMapping typeMapping) {
        Property property = new Property();
        property.setName(name);
        property.setRequired(required);
        property.setMany(helper.isManyValued(typeMapping, type));
        return property;
    }
}
