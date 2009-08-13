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

import java.net.URI;
import javax.management.MBeanServer;

import org.w3c.dom.Document;

import org.sca4j.fabric.instantiator.component.AtomicComponentInstantiator;
import org.sca4j.fabric.instantiator.component.ComponentInstantiator;
import org.sca4j.fabric.runtime.ComponentNames;
import static org.sca4j.fabric.runtime.ComponentNames.APPLICATION_CLASSLOADER_ID;
import static org.sca4j.fabric.runtime.ComponentNames.BOOT_CLASSLOADER_ID;
import static org.sca4j.fabric.runtime.ComponentNames.RUNTIME_URI;
import org.sca4j.fabric.services.documentloader.DocumentLoader;
import org.sca4j.fabric.services.documentloader.DocumentLoaderImpl;
import org.sca4j.fabric.services.synthesizer.SingletonComponentSynthesizer;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.runtime.Bootstrapper;
import org.sca4j.host.runtime.SCA4JRuntime;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.introspection.impl.contract.DefaultContractProcessor;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.scdl.Composite;
import org.sca4j.spi.classloader.MultiParentClassLoader;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.runtime.RuntimeServices;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.services.synthesize.ComponentRegistrationException;
import org.sca4j.spi.services.synthesize.ComponentSynthesizer;
import org.sca4j.system.introspection.BootstrapIntrospectionFactory;
import org.sca4j.system.introspection.SystemImplementationProcessor;

/**
 * The base Bootstrapper implementation.
 *
 * @version $Rev: 5395 $ $Date: 2008-09-13 14:35:47 +0100 (Sat, 13 Sep 2008) $
 */
public abstract class AbstractBootstrapper implements Bootstrapper {

    private static final URI HOST_CLASSLOADER_ID = URI.create("sca4j://runtime/HostClassLoader");
    private static final URI RUNTIME_SERVICES = URI.create("sca4j://RuntimeServices");

    // bootstrap components - these are disposed of after the core runtime system components are booted
    private final ContractProcessor contractProcessor;
    private final ComponentInstantiator instantiator;
    private final SystemImplementationProcessor systemImplementationProcessor;
    private ComponentSynthesizer synthesizer;

    // runtime components - these are persistent and supplied by the runtime implementation
    private MonitorFactory monitorFactory;
    private HostInfo hostInfo;
    private ClassLoaderRegistry classLoaderRegistry;
    private MetaDataStore metaDataStore;
    private ScopeRegistry scopeRegistry;
    private LogicalCompositeComponent domain;
    private LogicalComponentManager logicalComponetManager;
    private ComponentManager componentManager;
    private ScopeContainer<?> scopeContainer;

    private Domain runtimeDomain;

    private ClassLoader bootClassLoader;
    private ClassLoader hostClassLoader;

    protected AbstractBootstrapper() {
        // create components needed for to bootstrap the runtime
        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        contractProcessor = new DefaultContractProcessor(helper);
        DocumentLoader documentLoader = new DocumentLoaderImpl();
        instantiator = new AtomicComponentInstantiator(documentLoader);
        systemImplementationProcessor = BootstrapIntrospectionFactory.createSystemImplementationProcessor();
    }

    public void bootRuntimeDomain(SCA4JRuntime<?> runtime, ClassLoader bootClassLoader, ClassLoader appClassLoader) throws InitializationException {

        this.bootClassLoader = bootClassLoader;
        // classloader shared by extension and application classes
        this.hostClassLoader = runtime.getHostClassLoader();

        monitorFactory = runtime.getMonitorFactory();
        hostInfo = runtime.getHostInfo();

        RuntimeServices runtimeServices = runtime.getSystemComponent(RuntimeServices.class, RUNTIME_SERVICES);
        logicalComponetManager = runtimeServices.getLogicalComponentManager();
        componentManager = runtimeServices.getComponentManager();
        domain = logicalComponetManager.getRootComponent();
        classLoaderRegistry = runtimeServices.getClassLoaderRegistry();
        metaDataStore = runtimeServices.getMetaDataStore();
        scopeRegistry = runtimeServices.getScopeRegistry();
        scopeContainer = runtimeServices.getScopeContainer();

        synthesizer = new SingletonComponentSynthesizer(systemImplementationProcessor,
                                                        instantiator,
                                                        logicalComponetManager,
                                                        componentManager,
                                                        contractProcessor,
                                                        scopeContainer);

        // register primordial components provided by the runtime itself
        registerRuntimeComponents(runtime);

        runtimeDomain = BootstrapAssemblyFactory.createDomain(monitorFactory,
                                                              classLoaderRegistry,
                                                              scopeRegistry,
                                                              componentManager,
                                                              logicalComponetManager,
                                                              metaDataStore,
                                                              runtime.getMBeanServer(),
                                                              runtime.getJMXSubDomain(),
                                                              hostInfo);

        // create and register bootstrap components provided by this bootstrapper
        registerDomain(runtime);

        // register the classloaders
        registerClassLoaders(bootClassLoader, appClassLoader);

    }

