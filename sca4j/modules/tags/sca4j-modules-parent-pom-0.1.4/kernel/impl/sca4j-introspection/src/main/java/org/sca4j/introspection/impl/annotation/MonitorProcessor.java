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

import org.osoa.sca.annotations.Reference;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.MonitorResource;
import org.sca4j.scdl.ServiceContract;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class MonitorProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Monitor, I> {

    private final IntrospectionHelper helper;
    private final ContractProcessor contractProcessor;

    public MonitorProcessor(@Reference IntrospectionHelper helper, @Reference ContractProcessor contractProcessor) {
        super(Monitor.class);
        this.helper = helper;
        this.contractProcessor = contractProcessor;
    }

    public void visitField(Monitor annotation, Field field, I implementation, IntrospectionContext context) {
        String name = helper.getSiteName(field, null);
        Type type = field.getGenericType();
        FieldInjectionSite site = new FieldInjectionSite(field);
        MonitorResource resource = createDefinition(name, type, context.getTypeMapping(), context);
        implementation.getComponentType().add(resource, site);
    }

    public void visitMethod(Monitor annotation, Method method, I implementation, IntrospectionContext context) {
        String name = helper.getSiteName(method, null);
        Type type = helper.getGenericType(method);
        MethodInjectionSite site = new MethodInjectionSite(method, 0);
        MonitorResource resource = createDefinition(name, type, context.getTypeMapping(), context);
        implementation.getComponentType().add(resource, site);
    }

    public void visitConstructorParameter(Monitor annotation, Constructor<?> constructor, int index, I implementation, IntrospectionContext context) {
        String name = helper.getSiteName(constructor, index, null);
        Type type = helper.getGenericType(constructor, index);
        ConstructorInjectionSite site = new ConstructorInjectionSite(constructor, index);
        MonitorResource resource = createDefinition(name, type, context.getTypeMapping(), context);
        implementation.getComponentType().add(resource, site);
    }


    MonitorResource createDefinition(String name, Type type, TypeMapping typeMapping, IntrospectionContext context) {
        ServiceContract<?> contract = contractProcessor.introspect(typeMapping, type, context);
        return new MonitorResource(name, false, contract);
    }
}
