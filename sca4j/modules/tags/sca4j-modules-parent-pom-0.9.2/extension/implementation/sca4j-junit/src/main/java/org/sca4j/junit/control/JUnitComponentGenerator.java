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
package org.sca4j.junit.control;

import java.net.URI;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.java.provision.JavaComponentDefinition;
import org.sca4j.java.provision.JavaWireSourceDefinition;
import org.sca4j.java.provision.JavaWireTargetDefinition;
import org.sca4j.junit.scdl.JUnitImplementation;
import org.sca4j.pojo.control.InstanceFactoryGenerationHelper;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.Scope;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * @version $Rev: 5318 $ $Date: 2008-09-01 22:48:11 +0100 (Mon, 01 Sep 2008) $
 */
@EagerInit
public class JUnitComponentGenerator implements ComponentGenerator<LogicalComponent<JUnitImplementation>> {

    private final GeneratorRegistry registry;
    private final InstanceFactoryGenerationHelper helper;

    public JUnitComponentGenerator(@Reference GeneratorRegistry registry, @Reference InstanceFactoryGenerationHelper helper) {
        this.registry = registry;
        this.helper = helper;
    }

    @Init
    public void init() {
        registry.register(JUnitImplementation.class, this);
    }

    public PhysicalComponentDefinition generate(LogicalComponent<JUnitImplementation> component)
            throws GenerationException {

        ComponentDefinition<JUnitImplementation> definition = component.getDefinition();
        JUnitImplementation implementation = definition.getImplementation();
        PojoComponentType type = implementation.getComponentType();
        Integer level = helper.getInitLevel(definition, type);
        URI componentId = component.getUri();
        String scope = type.getScope();

        InstanceFactoryDefinition providerDefinition = new InstanceFactoryDefinition();
        providerDefinition.setReinjectable(Scope.COMPOSITE.getScope().equals(scope));
        providerDefinition.setConstructor(type.getConstructor());
        providerDefinition.setInitMethod(type.getInitMethod());
        providerDefinition.setDestroyMethod(type.getDestroyMethod());
        providerDefinition.setImplementationClass(implementation.getImplementationClass());
        helper.processInjectionSites(component, providerDefinition);

        JavaComponentDefinition physical = new JavaComponentDefinition();
        physical.setGroupId(component.getParent().getUri());
        physical.setComponentId(componentId);

        URI classLoaderId = component.getClassLoaderId();
        physical.setClassLoaderId(classLoaderId);

        physical.setScope(scope);
        physical.setInitLevel(level);
        physical.setProviderDefinition(providerDefinition);
        helper.processPropertyValues(component, physical);
        return physical;
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<JUnitImplementation> source,
                                                           LogicalReference reference,
                                                           Policy policy) throws GenerationException {
        URI uri = reference.getUri();
        ServiceContract serviceContract = reference.getDefinition().getServiceContract();
        String interfaceName = getInterfaceName(serviceContract);
        URI classLoaderId = source.getClassLoaderId();

        JavaWireSourceDefinition wireDefinition = new JavaWireSourceDefinition();
        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.REFERENCE, uri.getFragment()));
        wireDefinition.setInterfaceName(interfaceName);
        if (serviceContract.isConversational()) {
            wireDefinition.setInteractionType(InteractionType.CONVERSATIONAL);
        }

        // assume for now that any wire from a JUnit component can be optimized
        wireDefinition.setOptimizable(true);

        wireDefinition.setClassLoaderId(classLoaderId);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<JUnitImplementation> source,
                                                                   ServiceContract serviceContract,
                                                                   Policy policy) throws GenerationException {
        throw new UnsupportedOperationException();
    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<JUnitImplementation> source,
                                                                   LogicalResource<?> resource)
            throws GenerationException {

        URI uri = resource.getUri();
        ServiceContract serviceContract = resource.getResourceDefinition().getServiceContract();
        String interfaceName = getInterfaceName(serviceContract);
        URI classLoaderId = source.getClassLoaderId();

        JavaWireSourceDefinition wireDefinition = new JavaWireSourceDefinition();
        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.RESOURCE, uri.getFragment()));
        wireDefinition.setClassLoaderId(classLoaderId);
        wireDefinition.setInterfaceName(interfaceName);
        return wireDefinition;
    }

    private String getInterfaceName(ServiceContract contract) {
        return contract.getQualifiedInterfaceName();
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service,
                                                           LogicalComponent<JUnitImplementation> target,
                                                           Policy policy) throws GenerationException {
        JavaWireTargetDefinition wireDefinition = new JavaWireTargetDefinition();
        wireDefinition.setUri(service.getUri());
        URI classLoaderId = target.getClassLoaderId();
        wireDefinition.setClassLoaderId(classLoaderId);
        return wireDefinition;
    }
}
