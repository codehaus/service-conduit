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
package org.sca4j.system.control;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.pojo.control.InstanceFactoryGenerationHelper;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;
import org.sca4j.system.provision.SystemComponentDefinition;
import org.sca4j.system.provision.SystemWireSourceDefinition;
import org.sca4j.system.provision.SystemWireTargetDefinition;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * @version $Rev: 5318 $ $Date: 2008-09-01 22:48:11 +0100 (Mon, 01 Sep 2008) $
 */
@EagerInit
public class SystemComponentGenerator implements ComponentGenerator<LogicalComponent<SystemImplementation>> {

    private final InstanceFactoryGenerationHelper helper;

    public SystemComponentGenerator(@Reference GeneratorRegistry registry, @Reference InstanceFactoryGenerationHelper helper) {

        registry.register(SystemImplementation.class, this);
        this.helper = helper;
    }

    public PhysicalComponentDefinition generate(LogicalComponent<SystemImplementation> component) throws GenerationException {
        ComponentDefinition<SystemImplementation> definition = component.getDefinition();
        SystemImplementation implementation = definition.getImplementation();
        PojoComponentType type = implementation.getComponentType();

        InstanceFactoryDefinition providerDefinition = new InstanceFactoryDefinition();
        providerDefinition.setReinjectable(true);
        providerDefinition.setConstructor(type.getConstructor());
        providerDefinition.setInitMethod(type.getInitMethod());
        providerDefinition.setDestroyMethod(type.getDestroyMethod());
        providerDefinition.setImplementationClass(implementation.getImplementationClass());
        helper.processInjectionSites(component, providerDefinition);

        // create the physical component definition
        URI componentId = component.getUri();
        SystemComponentDefinition physical = new SystemComponentDefinition();
        physical.setComponentId(componentId);
        physical.setGroupId(component.getParent().getUri());
        physical.setScope(type.getScope());
        physical.setInitLevel(helper.getInitLevel(definition, type));
        physical.setProviderDefinition(providerDefinition);
        helper.processPropertyValues(component, physical);

        // generate the classloader resource definition
        URI classLoaderId = component.getClassLoaderId();
        physical.setClassLoaderId(classLoaderId);

        return physical;
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<SystemImplementation> source, LogicalReference reference, Policy policy)
            throws GenerationException {

        URI uri = reference.getUri();
        SystemWireSourceDefinition wireDefinition = new SystemWireSourceDefinition();
        wireDefinition.setOptimizable(true);
        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.REFERENCE, uri.getFragment()));
        ServiceContract<?> serviceContract = reference.getDefinition().getServiceContract();
        String interfaceName = serviceContract.getQualifiedInterfaceName();
        wireDefinition.setInterfaceName(interfaceName);
         
        URI classLoaderId = source.getClassLoaderId();
        wireDefinition.setClassLoaderId(classLoaderId);

        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<SystemImplementation> source,
                                                                   ServiceContract<?> serviceContract,
                                                                   Policy policy) throws GenerationException {
        throw new UnsupportedOperationException();
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service, LogicalComponent<SystemImplementation> logical, Policy policy)
            throws GenerationException {
        SystemWireTargetDefinition wireDefinition = new SystemWireTargetDefinition();
        wireDefinition.setOptimizable(true);
        wireDefinition.setUri(service.getUri());
        URI classLoaderId = logical.getClassLoaderId();
        wireDefinition.setClassLoaderId(classLoaderId);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<SystemImplementation> source, LogicalResource<?> resource)
            throws GenerationException {
        URI uri = resource.getUri();
        SystemWireSourceDefinition wireDefinition = new SystemWireSourceDefinition();
        wireDefinition.setOptimizable(true);
        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.RESOURCE, uri.getFragment()));

        URI classLoaderId = source.getClassLoaderId();
        wireDefinition.setClassLoaderId(classLoaderId);

        return wireDefinition;
    }


}
