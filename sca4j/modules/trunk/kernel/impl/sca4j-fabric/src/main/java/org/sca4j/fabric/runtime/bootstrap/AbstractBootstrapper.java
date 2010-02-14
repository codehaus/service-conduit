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
package org.sca4j.fabric.runtime.bootstrap;

import static org.sca4j.fabric.runtime.ComponentNames.BOOT_CLASSLOADER_ID;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import javax.management.MBeanServer;

import org.sca4j.fabric.config.ConfigServiceImpl;
import org.sca4j.fabric.instantiator.component.AtomicComponentInstantiator;
import org.sca4j.fabric.instantiator.component.ComponentInstantiator;
import org.sca4j.fabric.runtime.ComponentNames;
import org.sca4j.fabric.services.documentloader.DocumentLoader;
import org.sca4j.fabric.services.documentloader.DocumentLoaderImpl;
import org.sca4j.fabric.services.synthesizer.SingletonComponentSynthesizer;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.runtime.Bootstrapper;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.runtime.SCA4JRuntime;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.introspection.impl.contract.DefaultContractProcessor;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.scdl.Composite;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.config.ConfigService;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.runtime.RuntimeServices;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.services.synthesize.ComponentRegistrationException;
import org.sca4j.spi.services.synthesize.ComponentSynthesizer;
import org.sca4j.system.introspection.BootstrapIntrospectionFactory;
import org.sca4j.system.introspection.SystemImplementationProcessor;
import org.w3c.dom.Document;

/**
 * The base Bootstrapper implementation.
 *
 * @version $Rev: 5395 $ $Date: 2008-09-13 14:35:47 +0100 (Sat, 13 Sep 2008) $
 */
public abstract class AbstractBootstrapper implements Bootstrapper {

    private static final URI RUNTIME_SERVICES = URI.create("sca4j://RuntimeServices");

    // bootstrap components - these are disposed of after the core runtime system components are booted
    private final ContractProcessor contractProcessor;
    private final ComponentInstantiator instantiator;
    private final SystemImplementationProcessor systemImplementationProcessor;
    private ComponentSynthesizer synthesizer;
    private InputStream systemConfig;
    
    // runtime components - these are persistent and supplied by the runtime implementation
    private MonitorFactory monitorFactory;
    private HostInfo hostInfo;
    private MetaDataStore metaDataStore;
    private ScopeRegistry scopeRegistry;
    private LogicalCompositeComponent domain;
    private LogicalComponentManager logicalComponetManager;
    private ComponentManager componentManager;
    private ScopeContainer<?> scopeContainer;

    private Domain runtimeDomain;

    private ClassLoader bootClassLoader;

    protected AbstractBootstrapper(String systemConfig) {
        if (systemConfig != null) {
            this.systemConfig = new ByteArrayInputStream(systemConfig.getBytes());
        }
        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        contractProcessor = new DefaultContractProcessor(helper);
        DocumentLoader documentLoader = new DocumentLoaderImpl();
        instantiator = new AtomicComponentInstantiator(documentLoader);
        systemImplementationProcessor = BootstrapIntrospectionFactory.createSystemImplementationProcessor();
    }

    public void bootRuntimeDomain(SCA4JRuntime<?> runtime, ClassLoader bootClassLoader, ClassLoader appClassLoader) throws InitializationException {

        this.bootClassLoader = bootClassLoader;
        
        monitorFactory = runtime.getMonitorFactory();
        hostInfo = runtime.getHostInfo();

        RuntimeServices runtimeServices = runtime.getSystemComponent(RuntimeServices.class, RUNTIME_SERVICES);
        logicalComponetManager = runtimeServices.getLogicalComponentManager();
        componentManager = runtimeServices.getComponentManager();
        domain = logicalComponetManager.getRootComponent();
        metaDataStore = runtimeServices.getMetaDataStore();
        scopeRegistry = runtimeServices.getScopeRegistry();
        scopeContainer = runtimeServices.getScopeContainer();

        synthesizer = new SingletonComponentSynthesizer(systemImplementationProcessor, instantiator, logicalComponetManager, componentManager, contractProcessor, scopeContainer);

        // register primordial components provided by the runtime itself
        registerRuntimeComponents(runtime);

        runtimeDomain = BootstrapAssemblyFactory.createDomain(monitorFactory,
                                                              scopeRegistry,
                                                              componentManager,
                                                              logicalComponetManager,
                                                              metaDataStore,
                                                              runtime.getMBeanServer(),
                                                              runtime.getJMXSubDomain(),
                                                              hostInfo);

        // create and register bootstrap components provided by this bootstrapper
        registerDomain(runtime);

    }

    public void bootSystem() throws InitializationException {
        try {

            // load the system composite
            Composite composite = loadSystemComposite(BOOT_CLASSLOADER_ID, bootClassLoader, systemImplementationProcessor, monitorFactory);
            
            ConfigService configService = new ConfigServiceImpl(this.systemConfig);
            for (String propertyName: configService.getPropertyNames()) {
                hostInfo.addProperty(propertyName, configService.getHostProperty(propertyName));
            }
            Document domainConfig = configService.getDomainConfig();
            if (domainConfig == null) {
                Thread.dumpStack();
            }
            domain.setPropertyValue("config", domainConfig);
            

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

        registerComponent("ScopeRegistry", ScopeRegistry.class, scopeRegistry, true);

        registerComponent("MetaDataStore", MetaDataStore.class, metaDataStore, true);
    }

    private void registerDomain(SCA4JRuntime<?> runtime) throws InitializationException {
        registerComponent("RuntimeDomain", Domain.class, runtimeDomain, true);
        // the following is a hack to initialize the domain
        runtime.getSystemComponent(Domain.class, ComponentNames.RUNTIME_DOMAIN_URI);
    }

    private <S, I extends S> void registerComponent(String name, Class<S> type, I instance, boolean introspect) throws InitializationException {
        try {
            synthesizer.registerComponent(name, type, instance, introspect);
        } catch (ComponentRegistrationException e) {
            throw new InitializationException(e);
        }
    }

}
