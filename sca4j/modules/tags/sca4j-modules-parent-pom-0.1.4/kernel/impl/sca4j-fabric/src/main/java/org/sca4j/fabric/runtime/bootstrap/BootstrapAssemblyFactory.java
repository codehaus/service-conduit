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
package org.sca4j.fabric.runtime.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;

import org.sca4j.fabric.allocator.Allocator;
import org.sca4j.fabric.allocator.LocalAllocator;
import org.sca4j.fabric.builder.ConnectorImpl;
import org.sca4j.fabric.builder.classloader.ClassLoaderBuilder;
import org.sca4j.fabric.builder.classloader.ClassLoaderBuilderImpl;
import org.sca4j.fabric.builder.component.DefaultComponentBuilderRegistry;
import org.sca4j.fabric.command.AttachWireCommand;
import org.sca4j.fabric.command.BuildComponentCommand;
import org.sca4j.fabric.command.InitializeComponentCommand;
import org.sca4j.fabric.command.ProvisionClassloaderCommand;
import org.sca4j.fabric.command.StartComponentCommand;
import org.sca4j.fabric.command.StartCompositeContextCommand;
import org.sca4j.fabric.domain.RuntimeDomain;
import org.sca4j.fabric.executor.AttachWireCommandExecutor;
import org.sca4j.fabric.executor.BuildComponentCommandExecutor;
import org.sca4j.fabric.executor.CommandExecutorRegistryImpl;
import org.sca4j.fabric.executor.InitializeComponentCommandExecutor;
import org.sca4j.fabric.executor.ProvisionClassloaderCommandExecutor;
import org.sca4j.fabric.executor.StartComponentCommandExecutor;
import org.sca4j.fabric.executor.StartCompositeContextCommandExecutor;
import org.sca4j.fabric.generator.GeneratorRegistryImpl;
import org.sca4j.fabric.generator.PhysicalModelGenerator;
import org.sca4j.fabric.generator.PhysicalModelGeneratorImpl;
import org.sca4j.fabric.generator.classloader.ClassLoaderCommandGenerator;
import org.sca4j.fabric.generator.classloader.ClassLoaderCommandGeneratorImpl;
import org.sca4j.fabric.generator.component.BuildComponentCommandGenerator;
import org.sca4j.fabric.generator.component.InitializeComponentCommandGenerator;
import org.sca4j.fabric.generator.component.StartComponentCommandGenerator;
import org.sca4j.fabric.generator.component.StartCompositeContextCommandGenerator;
import org.sca4j.fabric.generator.component.StopComponentCommandGenerator;
import org.sca4j.fabric.generator.component.StopCompositeContextCommandGenerator;
import org.sca4j.fabric.generator.wire.LocalWireCommandGenerator;
import org.sca4j.fabric.generator.wire.PhysicalOperationHelper;
import org.sca4j.fabric.generator.wire.PhysicalOperationHelperImpl;
import org.sca4j.fabric.generator.wire.PhysicalWireGenerator;
import org.sca4j.fabric.generator.wire.PhysicalWireGeneratorImpl;
import org.sca4j.fabric.generator.wire.ResourceWireCommandGenerator;
import org.sca4j.fabric.generator.wire.ServiceWireCommandGenerator;
import org.sca4j.fabric.implementation.singleton.SingletonComponentGenerator;
import org.sca4j.fabric.implementation.singleton.SingletonSourceWireAttacher;
import org.sca4j.fabric.implementation.singleton.SingletonTargetWireAttacher;
import org.sca4j.fabric.implementation.singleton.SingletonWireSourceDefinition;
import org.sca4j.fabric.implementation.singleton.SingletonWireTargetDefinition;
import org.sca4j.fabric.instantiator.LogicalModelInstantiator;
import org.sca4j.fabric.instantiator.LogicalModelInstantiatorImpl;
import org.sca4j.fabric.instantiator.ResolutionService;
import org.sca4j.fabric.instantiator.ResolutionServiceImpl;
import org.sca4j.fabric.instantiator.component.AtomicComponentInstantiator;
import org.sca4j.fabric.instantiator.component.CompositeComponentInstantiator;
import org.sca4j.fabric.instantiator.component.WireInstantiator;
import org.sca4j.fabric.instantiator.component.WireInstantiatorImpl;
import org.sca4j.fabric.instantiator.normalize.PromotionNormalizer;
import org.sca4j.fabric.instantiator.normalize.PromotionNormalizerImpl;
import org.sca4j.fabric.instantiator.promotion.DefaultPromotionResolutionService;
import org.sca4j.fabric.instantiator.promotion.PromotionResolutionService;
import org.sca4j.fabric.instantiator.target.ExplicitTargetResolutionService;
import org.sca4j.fabric.instantiator.target.ServiceContractResolver;
import org.sca4j.fabric.instantiator.target.ServiceContractResolverImpl;
import org.sca4j.fabric.instantiator.target.TargetResolutionService;
import org.sca4j.fabric.instantiator.target.TypeBasedAutowireResolutionService;
import org.sca4j.fabric.monitor.MonitorWireAttacher;
import org.sca4j.fabric.monitor.MonitorWireGenerator;
import org.sca4j.fabric.monitor.MonitorWireTargetDefinition;
import org.sca4j.fabric.policy.NullPolicyResolver;
import org.sca4j.fabric.services.contribution.ClasspathProcessorRegistryImpl;
import org.sca4j.fabric.services.contribution.LocalContributionUriResolver;
import org.sca4j.fabric.services.contribution.processor.JarClasspathProcessor;
import org.sca4j.fabric.services.documentloader.DocumentLoader;
import org.sca4j.fabric.services.documentloader.DocumentLoaderImpl;
import org.sca4j.fabric.services.routing.RuntimeRoutingService;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.jmx.control.JMXBindingGenerator;
import org.sca4j.jmx.provision.JMXWireSourceDefinition;
import org.sca4j.jmx.runtime.JMXWireAttacher;
import org.sca4j.jmx.scdl.JMXBinding;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.pojo.control.GenerationHelperImpl;
import org.sca4j.pojo.instancefactory.BuildHelperImpl;
import org.sca4j.pojo.instancefactory.DefaultInstanceFactoryBuilderRegistry;
import org.sca4j.pojo.instancefactory.InstanceFactoryBuildHelper;
import org.sca4j.pojo.instancefactory.InstanceFactoryBuilderRegistry;
import org.sca4j.pojo.instancefactory.ReflectiveInstanceFactoryBuilder;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.generator.AddCommandGenerator;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.RemoveCommandGenerator;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.system.control.SystemComponentGenerator;
import org.sca4j.system.provision.SystemWireSourceDefinition;
import org.sca4j.system.provision.SystemWireTargetDefinition;
import org.sca4j.system.runtime.SystemComponentBuilder;
import org.sca4j.system.runtime.SystemSourceWireAttacher;
import org.sca4j.system.runtime.SystemTargetWireAttacher;
import org.sca4j.transform.DefaultTransformerRegistry;
import org.sca4j.transform.PullTransformer;
import org.sca4j.transform.TransformerRegistry;
import org.sca4j.transform.dom2java.String2Boolean;
import org.sca4j.transform.dom2java.String2Class;
import org.sca4j.transform.dom2java.String2Integer;
import org.sca4j.transform.dom2java.String2QName;
import org.sca4j.transform.dom2java.String2String;
import org.sca4j.transform.dom2java.generics.list.String2ListOfQName;
import org.sca4j.transform.dom2java.generics.list.String2ListOfString;
import org.sca4j.transform.dom2java.generics.map.String2MapOfString2String;

