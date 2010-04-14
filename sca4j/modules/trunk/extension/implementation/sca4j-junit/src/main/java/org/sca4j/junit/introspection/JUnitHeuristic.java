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
package org.sca4j.junit.introspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.introspection.java.NoConstructorFound;
import org.sca4j.introspection.java.UnknownInjectionType;
import org.sca4j.junit.scdl.JUnitImplementation;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.Multiplicity;
import org.sca4j.scdl.Property;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.Signature;
import org.sca4j.scdl.validation.AmbiguousConstructor;

/**
 * @version $Rev: 4360 $ $Date: 2008-05-26 10:33:45 +0100 (Mon, 26 May 2008) $
 */
public class JUnitHeuristic implements HeuristicProcessor<JUnitImplementation> {

    private final IntrospectionHelper helper;
    private final ContractProcessor contractProcessor;

    private final HeuristicProcessor<JUnitImplementation> serviceHeuristic;

    public JUnitHeuristic(@Reference IntrospectionHelper helper,
                          @Reference ContractProcessor contractProcessor,
                          @Reference(name = "service")HeuristicProcessor<JUnitImplementation> serviceHeuristic) {
        this.helper = helper;
        this.contractProcessor = contractProcessor;
        this.serviceHeuristic = serviceHeuristic;
    }

    public void applyHeuristics(JUnitImplementation implementation, Class<?> implClass, IntrospectionContext context) {
        PojoComponentType componentType = implementation.getComponentType();

        serviceHeuristic.applyHeuristics(implementation, implClass, context);

        if (componentType.getConstructor() == null) {
            Signature ctor = findConstructor(implClass, context);
            componentType.setConstructor(ctor);
        }

        if (componentType.getProperties().isEmpty() && componentType.getReferences().isEmpty() && componentType.getResources().isEmpty()) {
            evaluateConstructor(implementation, implClass, context);
            evaluateSetters(implementation, implClass, context);
            evaluateFields(implementation, implClass, context);
        }

        if (componentType.getInitMethod() == null) {
            componentType.setInitMethod(getCallback(implClass, "setUp"));
        }
        if (componentType.getDestroyMethod() == null) {
            componentType.setDestroyMethod(getCallback(implClass, "tearDown"));
        }
    }

    private Signature getCallback(Class<?> implClass, String name) {
        while (Object.class != implClass) {
            try {
                Method callback = implClass.getDeclaredMethod(name);
                return new Signature(callback);
            } catch (NoSuchMethodException e) {
                implClass = implClass.getSuperclass();
                continue;
            }
        }
        return null;
    }

    private Signature findConstructor(Class<?> implClass, IntrospectionContext context) {
        Constructor<?>[] constructors = implClass.getDeclaredConstructors();
        Constructor<?> selected = null;
        if (constructors.length == 1) {
            selected = constructors[0];
        } else {
            for (Constructor<?> constructor : constructors) {
                if (constructor.isAnnotationPresent(org.oasisopen.sca.annotation.Constructor.class)) {
                    if (selected != null) {
                        context.addError(new AmbiguousConstructor(implClass));
                        return null;
                    }
                    selected = constructor;
                }
            }
            if (selected == null) {
                context.addError(new NoConstructorFound(implClass));
                return null;
            }
        }
        return new Signature(selected);
    }

    private void evaluateConstructor(JUnitImplementation implementation, Class<?> implClass, IntrospectionContext context) {
        PojoComponentType componentType = implementation.getComponentType();
        Map<InjectionSite, InjectableAttribute> sites = componentType.getInjectionSites();
        Constructor<?> constructor;
        try {
            if (componentType.getConstructor() == null) {
                // there was an error with the constructor previously, just return
                return;
            }
            constructor = componentType.getConstructor().getConstructor(implClass);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }

        TypeMapping typeMapping = context.getTypeMapping();
        Type[] parameterTypes = constructor.getGenericParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            InjectionSite site = new ConstructorInjectionSite(constructor, i);

            // skip sites that have already been mapped
            if (sites.containsKey(site)) {
                continue;
            }

            Type parameterType = parameterTypes[i];
            String name = helper.getSiteName(constructor, i, null);
            processSite(componentType, typeMapping, name, parameterType, site, context);
        }
    }

    void evaluateSetters(JUnitImplementation implementation, Class<?> implClass, IntrospectionContext context) {
        PojoComponentType componentType = implementation.getComponentType();
        Map<InjectionSite, InjectableAttribute> sites = componentType.getInjectionSites();
        TypeMapping typeMapping = context.getTypeMapping();
        Set<Method> setters = helper.getInjectionMethods(implClass, componentType.getServices().values());
        for (Method setter : setters) {
            InjectionSite site = new MethodInjectionSite(setter, 0);

            // skip sites that have already been mapped
            if (sites.containsKey(site)) {
                continue;
            }

            String name = helper.getSiteName(setter, null);
            Type parameterType = setter.getGenericParameterTypes()[0];
            processSite(componentType, typeMapping, name, parameterType, site, context);
        }
    }

    void evaluateFields(JUnitImplementation implementation, Class<?> implClass, IntrospectionContext context) {
        PojoComponentType componentType = implementation.getComponentType();
        Map<InjectionSite, InjectableAttribute> sites = componentType.getInjectionSites();
        TypeMapping typeMapping = context.getTypeMapping();
        Set<Field> fields = helper.getInjectionFields(implClass);
        for (Field field : fields) {
            InjectionSite site = new FieldInjectionSite(field);

            // skip sites that have already been mapped
            if (sites.containsKey(site)) {
                continue;
            }

            String name = helper.getSiteName(field, null);
            Type parameterType = field.getGenericType();
            processSite(componentType, typeMapping, name, parameterType, site, context);
        }
    }


    private void processSite(PojoComponentType componentType,
                             TypeMapping typeMapping,
                             String name,
                             Type parameterType,
                             InjectionSite site,
                             IntrospectionContext context) {
        InjectableAttributeType type = helper.inferType(parameterType, typeMapping);
        switch (type) {
        case PROPERTY:
            addProperty(componentType, typeMapping, name, parameterType, site);
            break;
        case REFERENCE:
            addReference(componentType, typeMapping, name, parameterType, site, context);
            break;
        case CALLBACK:
            // ignore
            break;
        default:
            context.addError(new UnknownInjectionType(site, type, componentType.getImplClass()));
            break;
        }
    }

    private void addProperty(PojoComponentType componentType, TypeMapping typeMapping, String name, Type parameterType, InjectionSite site) {
        Property property = new Property(name, null);
        property.setMany(helper.isManyValued(typeMapping, parameterType));
        componentType.add(property, site);
    }

    private void addReference(PojoComponentType componentType,
                              TypeMapping typeMapping,
                              String name,
                              Type parameterType,
                              InjectionSite site,
                              IntrospectionContext context) {
        ServiceContract contract = contractProcessor.introspect(typeMapping, parameterType, context);
        Multiplicity multiplicity = helper.isManyValued(typeMapping, parameterType) ? Multiplicity.ONE_N : Multiplicity.ONE_ONE;
        ReferenceDefinition reference = new ReferenceDefinition(name, contract, multiplicity);
        componentType.add(reference, site);
    }
}
