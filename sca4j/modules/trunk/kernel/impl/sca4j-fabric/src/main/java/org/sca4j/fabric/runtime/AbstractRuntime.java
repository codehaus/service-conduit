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

import java.net.URI;
import java.util.List;

import javax.management.MBeanServer;
import javax.xml.namespace.QName;

import org.sca4j.fabric.component.scope.CompositeScopeContainer;
import org.sca4j.fabric.component.scope.ScopeContainerMonitor;
import org.sca4j.fabric.component.scope.ScopeRegistryImpl;
import org.sca4j.fabric.runtime.bootstrap.ScdlBootstrapperImpl;
import org.sca4j.fabric.services.componentmanager.ComponentManagerImpl;
import org.sca4j.fabric.services.contribution.ContributionScanner;
import org.sca4j.fabric.services.contribution.MetaDataStoreImpl;
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
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.monitor.impl.JavaLoggingMonitorFactory;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.Include;
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
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.contribution.CompositeResourceElement;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.Resource;
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
    private Bootstrapper bootstrapper;

    private HI hostInfo;
    private MonitorFactory monitorFactory;
    private LogicalComponentManager logicalComponentManager;
    private ComponentManager componentManager;
    private CompositeScopeContainer scopeContainer;
    private MetaDataStore metaDataStore;
    private ScopeRegistry scopeRegistry;
    private ClassLoader hostClassLoader;
    private ContributionScanner contributionScanner = new ContributionScanner();
    private boolean shutdown;

    protected AbstractRuntime(Class<HI> runtimeInfoType) {
        this.hostInfoType = runtimeInfoType;
    }

    public void bootPrimordial(BootConfiguration configuration) throws InitializationException {

        bootClassLoader = configuration.getBootClassLoader();
        appClassLoader = configuration.getAppClassLoader();
        hostClassLoader = configuration.getHostClassLoader();

        bootstrapper = new ScdlBootstrapperImpl(configuration.getSystemScdl(), configuration.getSystemConfig());

        LogicalComponentStore store = new NonPersistentLogicalComponentStore(RUNTIME_URI, Autowire.ON);
        logicalComponentManager = new LogicalComponentManagerImpl(store);
        try {
            logicalComponentManager.initialize();
        } catch (RecoveryException e) {
            throw new InitializationException(e);
        }

        componentManager = new ComponentManagerImpl();
        metaDataStore = new MetaDataStoreImpl();

        scopeContainer = new CompositeScopeContainer(getMonitorFactory().getMonitor(ScopeContainerMonitor.class));
        scopeContainer.start();

        scopeRegistry = new ScopeRegistryImpl();
        scopeRegistry.register(scopeContainer);
        bootstrapper.bootRuntimeDomain(this, bootClassLoader, appClassLoader);

        startRuntimeDomainContext();

    }

    public void bootSystem() throws InitializationException {
        bootstrapper.bootSystem();
        try {
            includeExtensions();
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
        scanUserContributions();
    }

    public void shutdown() {
        shutdown = true;
        if (scopeContainer != null) {
            scopeContainer.stopAllContexts(new WorkContext());
        }
    }
    
    public boolean isShutdown() {
        return shutdown;
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
            throw new AssertionError(e);
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
        if (monitorFactory == null) {
            this.monitorFactory = new JavaLoggingMonitorFactory();
        }
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

    public MetaDataStore getMetaDataStore() {
        return metaDataStore;
    }

    public ScopeRegistry getScopeRegistry() {
        return scopeRegistry;
    }

    private void scanUserContributions() throws StartException {
        try {
            ContributionSource[] userContributions = contributionScanner.scanUserContributions();
            ContributionService contributionService = getSystemComponent(ContributionService.class, CONTRIBUTION_SERVICE_URI);
            List<URI> contributionUris = contributionService.contribute(userContributions);
            DefinitionsRegistry definitionsRegistry = getSystemComponent(DefinitionsRegistry.class, DEFINITIONS_REGISTRY);
            definitionsRegistry.activateDefinitions(contributionUris);
        } catch (ContributionException e) {
            throw new StartException("Error contributing user code", e);
        } catch (DefinitionActivationException e) {
            throw new StartException("Error contributing user code", e);
        }
    }

    private void includeExtensions() throws InitializationException, DefinitionActivationException {
        try {
            ContributionSource[] extensions = contributionScanner.scanExtensionContributions();
            ContributionService contributionService = getSystemComponent(ContributionService.class, CONTRIBUTION_SERVICE_URI);
            List<URI> contributionUris = contributionService.contribute(extensions);
            Domain domain = getSystemComponent(Domain.class, RUNTIME_DOMAIN_URI);
            Composite composite = createExtensionComposite(contributionUris);
            domain.include(composite);
            DefinitionsRegistry definitionsRegistry = getSystemComponent(DefinitionsRegistry.class, DEFINITIONS_REGISTRY);
            definitionsRegistry.activateDefinitions(contributionUris);
        } catch (ContributionException e) {
            throw new ExtensionInitializationException("Error contributing extensions", e);
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
            for (Resource resource : contribution.getResources()) {
                for (CompositeResourceElement element : resource.getResourceElements(CompositeResourceElement.class)) {
                    QName name = element.getSymbol();
                    Composite childComposite = element.getComposite();
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
