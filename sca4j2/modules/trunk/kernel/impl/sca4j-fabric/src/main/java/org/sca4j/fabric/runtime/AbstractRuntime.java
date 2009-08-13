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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.fabric.runtime;

import static org.sca4j.fabric.runtime.ComponentNames.CONTRIBUTION_SERVICE_URI;
import static org.sca4j.fabric.runtime.ComponentNames.DEFINITIONS_REGISTRY;
import static org.sca4j.fabric.runtime.ComponentNames.DISCOVERY_SERVICE_URI;
import static org.sca4j.fabric.runtime.ComponentNames.EVENT_SERVICE_URI;
import static org.sca4j.fabric.runtime.ComponentNames.METADATA_STORE_URI;
import static org.sca4j.fabric.runtime.ComponentNames.RUNTIME_DOMAIN_URI;
import static org.sca4j.fabric.runtime.ComponentNames.RUNTIME_URI;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.xml.namespace.QName;

import org.sca4j.fabric.component.scope.CompositeScopeContainer;
import org.sca4j.fabric.component.scope.ScopeContainerMonitor;
import org.sca4j.fabric.component.scope.ScopeRegistryImpl;
import org.sca4j.fabric.runtime.bootstrap.ScdlBootstrapperImpl;
import org.sca4j.fabric.services.classloading.ClassLoaderRegistryImpl;
import org.sca4j.fabric.services.componentmanager.ComponentManagerImpl;
import org.sca4j.fabric.services.contribution.MetaDataStoreImpl;
import org.sca4j.fabric.services.contribution.ProcessorRegistryImpl;
import org.sca4j.fabric.services.contribution.manifest.XmlManifestProcessor;
import org.sca4j.fabric.services.lcm.LogicalComponentManagerImpl;
import org.sca4j.fabric.services.lcm.NonPersistentLogicalComponentStore;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ContributionService;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.Deployable;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.host.runtime.Bootstrapper;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.runtime.SCA4JRuntime;
import org.sca4j.host.runtime.StartException;
import org.sca4j.introspection.validation.InvalidContributionException;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.Include;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.runtime.RuntimeServices;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.spi.services.definitions.DefinitionActivationException;
import org.sca4j.spi.services.definitions.DefinitionsRegistry;
import org.sca4j.spi.services.discovery.DiscoveryException;
import org.sca4j.spi.services.discovery.DiscoveryService;
import org.sca4j.spi.services.event.EventService;
import org.sca4j.spi.services.event.RuntimeStart;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.services.lcm.LogicalComponentStore;
import org.sca4j.spi.services.lcm.RecoveryException;

/**
 * @version $Rev: 5265 $ $Date: 2008-08-25 09:30:09 +0100 (Mon, 25 Aug 2008) $
 */
public abstract class AbstractRuntime<HI extends HostInfo> implements SCA4JRuntime<HI>, RuntimeServices {
    
    private Class<HI> hostInfoType;
    private MBeanServer mbServer;
    private String jmxSubDomain;
    private ClassLoader bootClassLoader;
    private ClassLoader appClassLoader;
    private List<String> bootExports;
    private ContributionSource intents;
    private List<ContributionSource> extensions;
    private Bootstrapper bootstrapper;

    private HI hostInfo;
    private MonitorFactory monitorFactory;
    private LogicalComponentManager logicalComponentManager;
    private ComponentManager componentManager;
    private CompositeScopeContainer scopeContainer;
    private ClassLoaderRegistry classLoaderRegistry;
    private MetaDataStore metaDataStore;
    private ScopeRegistry scopeRegistry;
    private ClassLoader hostClassLoader;

    protected AbstractRuntime(Class<HI> runtimeInfoType) {
        this.hostInfoType = runtimeInfoType;
    }

