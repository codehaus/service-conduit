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

import java.net.URI;
import javax.management.MBeanServer;

import org.sca4j.fabric.component.scope.CompositeScopeContainer;
import org.sca4j.fabric.component.scope.ScopeContainerMonitor;
import org.sca4j.fabric.component.scope.ScopeRegistryImpl;
import static org.sca4j.fabric.runtime.ComponentNames.EVENT_SERVICE_URI;
import static org.sca4j.fabric.runtime.ComponentNames.RUNTIME_URI;
import org.sca4j.fabric.services.classloading.ClassLoaderRegistryImpl;
import org.sca4j.fabric.services.componentmanager.ComponentManagerImpl;
import org.sca4j.fabric.services.contribution.MetaDataStoreImpl;
import org.sca4j.fabric.services.contribution.ProcessorRegistryImpl;
import org.sca4j.fabric.services.lcm.LogicalComponentManagerImpl;
import org.sca4j.fabric.services.lcm.NonPersistentLogicalComponentStore;
import org.sca4j.host.runtime.SCA4JRuntime;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.runtime.StartException;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.scdl.Autowire;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.runtime.RuntimeServices;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
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
    private WorkScheduler workScheduler;

    /**
     * Information provided by the host about its runtime environment.
     */
    protected HI hostInfo;

    /**
     * MonitorFactory provided by the host for directing events to its management framework.
     */
    protected MonitorFactory monitorFactory;

    /**
     * The LogicalComponentManager that manages all logical components in this runtime.
     */
    protected LogicalComponentManager logicalComponentManager;

    /**
     * The ComponentManager that manages all physical components in this runtime.
     */
    protected ComponentManager componentManager;

    /**
     * The ScopeContainer used to managed system component instances.
     */
    protected CompositeScopeContainer scopeContainer;

    /**
     * The ClassLoaderRegristy that manages all runtime classloaders.
     */
    protected ClassLoaderRegistry classLoaderRegistry;

    /**
     * The MetaDataStore that indexes contribution metadata and artifacts.
     */
    protected MetaDataStore metaDataStore;

    /**
     * The ScopeRegistry that manages runtime ScopeContainers
     */
    protected ScopeRegistry scopeRegistry;

    protected ClassLoader hostClassLoader;


    protected AbstractRuntime(Class<HI> runtimeInfoType, MonitorFactory monitorFactory) {
        this.hostInfoType = runtimeInfoType;
        this.monitorFactory = monitorFactory;
    }

    protected AbstractRuntime(Class<HI> runtimeInfoType) {
        this.hostInfoType = runtimeInfoType;
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

    public void initialize() throws InitializationException {
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
    }

    public void startRuntimeDomainContext() throws InitializationException {
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

    public void start() throws StartException {
        // starts the runtime by publishing a start event
        EventService eventService = getSystemComponent(EventService.class, EVENT_SERVICE_URI);
        eventService.publish(new RuntimeStart());
    }

    public void destroy() {
        // destroy system components
        WorkContext workContext = new WorkContext();
        CallFrame frame = new CallFrame(ComponentNames.RUNTIME_URI);
        workContext.addCallFrame(frame);
       scopeContainer.stopContext(workContext);
        
       //scopeContainer.stopAllContexts(workContext);
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

    public WorkScheduler getWorkScheduler() {
        return workScheduler;
    }

    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }
}
