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
package org.sca4j.maven.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.maven.surefire.testset.TestSetFailedException;

import org.sca4j.fabric.runtime.AbstractRuntime;
import static org.sca4j.fabric.runtime.ComponentNames.CONTRIBUTION_SERVICE_URI;
import static org.sca4j.fabric.runtime.ComponentNames.APPLICATION_DOMAIN_URI;
import static org.sca4j.fabric.runtime.ComponentNames.XML_FACTORY_URI;
import org.sca4j.fabric.util.FileHelper;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ContributionService;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.maven.contribution.ModuleContributionSource;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.pojo.component.InvokerInterceptor;
import org.sca4j.pojo.component.PojoComponent;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.Operation;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.ResourceElement;

/**
 * Default Maven runtime implementation.
 *
 * @version $Rev: 5276 $ $Date: 2008-08-26 05:40:44 +0100 (Tue, 26 Aug 2008) $
 */
public class MavenEmbeddedRuntimeImpl extends AbstractRuntime<MavenHostInfo> implements MavenEmbeddedRuntime {
    public MavenEmbeddedRuntimeImpl() {
        super(MavenHostInfo.class);
    }

    public Composite activate(URL url, QName qName) throws ContributionException, DeploymentException {
        try {
            URI contributionUri = url.toURI();
            ModuleContributionSource source =
                    new ModuleContributionSource(contributionUri, FileHelper.toFile(url).toString());
            return activate(source, qName);
        } catch (MalformedURLException e) {
            String identifier = url.toString();
            throw new DeploymentException("Invalid project directory: " + identifier, identifier, e);
        } catch (URISyntaxException e) {
            throw new DeploymentException("Error activating test contribution", e);
        }
    }

    public Composite activate(ContributionSource source, QName qName) throws ContributionException, DeploymentException {
        // contribute the Maven project to the application domain
        Domain domain = getSystemComponent(Domain.class, APPLICATION_DOMAIN_URI);
        ContributionService contributionService =
                getSystemComponent(ContributionService.class, CONTRIBUTION_SERVICE_URI);
        contributionService.contribute(source);
        // activate the deployable composite in the domain
        domain.include(qName);
        ResourceElement<?, ?> element = getMetaDataStore().resolve(new QNameSymbol(qName));
        assert element != null;
        return (Composite) element.getValue();
    }

    public Composite activate(URL url, URL scdlLocation) throws ContributionException, DeploymentException {
        QName name;
        try {
            name = parseCompositeQName(scdlLocation);
        } catch (IOException e) {
            throw new ContributionException(e);
        } catch (XMLStreamException e) {
            throw new ContributionException(e);
        }
        return activate(url, name);
    }

    public void startContext(URI groupId) throws ContextStartException {
        WorkContext workContext = new WorkContext();
        CallFrame frame = new CallFrame(groupId);
        workContext.addCallFrame(frame);
        try {
            getScopeContainer().startContext(workContext);
        } catch (GroupInitializationException e) {
            throw new ContextStartException(e);
        }
    }

    @SuppressWarnings({"unchecked"})
    public void executeTest(URI contextId, String componentName, Operation<?> operation) throws TestSetFailedException {
        WorkContext oldContext = PojoWorkContextTunnel.getThreadWorkContext();
        try {
            WorkContext workContext = new WorkContext();
            CallFrame frame = new CallFrame();
            workContext.addCallFrame(frame);
            URI componentId = URI.create(contextId.toString() + "/" + componentName);

            // FIXME we should not be creating a InvokerInterceptor here
            // FIXME this should create a wire to the JUnit component and invoke the head interceptor on the chain
            PojoComponent component = (PojoComponent) getComponentManager().getComponent(componentId);
            PojoWorkContextTunnel.setThreadWorkContext(workContext);
            Object instance = component.createObjectFactory().getInstance();
            Method m = instance.getClass().getMethod(operation.getName());
            ScopeContainer scopeContainer = component.getScopeContainer();
            InvokerInterceptor<?, ?> interceptor = new InvokerInterceptor(m, false, false, component, scopeContainer);

            Message msg = new MessageImpl();
            msg.setWorkContext(workContext);
            Message response = interceptor.invoke(msg);
            if (response.isFault()) {
                throw new TestSetFailedException(operation.getName(), (Throwable) response.getBody());
            }
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        } catch (ObjectCreationException e) {
            throw new AssertionError(e);
        } finally {
            PojoWorkContextTunnel.setThreadWorkContext(oldContext);
        }
    }

    /**
     * Determines a composite's QName.
     * <p/>
     * This method preserves backward compatibility for specifying SCDL location in an iTest plugin configuration.
     *
     * @param url the SCDL location
     * @return the composite QName
     * @throws IOException        if an error occurs opening the composite file
     * @throws XMLStreamException if an error occurs processing the composite
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