    public void bootPrimordial(BootConfiguration configuration) throws InitializationException {

        bootClassLoader = configuration.getBootClassLoader();
        appClassLoader = configuration.getAppClassLoader();
        hostClassLoader = configuration.getHostClassLoader();
        
        bootExports = configuration.getBootLibraryExports();
        intents = configuration.getIntents();
        extensions = configuration.getExtensions();
        bootstrapper = new ScdlBootstrapperImpl(configuration.getSystemScdl(), configuration.getSystemConfig(), configuration.getSystemConfigDocument());
        
        LogicalComponentStore store = new NonPersistentLogicalComponentStore(RUNTIME_URI, Autowire.ON);
        logicalComponentManager = new LogicalComponentManagerImpl(store);
        try {
            logicalComponentManager.initialize();
        } catch (RecoveryException e) {
            throw new InitializationException(e);
        }
        
        componentManager = new ComponentManagerImpl();
        classLoaderRegistry = new ClassLoaderRegistryImpl();
        ProcessorRegistry processorRegistry = new ProcessorRegistryImpl();
        metaDataStore = new MetaDataStoreImpl(classLoaderRegistry, processorRegistry);
        
        scopeContainer = new CompositeScopeContainer(getMonitorFactory().getMonitor(ScopeContainerMonitor.class));
        scopeContainer.start();
        
        scopeRegistry = new ScopeRegistryImpl();
        scopeRegistry.register(scopeContainer);
        bootstrapper.bootRuntimeDomain(this, bootClassLoader, appClassLoader);
        
        startRuntimeDomainContext();
        
    }


    public void bootSystem() throws InitializationException {

        bootstrapper.bootSystem();
        synthesizeBootContribution();
        try {
            activateIntents(intents);
            includeExtensions(extensions);
        } catch (DefinitionActivationException e) {
            throw new InitializationException(e);
        }

    }

    public void joinDomain(final long timeout) throws InitializationException {
        startApplicationDomainContext();
        DiscoveryService discoveryService = getSystemComponent(DiscoveryService.class, DISCOVERY_SERVICE_URI);
        try {
            discoveryService.joinDomain(timeout);
        } catch (DiscoveryException e) {
            throw new InitializationException(e);
        }
    }

    public void start() throws StartException {
        EventService eventService = getSystemComponent(EventService.class, EVENT_SERVICE_URI);
        eventService.publish(new RuntimeStart());
    }

    public void shutdown() {
        scopeContainer.stopAllContexts(new WorkContext());
    }

