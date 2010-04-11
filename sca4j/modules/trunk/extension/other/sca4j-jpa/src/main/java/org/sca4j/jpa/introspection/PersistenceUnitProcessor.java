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
package org.sca4j.jpa.introspection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.jpa.scdl.PersistenceUnitResource;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ValidationContext;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
@EagerInit
public class PersistenceUnitProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<PersistenceUnit, I> {

    private final ServiceContract<Type> factoryServiceContract;

    public PersistenceUnitProcessor(@Reference ContractProcessor contractProcessor) {
        super(PersistenceUnit.class);
        ValidationContext context = new DefaultValidationContext();
        factoryServiceContract = contractProcessor.introspect(new TypeMapping(), EntityManagerFactory.class, context);
        assert !context.hasErrors(); // should not happen
    }

    public void visitField(PersistenceUnit annotation, Field field, I implementation, IntrospectionContext context) {
        FieldInjectionSite site = new FieldInjectionSite(field);
        boolean provideSpecific = !field.getType().equals(EntityManagerFactory.class);
        PersistenceUnitResource definition = createDefinition(annotation, provideSpecific);
        implementation.getComponentType().add(definition, site);
    }

    public void visitMethod(PersistenceUnit annotation, Method method, I implementation, IntrospectionContext context) {
        MethodInjectionSite site = new MethodInjectionSite(method, 0);
        boolean provideSpecific = !method.getParameterTypes()[0].equals(EntityManagerFactory.class);
        PersistenceUnitResource definition = createDefinition(annotation, provideSpecific);
        implementation.getComponentType().add(definition, site);
    }

    PersistenceUnitResource createDefinition(PersistenceUnit annotation, boolean providerSpecific) {
        String name = annotation.name();
        String unitName = annotation.unitName();
        return new PersistenceUnitResource(name, unitName, factoryServiceContract, providerSpecific);
    }
}