/**
 * @version $Rev: 5389 $ $Date: 2008-09-11 07:39:02 +0100 (Thu, 11 Sep 2008) $
 */
public class BootstrapAssemblyFactory {

    public static Domain createDomain(MonitorFactory monitorFactory,
                                      ClassLoaderRegistry classLoaderRegistry,
                                      ScopeRegistry scopeRegistry,
                                      ComponentManager componentManager,
                                      LogicalComponentManager logicalComponentManager,
                                      MetaDataStore metaDataStore,
                                      MBeanServer mbServer,
                                      String jmxSubDomain,
                                      HostInfo info) throws InitializationException {

        Allocator allocator = new LocalAllocator();
        CommandExecutorRegistry commandRegistry =
                createCommandExecutorRegistry(monitorFactory,
                                              classLoaderRegistry,
                                              scopeRegistry,
                                              componentManager,
                                              mbServer,
                                              metaDataStore,
                                              jmxSubDomain,
                                              info);

        RuntimeRoutingService routingService = new RuntimeRoutingService(commandRegistry, scopeRegistry);

        PhysicalModelGenerator physicalModelGenerator =
                createPhysicalModelGenerator(logicalComponentManager, metaDataStore);

        LogicalModelInstantiator logicalModelInstantiator = createLogicalModelGenerator(logicalComponentManager);

        return new RuntimeDomain(allocator,
                                 metaDataStore,
                                 physicalModelGenerator,
                                 logicalModelInstantiator,
                                 logicalComponentManager,
                                 routingService);
    }

