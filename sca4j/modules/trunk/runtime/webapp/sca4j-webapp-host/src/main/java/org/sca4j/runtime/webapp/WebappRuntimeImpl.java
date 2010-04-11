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
package org.sca4j.runtime.webapp;

import static org.sca4j.fabric.runtime.ComponentNames.APPLICATION_DOMAIN_URI;
import static org.sca4j.fabric.runtime.ComponentNames.CONTRIBUTION_SERVICE_URI;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.xml.namespace.QName;

import org.sca4j.container.web.spi.WebRequestTunnel;
import org.sca4j.fabric.runtime.AbstractRuntime;
import org.sca4j.fabric.runtime.ComponentNames;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ContributionService;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.runtime.webapp.contribution.WarContributionSource;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Bootstrapper for the SCA4J runtime in a web application host. This listener manages one runtime per servlet context; the lifecycle of that
 * runtime corresponds to the the lifecycle of the associated servlet context.
 * <p/>
 * The bootstrapper launches the runtime, booting system extensions and applications, according to the servlet parameters defined in {@link
 * Constants}. When the runtime is instantiated, it is placed in the servlet context with the attribute {@link Constants#RUNTIME_PARAM}. The runtime
 * implements {@link WebappRuntime} so that filters and servlets loaded in the parent web app classloader may pass events and requests to it.
 * <p/>
 *
 * @version $$Rev: 5276 $$ $$Date: 2008-08-26 05:40:44 +0100 (Tue, 26 Aug 2008) $$
 */

public class WebappRuntimeImpl extends AbstractRuntime<WebappHostInfo> implements WebappRuntime {
    private ServletRequestInjector requestInjector;
    private HttpSessionListener sessionListener;

    public WebappRuntimeImpl() {
        super(WebappHostInfo.class);
    }

    public void activate(QName qName, URI componentId) throws ContributionException, DeploymentException {
        try {
            // contribute the war to the application domain
            Domain domain = getSystemComponent(Domain.class, APPLICATION_DOMAIN_URI);
            ContributionService contributionService = getSystemComponent(ContributionService.class, CONTRIBUTION_SERVICE_URI);
            URI contributionUri = new URI("file", qName.getLocalPart(), null);
            WarContributionSource source = new WarContributionSource(contributionUri);
            contributionService.contribute(source);
            // activate the deployable composite in the domain
            domain.include(qName);
        } catch (MalformedURLException e) {
            throw new DeploymentException("Invalid web archive", e);
        } catch (URISyntaxException e) {
            throw new DeploymentException("Error processing project", e);
        }
    }

    public ServletRequestInjector getRequestInjector() {
        if (requestInjector == null) {
            URI uri = URI.create(ComponentNames.RUNTIME_NAME + "/servletHost");
            requestInjector = getSystemComponent(ServletRequestInjector.class, uri);
        }
        return requestInjector;
    }

    public void requestInitialized(ServletRequestEvent sre) {
        WorkContext workContext = new WorkContext();
        PojoWorkContextTunnel.setThreadWorkContext(workContext);
        ServletRequest req = sre.getServletRequest();
        if (req instanceof HttpServletRequest) {
            WebRequestTunnel.setRequest(((HttpServletRequest) req));
        }
    }

    public void requestDestroyed(ServletRequestEvent sre) {
        PojoWorkContextTunnel.setThreadWorkContext(null);
        WebRequestTunnel.setRequest(null);
    }

    public void sessionCreated(HttpSessionEvent event) {
        getSessionListener().sessionCreated(event);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        getSessionListener().sessionDestroyed(event);
    }

    private HttpSessionListener getSessionListener() {
        if (sessionListener == null) {
            URI uri = URI.create(ComponentNames.RUNTIME_NAME + "/WebApplicationActivator");
            sessionListener = getSystemComponent(HttpSessionListener.class, uri);
        }
        return sessionListener;
    }
}
