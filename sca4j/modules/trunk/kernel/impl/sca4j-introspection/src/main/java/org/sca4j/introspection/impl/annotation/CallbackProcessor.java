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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.CallbackDefinition;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.ServiceContract;

/**
 * @version $Rev: 4359 $ $Date: 2008-05-26 07:52:15 +0100 (Mon, 26 May 2008) $
 */
public class CallbackProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Callback, I> {
    private final IntrospectionHelper helper;
    private final ContractProcessor contractProcessor;

    public CallbackProcessor(@Reference ContractProcessor contractProcessor, @Reference IntrospectionHelper helper) {
        super(Callback.class);
        this.contractProcessor = contractProcessor;
        this.helper = helper;
    }


    public void visitField(Callback annotation, Field field, I implementation, IntrospectionContext context) {
        validate(field, context);

        String name = helper.getSiteName(field, null);
        Type type = field.getGenericType();
        FieldInjectionSite site = new FieldInjectionSite(field);
        CallbackDefinition definition = createDefinition(name, type, context.getTypeMapping(), context);
        implementation.getComponentType().add(definition, site);
    }

    public void visitMethod(Callback annotation, Method method, I implementation, IntrospectionContext context) {
        validate(method, context);

        String name = helper.getSiteName(method, null);
        Type type = helper.getGenericType(method);
        MethodInjectionSite site = new MethodInjectionSite(method, 0);
        CallbackDefinition definition = createDefinition(name, type, context.getTypeMapping(), context);
        implementation.getComponentType().add(definition, site);
    }

    private void validate(Field field, IntrospectionContext context) {
        if (!Modifier.isProtected(field.getModifiers()) && !Modifier.isPublic(field.getModifiers())) {
            Class<?> clazz = field.getDeclaringClass();
            InvalidAccessor warning =
                    new InvalidAccessor("Illegal callback. The field " + field.getName() + " on " + clazz.getName()
                            + " is annotated with @Callback and must be public or protected.", clazz);
            context.addError(warning);
        }
    }

    private void validate(Method method, IntrospectionContext context) {
        if (!Modifier.isProtected(method.getModifiers()) && !Modifier.isPublic(method.getModifiers())) {
            Class<?> clazz = method.getDeclaringClass();
            InvalidAccessor warning = new InvalidAccessor("Illegal callback. The method " + method
                    + " is annotated with @Callback and must be public or protected.", clazz);
            context.addError(warning);
        }
    }

    private CallbackDefinition createDefinition(String name, Type type, TypeMapping typeMapping, IntrospectionContext context) {
        Type baseType = helper.getBaseType(type, typeMapping);
        ServiceContract<Type> contract = contractProcessor.introspect(typeMapping, baseType, context);
        return new CallbackDefinition(name, contract);
    }
}
