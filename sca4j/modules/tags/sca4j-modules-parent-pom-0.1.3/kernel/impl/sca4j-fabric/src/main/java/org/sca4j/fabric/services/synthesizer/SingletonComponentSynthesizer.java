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

package org.sca4j.fabric.services.synthesizer;

import java.net.URI;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.implementation.singleton.SingletonComponent;
import org.sca4j.fabric.implementation.singleton.SingletonImplementation;
import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.fabric.instantiator.component.ComponentInstantiator;
import static org.sca4j.fabric.runtime.ComponentNames.BOOT_CLASSLOADER_ID;
import org.sca4j.host.domain.AssemblyException;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Scope;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalWire;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.componentmanager.RegistrationException;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.services.synthesize.ComponentRegistrationException;
import org.sca4j.spi.services.synthesize.ComponentSynthesizer;
import org.sca4j.spi.services.synthesize.InvalidServiceContractException;
import org.sca4j.system.introspection.SystemImplementationProcessor;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * Implementation that synthesizes a singleton component from an existing object instance.
 *
 * @version $Revision$ $Date$
 */
public class SingletonComponentSynthesizer implements ComponentSynthesizer {

    private SystemImplementationProcessor implementationProcessor;
    private ComponentInstantiator instantiator;
    private LogicalComponentManager lcm;
    private ComponentManager componentManager;
    private ContractProcessor contractProcessor;
    private ScopeContainer scopeContainer;

    @Constructor
    public SingletonComponentSynthesizer(@Reference SystemImplementationProcessor implementationProcessor,
                                         @Reference ComponentInstantiator instantiator,
                                         @Reference LogicalComponentManager lcm,
                                         @Reference ComponentManager componentManager,
                                         @Reference ContractProcessor contractProcessor,
                                         @Reference ScopeRegistry registry) {
        this(implementationProcessor, instantiator, lcm, componentManager, contractProcessor, registry.getScopeContainer(Scope.COMPOSITE));
    }

    public SingletonComponentSynthesizer(SystemImplementationProcessor implementationProcessor,
                                         ComponentInstantiator instantiator,
                                         LogicalComponentManager lcm,
                                         ComponentManager componentManager,
                                         ContractProcessor contractProcessor,
                                         ScopeContainer scopeContainer) {
        this.implementationProcessor = implementationProcessor;
        this.instantiator = instantiator;
        this.lcm = lcm;
        this.componentManager = componentManager;
        this.contractProcessor = contractProcessor;
        this.scopeContainer = scopeContainer;
    }

    public <S, I extends S> void registerComponent(String name, Class<S> type, I instance, boolean introspect) throws ComponentRegistrationException {
        try {
            LogicalComponent<?> logical = createLogicalComponent(name, type, instance, introspect);
            AtomicComponent<I> physical = createPhysicalComponent(logical, instance);
            lcm.getRootComponent().addComponent(logical);
            componentManager.register(physical);
            scopeContainer.register(physical);
        } catch (InvalidServiceContractException e) {
            throw new ComponentRegistrationException(e);
        } catch (RegistrationException e) {
            throw new ComponentRegistrationException(e);
        } catch (AssemblyException e) {
            throw new ComponentRegistrationException(e);
        }
    }


    private <S, I extends S> LogicalComponent<Implementation<?>> createLogicalComponent(String name, Class<S> type, I instance, boolean introspect)
            throws InvalidServiceContractException, AssemblyException {
        LogicalCompositeComponent domain = lcm.getRootComponent();
        ComponentDefinition<Implementation<?>> definition = createDefinition(name, type, instance, introspect);
        LogicalChange change = new LogicalChange(domain);
        LogicalComponent<Implementation<?>> logical = instantiator.instantiate(domain, domain.getPropertyValues(), definition, change);
        if (change.hasErrors()) {
            throw new AssemblyException(change.getErrors(), change.getWarnings());
        }
        // mark singleton components as provisioned since instances are not created
        logical.setProvisioned(true);
        logical.setClassLoaderId(BOOT_CLASSLOADER_ID);
        // all references are initially resolved since they are manually injected
        for (LogicalReference reference : logical.getReferences()) {
            reference.setResolved(true);
            for (LogicalWire wire : reference.getWires()) {
                wire.setProvisioned(true);
            }
        }
        return logical;
    }

    private <S, I extends S> ComponentDefinition<Implementation<?>> createDefinition(String name, Class<S> type, I instance, boolean introspect)
            throws InvalidServiceContractException {

        String implClassName = instance.getClass().getName();

        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(getClass().getClassLoader(), null, null, null, mapping);
        if (introspect) {
            // introspect the instance so it may be injected by the runtime with additional services
            SystemImplementation implementation = new SystemImplementation();
            implementation.setImplementationClass(implClassName);
            implementationProcessor.introspect(implementation, context);
            ComponentDefinition<Implementation<?>> def = new ComponentDefinition<Implementation<?>>(name);
            SingletonImplementation singletonImplementation = new SingletonImplementation(implementation.getComponentType(), implClassName);
            def.setImplementation(singletonImplementation);
            return def;
        } else {
            // instance does not have any services injected
            ServiceContract<?> contract = contractProcessor.introspect(mapping, type, context);
            if (context.hasErrors()) {
                throw new InvalidServiceContractException(context.getErrors());
            }
            String serviceName = contract.getInterfaceName();
            ServiceDefinition service = new ServiceDefinition(serviceName, contract);

            PojoComponentType componentType = new PojoComponentType(implClassName);
            componentType.add(service);

            SingletonImplementation implementation = new SingletonImplementation(componentType, implClassName);
            implementation.setComponentType(componentType);
            ComponentDefinition<Implementation<?>> def = new ComponentDefinition<Implementation<?>>(name);
            def.setImplementation(implementation);
            return def;
        }
    }

    private <I> AtomicComponent<I> createPhysicalComponent(LogicalComponent<?> logicalComponent, I instance) {
        URI uri = logicalComponent.getUri();
        PojoComponentType type = (PojoComponentType) logicalComponent.getComponentType();
        type.getInjectionSites();
        return new SingletonComponent<I>(uri, instance, type.getInjectionSites());
    }


}