    public <I> I getSystemComponent(Class<I> service, URI uri) {
        
        if (RuntimeServices.class.equals(service)) {
            return service.cast(this);
        }
        
        AtomicComponent<?> component = (AtomicComponent<?>) componentManager.getComponent(uri);
        if (component == null) {
            return null;
        }

        WorkContext workContext = new WorkContext();
        WorkContext oldContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            InstanceWrapper<?> wrapper = scopeContainer.getWrapper(component, workContext);
            return service.cast(wrapper.getInstance());
        } catch (InstanceLifecycleException e) {
            // FIXME throw something better
            throw new AssertionError();
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(oldContext);
        }
        
    }

    public ClassLoader getHostClassLoader() {
        return hostClassLoader;
    }

    public void setHostClassLoader(ClassLoader hostClassLoader) {
        this.hostClassLoader = hostClassLoader;
    }

    public Class<HI> getHostInfoType() {
        return hostInfoType;
    }

    public HI getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(HI hostInfo) {
        this.hostInfo = hostInfo;
    }

    public MonitorFactory getMonitorFactory() {
        return monitorFactory;
    }

    public void setMonitorFactory(MonitorFactory monitorFactory) {
        this.monitorFactory = monitorFactory;
    }

    public MBeanServer getMBeanServer() {
        return mbServer;
    }

    public void setMBeanServer(MBeanServer mbServer) {
        this.mbServer = mbServer;
    }

    public String getJMXSubDomain() {
        return jmxSubDomain;
    }

    public void setJmxSubDomain(String jmxDomain) {
        this.jmxSubDomain = jmxDomain;
    }
    public LogicalComponentManager getLogicalComponentManager() {
        return logicalComponentManager;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public ScopeContainer<?> getScopeContainer() {
        return scopeContainer;
    }

    public ClassLoaderRegistry getClassLoaderRegistry() {
        return classLoaderRegistry;
    }

    public MetaDataStore getMetaDataStore() {
        return metaDataStore;
    }

    public ScopeRegistry getScopeRegistry() {
        return scopeRegistry;
    }

    private void activateIntents(ContributionSource source) throws InitializationException {
        try {
            ContributionService contributionService = getSystemComponent(ContributionService.class, CONTRIBUTION_SERVICE_URI);
            URI uri = contributionService.contribute(source);
            DefinitionsRegistry definitionsRegistry = getSystemComponent(DefinitionsRegistry.class, DEFINITIONS_REGISTRY);
            List<URI> intents = new ArrayList<URI>();
            intents.add(uri);
            definitionsRegistry.activateDefinitions(intents);
        } catch (ContributionException e) {
            throw new InitializationException(e);
        } catch (DefinitionActivationException e) {
            throw new InitializationException(e);
        }
    }

    private void synthesizeBootContribution() throws InitializationException {
        try {
            assert !bootExports.isEmpty();
            XmlManifestProcessor processor = getSystemComponent(XmlManifestProcessor.class, ComponentNames.XML_MANIFEST_PROCESSOR);
            Contribution contribution = new Contribution(ComponentNames.BOOT_CLASSLOADER_ID);
            ContributionManifest manifest = new ContributionManifest();

            ValidationContext context = new DefaultValidationContext();
            for (String export : bootExports) {
                InputStream stream =
                        bootClassLoader.getResourceAsStream(export);
                if (stream == null) {
                    throw new InitializationException("boot jar is missing a pom.xml: " + export);
                }
                processor.process(manifest, stream, context);
            }
            if (context.hasErrors()) {
                throw new InvalidContributionException(context.getErrors(), context.getWarnings());
            }
            contribution.setManifest(manifest);
            MetaDataStore store = getSystemComponent(MetaDataStore.class, ComponentNames.METADATA_STORE_URI);
            store.store(contribution);
        } catch (MetaDataStoreException e) {
            throw new InitializationException(e);
        } catch (ContributionException e) {
            throw new InitializationException(e);
        }
    }

    private void includeExtensions(List<ContributionSource> sources) throws InitializationException, DefinitionActivationException {
        try {
            ContributionService contributionService = getSystemComponent(ContributionService.class, CONTRIBUTION_SERVICE_URI);
            List<URI> contributionUris = contributionService.contribute(sources);
            includeExtensionContributions(contributionUris);
            DefinitionsRegistry definitionsRegistry = getSystemComponent(DefinitionsRegistry.class, DEFINITIONS_REGISTRY);
            definitionsRegistry.activateDefinitions(contributionUris);
        } catch (ContributionException e) {
            throw new ExtensionInitializationException("Error contributing extensions", e);
        }
    }

    private void includeExtensionContributions(List<URI> contributionUris) throws InitializationException {
        Domain domain = getSystemComponent(Domain.class, RUNTIME_DOMAIN_URI);
        Composite composite = createExtensionComposite(contributionUris);
        try {
            domain.include(composite);
        } catch (DeploymentException e) {
            throw new ExtensionInitializationException("Error activating extensions", e);
        }
    }
    
    private Composite createExtensionComposite(List<URI> contributionUris) throws InitializationException {
        MetaDataStore metaDataStore = getSystemComponent(MetaDataStore.class, METADATA_STORE_URI);
        if (metaDataStore == null) {
            String id = METADATA_STORE_URI.toString();
            throw new InitializationException("Extensions metadata store not configured: " + id, id);
        }
        QName qName = new QName(org.sca4j.host.Namespaces.SCA4J_NS, "extension");
        Composite composite = new Composite(qName);
        for (URI uri : contributionUris) {
            Contribution contribution = metaDataStore.find(uri);
            assert contribution != null;

            for (Resource resource : contribution.getResources()) {
                for (ResourceElement<?, ?> entry : resource.getResourceElements()) {

                    if (!(entry.getValue() instanceof Composite)) {
                        continue;
                    }
                    @SuppressWarnings({"unchecked"})
                    ResourceElement<QNameSymbol, Composite> element = (ResourceElement<QNameSymbol, Composite>) entry;
                    QName name = element.getSymbol().getKey();
                    Composite childComposite = element.getValue();
                    for (Deployable deployable : contribution.getManifest().getDeployables()) {
                        if (deployable.getName().equals(name)) {
                            Include include = new Include();
                            include.setName(name);
                            include.setIncluded(childComposite);
                            composite.add(include);
                            break;
                        }
                    }
                }
            }
        }
        return composite;
    }

    private void startRuntimeDomainContext() throws InitializationException {
        
        try {
            WorkContext workContext = new WorkContext();
            CallFrame frame = new CallFrame(ComponentNames.RUNTIME_URI);
            workContext.addCallFrame(frame);
            scopeContainer.startContext(workContext);
            workContext.popCallFrame();
        } catch (GroupInitializationException e) {
            throw new InitializationException(e);
        }
        
    }

    public void startApplicationDomainContext() throws InitializationException {
        
        try {
            URI groupId = getHostInfo().getDomain();
            WorkContext workContext = new WorkContext();
            CallFrame frame = new CallFrame(groupId);
            workContext.addCallFrame(frame);
            scopeContainer.startContext(workContext);
            workContext.popCallFrame();
        } catch (GroupInitializationException e) {
            throw new InitializationException(e);
        }
        
    }
    
}
