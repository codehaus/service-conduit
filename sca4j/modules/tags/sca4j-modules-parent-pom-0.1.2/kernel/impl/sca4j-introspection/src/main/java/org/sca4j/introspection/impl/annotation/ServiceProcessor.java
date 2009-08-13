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

import java.lang.reflect.Type;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.java.AbstractAnnotationProcessor;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class ServiceProcessor<I extends Implementation<? extends InjectingComponentType>> extends AbstractAnnotationProcessor<Service, I> {

    private final ContractProcessor contractProcessor;

    public ServiceProcessor(@Reference ContractProcessor contractProcessor) {
        super(Service.class);
        this.contractProcessor = contractProcessor;
    }

    public void visitType(Service annotation, Class<?> type, I implementation, IntrospectionContext context) {
        TypeMapping typeMapping = context.getTypeMapping();
        InjectingComponentType componentType = implementation.getComponentType();

        for (Class<?> service : annotation.interfaces()) {
            ServiceDefinition definition = createDefinition(service, typeMapping, context);
            componentType.add(definition);
        }

        Class<?> service = annotation.value();
        if (!Void.class.equals(service)) {
            ServiceDefinition definition = createDefinition(service, typeMapping, context);
            componentType.add(definition);
        }
    }

    private ServiceDefinition createDefinition(Class<?> service, TypeMapping typeMapping, IntrospectionContext context) {
        ServiceContract<Type> serviceContract = contractProcessor.introspect(typeMapping, service, context);
        return new ServiceDefinition(serviceContract.getInterfaceName(), serviceContract);
    }
}
