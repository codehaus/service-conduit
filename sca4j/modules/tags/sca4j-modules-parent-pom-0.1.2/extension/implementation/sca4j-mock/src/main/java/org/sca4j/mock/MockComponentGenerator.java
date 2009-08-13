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

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

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