    private static LogicalModelInstantiator createLogicalModelGenerator(LogicalComponentManager logicalComponentManager) {
        PromotionResolutionService promotionResolutionService = new DefaultPromotionResolutionService();
        List<TargetResolutionService> targetResolutionServices = new ArrayList<TargetResolutionService>();
        ServiceContractResolver serviceContractResolver = new ServiceContractResolverImpl();
        ExplicitTargetResolutionService explicitTargetResolutionService = new ExplicitTargetResolutionService(serviceContractResolver);
        targetResolutionServices.add(explicitTargetResolutionService);
        TypeBasedAutowireResolutionService autowireResolutionService = new TypeBasedAutowireResolutionService(serviceContractResolver);
        targetResolutionServices.add(autowireResolutionService);
        ResolutionService resolutionService = new ResolutionServiceImpl(promotionResolutionService, targetResolutionServices);

        PromotionNormalizer normalizer = new PromotionNormalizerImpl();
        DocumentLoader documentLoader = new DocumentLoaderImpl();
        AtomicComponentInstantiator atomicComponentInstantiator = new AtomicComponentInstantiator(documentLoader);

        WireInstantiator wireInstantiator = new WireInstantiatorImpl();
        CompositeComponentInstantiator compositeComponentInstantiator =
                new CompositeComponentInstantiator(atomicComponentInstantiator, wireInstantiator, documentLoader);
        return new LogicalModelInstantiatorImpl(resolutionService,
                                                normalizer,
                                                logicalComponentManager,
                                                atomicComponentInstantiator,
                                                compositeComponentInstantiator,
                                                wireInstantiator);
    }

    private static CommandExecutorRegistry createCommandExecutorRegistry(MonitorFactory monitorFactory,
                                                                         ClassLoaderRegistry classLoaderRegistry,
                                                                         ScopeRegistry scopeRegistry,
                                                                         ComponentManager componentManager,
                                                                         MBeanServer mbeanServer,
                                                                         MetaDataStore metaDataStore,
                                                                         String jmxSubDomain,
                                                                         HostInfo info) {

        InstanceFactoryBuilderRegistry providerRegistry = new DefaultInstanceFactoryBuilderRegistry();
        InstanceFactoryBuildHelper buildHelper = new BuildHelperImpl(classLoaderRegistry);
        ReflectiveInstanceFactoryBuilder provider = new ReflectiveInstanceFactoryBuilder(providerRegistry, buildHelper);
        provider.init();

        TransformerRegistry<PullTransformer<?, ?>> transformerRegistry =
                new DefaultTransformerRegistry<PullTransformer<?, ?>>();
        transformerRegistry.register(new String2String());
        transformerRegistry.register(new String2Integer());
        transformerRegistry.register(new String2Boolean());
        transformerRegistry.register(new String2MapOfString2String());
        transformerRegistry.register(new String2Class(classLoaderRegistry));
        transformerRegistry.register(new String2QName());
        transformerRegistry.register(new String2ListOfString());
        transformerRegistry.register(new String2ListOfQName());

        ComponentBuilderRegistry registry = new DefaultComponentBuilderRegistry();

        SystemComponentBuilder<?> builder = new SystemComponentBuilder<Object>(registry,
                                                                               scopeRegistry,
                                                                               providerRegistry,
                                                                               classLoaderRegistry,
                                                                               transformerRegistry);
        builder.init();

        Map<Class<? extends PhysicalWireSourceDefinition>, SourceWireAttacher<? extends PhysicalWireSourceDefinition>> sourceAttachers =
                new ConcurrentHashMap<Class<? extends PhysicalWireSourceDefinition>, SourceWireAttacher<? extends PhysicalWireSourceDefinition>>();
        sourceAttachers.put(SystemWireSourceDefinition.class,
                            new SystemSourceWireAttacher(componentManager, transformerRegistry, classLoaderRegistry));
        sourceAttachers.put(SingletonWireSourceDefinition.class, new SingletonSourceWireAttacher(componentManager));

        sourceAttachers.put(JMXWireSourceDefinition.class, new JMXWireAttacher(mbeanServer, classLoaderRegistry, jmxSubDomain));

        Map<Class<? extends PhysicalWireTargetDefinition>, TargetWireAttacher<? extends PhysicalWireTargetDefinition>> targetAttachers =
                new ConcurrentHashMap<Class<? extends PhysicalWireTargetDefinition>, TargetWireAttacher<? extends PhysicalWireTargetDefinition>>();
        targetAttachers.put(SingletonWireTargetDefinition.class, new SingletonTargetWireAttacher(componentManager));
        targetAttachers.put(SystemWireTargetDefinition.class, new SystemTargetWireAttacher(componentManager, classLoaderRegistry));
        targetAttachers.put(MonitorWireTargetDefinition.class, new MonitorWireAttacher(monitorFactory, classLoaderRegistry));

        ConnectorImpl connector = new ConnectorImpl();
        connector.setSourceAttachers(sourceAttachers);
        connector.setTargetAttachers(targetAttachers);

        ClasspathProcessorRegistry cpRegistry = new ClasspathProcessorRegistryImpl();

        ClassLoaderBuilder classLoaderBuilder = createClassLoaderBuilder(classLoaderRegistry, cpRegistry, metaDataStore, info);

        CommandExecutorRegistryImpl commandRegistry = new CommandExecutorRegistryImpl();

        commandRegistry.register(StartCompositeContextCommand.class, new StartCompositeContextCommandExecutor(scopeRegistry));
        commandRegistry.register(InitializeComponentCommand.class, new InitializeComponentCommandExecutor(scopeRegistry, componentManager));
        commandRegistry.register(BuildComponentCommand.class, new BuildComponentCommandExecutor(registry, componentManager));
        commandRegistry.register(AttachWireCommand.class, new AttachWireCommandExecutor(connector));
        commandRegistry.register(StartComponentCommand.class, new StartComponentCommandExecutor(componentManager));
        commandRegistry.register(ProvisionClassloaderCommand.class, new ProvisionClassloaderCommandExecutor(classLoaderBuilder));

        return commandRegistry;

    }

