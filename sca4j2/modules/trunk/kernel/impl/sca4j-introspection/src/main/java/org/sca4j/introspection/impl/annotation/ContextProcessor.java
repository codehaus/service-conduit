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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.RequestContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.MethodInjectionSite;

/**
 * @version $Rev: 5455 $ $Date: 2008-09-21 06:26:43 +0100 (Sun, 21 Sep 2008) $
 */
public class ContextProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Context, I> {
    private final IntrospectionHelper helper;

    public ContextProcessor(@Reference IntrospectionHelper helper) {
        super(Context.class);
        this.helper = helper;
    }


    public void visitField(Context annotation, Field field, I implementation, IntrospectionContext context) {

        Type type = field.getGenericType();
        FieldInjectionSite site = new FieldInjectionSite(field);
        InjectableAttribute attribute = null;
        if (type instanceof Class) {
            attribute = getContext((Class) type);

        }
        if (attribute != null) {
            implementation.getComponentType().addInjectionSite(attribute, site);
        }
    }

    public void visitMethod(Context annotation, Method method, I implementation, IntrospectionContext context) {

        Type type = helper.getGenericType(method);
        MethodInjectionSite site = new MethodInjectionSite(method, 0);
        InjectableAttribute attribute = null;
        if (type instanceof Class) {
            attribute = getContext((Class) type);

        }
        if (attribute != null) {
            implementation.getComponentType().addInjectionSite(attribute, site);
        }
    }

    InjectableAttribute getContext(Class<?> type) {
        if (RequestContext.class.isAssignableFrom(type)) {
            return InjectableAttribute.REQUEST_CONTEXT;
        } else if (ComponentContext.class.isAssignableFrom(type)) {
            return InjectableAttribute.COMPONENT_CONTEXT;
        } else {
            return null;
        }
    }
}
