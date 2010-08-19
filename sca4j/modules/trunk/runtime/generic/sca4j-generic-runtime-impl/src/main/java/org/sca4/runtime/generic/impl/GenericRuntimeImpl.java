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
package org.sca4.runtime.generic.impl;

import static org.sca4j.fabric.runtime.ComponentNames.APPLICATION_DOMAIN_URI;
import static org.sca4j.fabric.runtime.ComponentNames.XML_FACTORY_URI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4.runtime.generic.impl.policy.PolicyDecorator;
import org.sca4j.fabric.runtime.AbstractRuntime;
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.introspection.impl.contract.JavaServiceContract;
import org.sca4j.java.runtime.JavaComponent;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.runtime.generic.GenericHostInfo;
import org.sca4j.runtime.generic.GenericRuntime;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;
import org.sca4j.spi.binding.BindingProxyProviderRegistry;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.InstanceWrapper;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.services.lcm.LogicalComponentManager;

/**
 * Default implementation of the generic runtime.
 * 
 * @author meerajk
 *
 */
public class GenericRuntimeImpl extends AbstractRuntime<GenericHostInfo> implements GenericRuntime {

    /**
     * Creates a new runtime.
     * 
     * @param domain Domain to use.
     * @param hostProperties Host properties to use.
     * @param monitorFactory Monitor factory to use.
     * @param mBeanServer MBean server to use.
     */
    public GenericRuntimeImpl(URI domain, Properties hostProperties, MonitorFactory monitorFactory, MBeanServer mBeanServer) {
        super(GenericHostInfo.class);
        setHostInfo(new GenericHostInfo(domain, hostProperties));
        setMBeanServer(mBeanServer);
        setMonitorFactory(monitorFactory);
    }
    
    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#contriute(java.lang.String)
     */
    public void contriute(String scdlPath) {
        
        try {
            
            // ContributionService contributionService = getSystemComponent(ContributionService.class, ComponentNames.CONTRIBUTION_SERVICE_URI);
            
            URL applicationScdlUrl = getClass().getClassLoader().getResource(scdlPath);
            
            applicationScdlUrl = applicationScdlUrl.toURI().toURL();
            QName compositeQName = parseCompositeQName(applicationScdlUrl);
            
            //URL baseUrl = new URL(applicationScdlUrl.toString().substring(0, applicationScdlUrl.toString().indexOf(scdlPath)));
            
            // ContributionSource contributionSource = new FileContributionSource(baseUrl, -1, "application/vnd.sca4j");
            // contributionService.contribute(contributionSource);
            Domain domain = getSystemComponent(Domain.class, APPLICATION_DOMAIN_URI);
            domain.include(compositeQName);

            WorkContext workContext = new WorkContext();
            CallFrame frame = new CallFrame(URI.create("test"));
            workContext.addCallFrame(frame);
            getScopeContainer().startContext(workContext);
            publishRuntimeStartedEvent();
            
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#boot()
     */
    public void boot() {
        
        try {
            BootConfiguration bootConfiguration = getBootConfiguration();
            bootPrimordial(bootConfiguration);
            bootSystem();
            joinDomain(-1);
            start();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        
    }
    
    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#getBinding(java.lang.Class, javax.xml.namespace.QName, javax.xml.namespace.QName[])
     */
    public <T> T getBinding(Class<T> endpointInterface, QName bindingType, URI endpointUri, QName ... intents) {
        BindingProxyProviderRegistry providerRegistry = 
            getSystemComponent(BindingProxyProviderRegistry.class, URI.create("sca4j://runtime/BindingProxyProviderRegistry"));
        return providerRegistry.getBinding(bindingType, endpointInterface, endpointUri, intents);
    }
    
    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#getServices()
     */
    public List<String> getServices() {
        
        LogicalComponentManager logicalComponentManager = 
            getSystemComponent(LogicalComponentManager.class, URI.create("sca4j://runtime/LogicalComponentManager"));
        LogicalCompositeComponent domainComponent = logicalComponentManager.getRootComponent();
        
        List<String> services = new LinkedList<String>();
        for (LogicalService logicalService : domainComponent.getServices()) {
            services.add(logicalService.getUri().toASCIIString().substring(1));
        }
        
        return services;
        
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#getServiceProxy(javax.xml.namespace.QName)
     */
    @SuppressWarnings("unchecked")
    public <T> T getServiceProxy(String serviceName) {
        
        LogicalComponentManager logicalComponentManager = 
            getSystemComponent(LogicalComponentManager.class, URI.create("sca4j://runtime/LogicalComponentManager"));
        LogicalCompositeComponent domainComponent = logicalComponentManager.getRootComponent();
        
        LogicalComponent<?> logicalComponent = getUri(domainComponent, serviceName);
        JavaServiceContract serviceContract = (JavaServiceContract) getServiceContract(domainComponent, serviceName);
        URI uri = logicalComponent.getUri();
        
        JavaComponent<?> javaComponent = (JavaComponent<?>) getComponentManager().getComponent(uri);
        WorkContext workContext = new WorkContext();
        WorkContext oldContext = PojoWorkContextTunnel.setThreadWorkContext(workContext);
        try {
            InstanceWrapper<?> wrapper = javaComponent.getScopeContainer().getWrapper(javaComponent, workContext);
            T instance = (T) wrapper.getInstance();
            List<QName> intents = logicalComponent.getIntents();
            if (intents == null) {
                return instance;
            } else {
                PolicyDecorator policyDecorator = getSystemComponent(PolicyDecorator.class, URI.create("sca4j://runtime/PolicyDecorator"));
                return policyDecorator.getDecoratedService(instance, serviceContract.getQualifiedInterfaceName(), intents);
            }
        } catch (InstanceLifecycleException e) {
            throw new AssertionError();
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(oldContext);
        }
        
    }
    
    /*
     * Get to the normalized URI.
     */
    private LogicalComponent<?> getUri(LogicalCompositeComponent parent, String serviceName) {
        
        LogicalService logicalService = parent.getService(serviceName);
        URI promotedUri = logicalService.getPromotedUri();
        String fragment = promotedUri.getFragment();
        URI componentUri = URI.create(promotedUri.toString().substring(0, promotedUri.toString().indexOf('#')));
        
        LogicalComponent<?> logicalComponent = parent.getComponent(componentUri);
        if (logicalComponent instanceof LogicalCompositeComponent) {
            LogicalCompositeComponent logicalCompositeComponent = (LogicalCompositeComponent) logicalComponent;
            return getUri(logicalCompositeComponent, fragment);
        }
        return logicalComponent;
        
    }
    
    /*
     * Get to the normalized service contract.
     */
    private ServiceContract getServiceContract(LogicalCompositeComponent parent, String serviceName) {
        
        LogicalService logicalService = parent.getService(serviceName);
        URI promotedUri = logicalService.getPromotedUri();
        String fragment = promotedUri.getFragment();
        URI componentUri = URI.create(promotedUri.toString().substring(0, promotedUri.toString().indexOf('#')));
        
        LogicalComponent<?> logicalComponent = parent.getComponent(componentUri);
        if (logicalComponent instanceof LogicalCompositeComponent) {
            LogicalCompositeComponent logicalCompositeComponent = (LogicalCompositeComponent) logicalComponent;
            return getServiceContract(logicalCompositeComponent, fragment);
        }
        return logicalComponent.getService(fragment).getDefinition().getServiceContract();
        
    }
    
    /*
     * Returns the boot configuration.
     */
    private BootConfiguration getBootConfiguration() throws IOException, XMLFactoryInstantiationException, XMLStreamException, URISyntaxException {
        
        BootConfiguration bootConfiguration = new BootConfiguration();
        
        ClassLoader classLoader = getClass().getClassLoader();
        bootConfiguration.setAppClassLoader(classLoader);
        bootConfiguration.setBootClassLoader(classLoader);
        bootConfiguration.setHostClassLoader(classLoader);
        
        bootConfiguration.setRuntime(this);
        
        bootConfiguration.setSystemConfig(classLoader.getResourceAsStream("META-INF/systemConfig.xml"));
        bootConfiguration.setSystemScdl(classLoader.getResource("META-INF/system.composite"));
        
        return bootConfiguration;
        
    }
    
    /*
     * Gets the composite QName.
     */
    private QName parseCompositeQName(URL url) throws IOException, XMLStreamException {
        
        XMLStreamReader reader = null;
        InputStream stream = null;
        try {
            stream = url.openStream();
            XMLFactory xmlFactory = getSystemComponent(XMLFactory.class, XML_FACTORY_URI);
            reader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(stream);
            reader.nextTag();
            String name = reader.getAttributeValue(null, "name");
            String targetNamespace = reader.getAttributeValue(null, "targetNamespace");
            return new QName(targetNamespace, name);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

    }

}