    private static ClassLoaderBuilder createClassLoaderBuilder(ClassLoaderRegistry classLoaderRegistry,
                                                               ClasspathProcessorRegistry cpRegistry,
                                                               MetaDataStore metaDataStore,
                                                               HostInfo info) {

        LocalContributionUriResolver resolver = new LocalContributionUriResolver(metaDataStore);
        JarClasspathProcessor classpathProcessor = new JarClasspathProcessor(cpRegistry, info);
        classpathProcessor.init();
        return new ClassLoaderBuilderImpl(classLoaderRegistry, resolver, cpRegistry, info);
    }

    private static PhysicalModelGenerator createPhysicalModelGenerator(LogicalComponentManager logicalComponentManager,
                                                                       MetaDataStore metaDataStore) {

        GeneratorRegistry generatorRegistry = createGeneratorRegistry();
        PhysicalOperationHelper physicalOperationHelper = new PhysicalOperationHelperImpl();
        PhysicalWireGenerator wireGenerator = new PhysicalWireGeneratorImpl(generatorRegistry, new NullPolicyResolver(), physicalOperationHelper);

        ClassLoaderCommandGenerator classLoaderCommandGenerator = new ClassLoaderCommandGeneratorImpl(metaDataStore);

        List<AddCommandGenerator> commandGenerators = new ArrayList<AddCommandGenerator>();
        commandGenerators.add(new BuildComponentCommandGenerator(generatorRegistry, 1));
        commandGenerators.add(new LocalWireCommandGenerator(wireGenerator, logicalComponentManager, 2));
        commandGenerators.add(new ServiceWireCommandGenerator(wireGenerator, 2));
        commandGenerators.add(new ResourceWireCommandGenerator(wireGenerator, 2));
        commandGenerators.add(new StartComponentCommandGenerator(3));
        commandGenerators.add(new StartCompositeContextCommandGenerator(4));
        commandGenerators.add(new InitializeComponentCommandGenerator(5));
        List<RemoveCommandGenerator> removeCmdGenerator = new ArrayList<RemoveCommandGenerator>(2);
        removeCmdGenerator.add(new StopCompositeContextCommandGenerator(0));
        removeCmdGenerator.add(new StopComponentCommandGenerator(1));
        return new PhysicalModelGeneratorImpl(commandGenerators, removeCmdGenerator, classLoaderCommandGenerator);
    }

    private static GeneratorRegistry createGeneratorRegistry() {
        GeneratorRegistryImpl registry = new GeneratorRegistryImpl();
        GenerationHelperImpl helper = new GenerationHelperImpl();
        new SystemComponentGenerator(registry, helper);
        new SingletonComponentGenerator(registry);
        registry.register(JMXBinding.class, new JMXBindingGenerator());
        new MonitorWireGenerator(registry).init();
        return registry;
    }

}
