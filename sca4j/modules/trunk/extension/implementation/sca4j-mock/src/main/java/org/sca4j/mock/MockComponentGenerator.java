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
package org.sca4j.mock;

import java.net.URI;

import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.policy.Policy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class MockComponentGenerator implements ComponentGenerator<LogicalComponent<ImplementationMock>> {

    private final GeneratorRegistry registry;

    /**
     * Initializes the generator registry.
     *
     * @param registry Generator registry.
     */
    public MockComponentGenerator(@Reference GeneratorRegistry registry) {
        this.registry = registry;
    }

    /**
     * Registers with the generator registry.
     */
    @Init
    public void init() {
        registry.register(ImplementationMock.class, this);
    }

    /**
     * Generates the component definition.
     */
    public MockComponentDefinition generate(LogicalComponent<ImplementationMock> component) throws GenerationException {

        MockComponentDefinition componentDefinition = new MockComponentDefinition();

        ImplementationMock implementationMock = component.getDefinition().getImplementation();
        MockComponentType componentType = implementationMock.getComponentType();

        componentDefinition.setInterfaces(implementationMock.getMockedInterfaces());

        componentDefinition.setComponentId(component.getUri());
        componentDefinition.setScope(componentType.getScope());

        URI classLoaderId = component.getClassLoaderId();
        componentDefinition.setClassLoaderId(classLoaderId);

        return componentDefinition;

    }

    /**
     * Generates the wire target definition.
     */
    public MockWireTargetDefinition generateWireTarget(LogicalService service,
                                                       LogicalComponent<ImplementationMock> component,
                                                       Policy policy) throws GenerationException {

        MockWireTargetDefinition definition = new MockWireTargetDefinition();
        definition.setUri(service.getUri());
        URI classLoaderId = component.getClassLoaderId();
        definition.setClassLoaderId(classLoaderId);
        ServiceContract<?> serviceContract = service.getDefinition().getServiceContract();

        definition.setMockedInterface(serviceContract.getQualifiedInterfaceName());

        return definition;

    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<ImplementationMock> component,
                                                                   LogicalResource<?> resource) {
        throw new UnsupportedOperationException("Mock objects cannot have resources");
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<ImplementationMock> component,
                                                           LogicalReference reference,
                                                           Policy policy) {
        throw new UnsupportedOperationException("Mock objects cannot be source of a wire");
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<ImplementationMock> source,
                                                                   ServiceContract<?> serviceContract,
                                                                   Policy policy) throws GenerationException {
        return new MockWireSourceDefinition();
    }

}