    public void bootSystem() throws InitializationException {
        try {

            // load the system composite
            Composite composite = loadSystemComposite(BOOT_CLASSLOADER_ID, bootClassLoader, systemImplementationProcessor, monitorFactory);

            // load user configuration
            Document userConfig = loadUserConfig();
            if (userConfig != null) {
                domain.setPropertyValue("userConfig", userConfig);
            }

            // load system configuration
            Document systemConfig = loadSystemConfig();
            if (systemConfig != null) {
                domain.setPropertyValue("systemConfig", systemConfig);
            }

            // deploy the composite to the runtime domain
            runtimeDomain.include(composite);
        } catch (DeploymentException e) {
            throw new InitializationException(e);
        }

    }

    /**
     * Loads the composite that supplies core system components to the runtime.
     *
     * @param contributionUri the synthetic contrbution URI the core components are part of
     * @param bootClassLoader the classloader core components are loaded in
     * @param processor       the ImplementationProcessor for introspecting component implementations.
     * @param monitorFactory  the MonitorFactory for reporting events
     * @return the loaded composite
     * @throws InitializationException if an error occurs loading the composite
     */
    protected abstract Composite loadSystemComposite(URI contributionUri,
                                                     ClassLoader bootClassLoader,
                                                     SystemImplementationProcessor processor,
                                                     MonitorFactory monitorFactory) throws InitializationException;

    /**
     * Subclasses return a Document representing the domain-level user configuration property or null if none is defined. This property may be
     * referenced entirely or in part via XPath by end-user components in the application domain to supply configuration values.
     *
     * @return a Document representing the domain-level user configuration property or null if none is defined
     * @throws InitializationException if an error occurs loading the configuration file
     */
    protected abstract Document loadUserConfig() throws InitializationException;

    /**
     * Subclasses return a Document representing the domain-level runtime configuration property or null if none is defined. This property may be
     * referenced entirely or in part via XPath by components in the runtime domain to supply configuration values.
     *
     * @return a Document representing the domain-level user configuration property or null if none is defined
     * @throws InitializationException if an error occurs loading the configuration file
     */
    protected abstract Document loadSystemConfig() throws InitializationException;

    private <T extends HostInfo> void registerRuntimeComponents(SCA4JRuntime<T> runtime) throws InitializationException {

        // services available through the outward facing SCA4JRuntime API
        registerComponent("MonitorFactory", MonitorFactory.class, monitorFactory, true);
        registerComponent("HostInfo", runtime.getHostInfoType(), runtime.getHostInfo(), true);
        MBeanServer mbServer = runtime.getMBeanServer();
        if (mbServer != null) {
            registerComponent("MBeanServer", MBeanServer.class, mbServer, false);
        }

        // services available through the inward facing RuntimeServices SPI
        registerComponent("ComponentManager", ComponentManager.class, componentManager, true);
        registerComponent("RuntimeLogicalComponentManager", LogicalComponentManager.class, logicalComponetManager, true);
        registerComponent("CompositeScopeContainer", ScopeContainer.class, scopeContainer, true);
        registerComponent("ClassLoaderRegistry", ClassLoaderRegistry.class, classLoaderRegistry, true);

        registerComponent("ScopeRegistry", ScopeRegistry.class, scopeRegistry, true);

        registerComponent("MetaDataStore", MetaDataStore.class, metaDataStore, true);
    }

    private void registerDomain(SCA4JRuntime<?> runtime) throws InitializationException {
        registerComponent("RuntimeDomain", Domain.class, runtimeDomain, true);
        // the following is a hack to initialize the domain
        runtime.getSystemComponent(Domain.class, ComponentNames.RUNTIME_DOMAIN_URI);
    }

    private void registerClassLoaders(ClassLoader bootClassLoader, ClassLoader appClassLoader) {

        classLoaderRegistry.register(HOST_CLASSLOADER_ID, hostClassLoader);
        classLoaderRegistry.register(BOOT_CLASSLOADER_ID, bootClassLoader);
        classLoaderRegistry.register(RUNTIME_URI, new MultiParentClassLoader(RUNTIME_URI, bootClassLoader));

        URI domainId = hostInfo.getDomain();
        classLoaderRegistry.register(APPLICATION_CLASSLOADER_ID, appClassLoader);
        MultiParentClassLoader applicationClassLoader = new MultiParentClassLoader(domainId, appClassLoader);
        classLoaderRegistry.register(domainId, applicationClassLoader);
    }

    private <S, I extends S> void registerComponent(String name, Class<S> type, I instance, boolean introspect) throws InitializationException {
        try {
            synthesizer.registerComponent(name, type, instance, introspect);
        } catch (ComponentRegistrationException e) {
            throw new InitializationException(e);
        }
    }

}
