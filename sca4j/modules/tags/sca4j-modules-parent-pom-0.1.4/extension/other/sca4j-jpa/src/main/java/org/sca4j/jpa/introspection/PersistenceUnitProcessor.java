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
        PersistenceUnitResource definition = createDefinition(annotation);
        implementation.getComponentType().add(definition, site);
    }

    public void visitMethod(PersistenceUnit annotation, Method method, I implementation, IntrospectionContext context) {
        MethodInjectionSite site = new MethodInjectionSite(method, 0);
        PersistenceUnitResource definition = createDefinition(annotation);
        implementation.getComponentType().add(definition, site);
    }

    PersistenceUnitResource createDefinition(PersistenceUnit annotation) {
        String name = annotation.name();
        String unitName = annotation.unitName();
        return new PersistenceUnitResource(name, unitName, factoryServiceContract);
    }
}
