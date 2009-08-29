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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

import static org.sca4j.runtime.webapp.Constants.APPLICATION_SCDL_PATH_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.APPLICATION_SCDL_PATH_PARAM;
import static org.sca4j.runtime.webapp.Constants.COMPONENT_PARAM;
import static org.sca4j.runtime.webapp.Constants.COMPOSITE_NAMESPACE_PARAM;
import static org.sca4j.runtime.webapp.Constants.COMPOSITE_PARAM;
import static org.sca4j.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_CONFIG_PARAM;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.namespace.QName;

import org.sca4j.host.SCA4JRuntimeException;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.FileContributionSource;
import org.sca4j.host.contribution.ValidationException;
import org.sca4j.host.domain.AssemblyException;
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.host.runtime.InitializationException;
import org.xml.sax.InputSource;

/**
 * Launches a SCA4J runtime in a web application, loading information from servlet context parameters. This listener manages one runtime per servlet
 * context; the lifecycle of that runtime corresponds to the the lifecycle of the associated servlet context.
 * <p/>
 * The <code>web.xml</code> of a web application embedding SCA4J must have entries for this listener and {@link Fabric3ContextListener}. The latter
 * notifies the runtime of session creation and expiration events through a "bridging" contract, {@link WebappRuntime}.
 *
 * @version $Rev: 5451 $ $Date: 2008-09-20 06:31:05 +0100 (Sat, 20 Sep 2008) $
 */
public class SCA4JContextListener implements ServletContextListener {

    private static final String SYSTEM_CONFIG = "/WEB-INF/systemConfig.xml";

    public void contextInitialized(ServletContextEvent event) {

        ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
        ServletContext servletContext = event.getServletContext();
        WebappUtil utils = getUtils(servletContext);
        WebappRuntime runtime;
        WebAppMonitor monitor = null;
        
        try {
            
            String compositeNamespace = utils.getInitParameter(COMPOSITE_NAMESPACE_PARAM, null);
            String compositeName = utils.getInitParameter(COMPOSITE_PARAM, "WebappComposite");
            URI componentId = new URI(utils.getInitParameter(COMPONENT_PARAM, "webapp"));
            String scdlPath = utils.getInitParameter(APPLICATION_SCDL_PATH_PARAM, APPLICATION_SCDL_PATH_DEFAULT);
            URL scdl = servletContext.getResource(scdlPath);
            if (scdl == null) {
                throw new InitializationException("Web composite not found");
            }
            
            runtime = utils.getRuntime(webappClassLoader);
            monitor = runtime.getMonitorFactory().getMonitor(WebAppMonitor.class);
            BootConfiguration configuration = createBootConfiguration(runtime, webappClassLoader, utils, servletContext);

            runtime.bootPrimordial(configuration);
            runtime.bootSystem();
            runtime.joinDomain(-1);
            runtime.start();
            servletContext.setAttribute(RUNTIME_ATTRIBUTE, runtime);
            monitor.started(runtime.getJMXSubDomain());
            
            // deploy the application composite
            QName qName = new QName(compositeNamespace, compositeName);
            runtime.activate(qName, componentId);
            monitor.compositeDeployed(qName);
            
        } catch (ValidationException e) {
        	monitor.contributionErrors(e.getMessage());
            throw new SCA4JInitException("Errors were detected in the web application contribution", e);
        } catch (AssemblyException e) {
            // print out the deployment errors
            monitor.deploymentErrors(e.getMessage());
            throw new SCA4JInitException("Deployment errors were detected", e);
        } catch (SCA4JRuntimeException e) {
            if (monitor != null) {
                monitor.runError(e);
            }
            throw e;
        } catch (Throwable e) {
            if (monitor != null) {
                monitor.runError(e);
            }
            throw new SCA4JInitException(e);
        }
    }

    /*
     * Creates the boot configuration.
     */
    private BootConfiguration createBootConfiguration(WebappRuntime runtime, ClassLoader webappClassLoader, WebappUtil utils, ServletContext servletContext) throws InitializationException, MalformedURLException {

        BootConfiguration configuration = new BootConfiguration();
        configuration.setAppClassLoader(webappClassLoader);
        configuration.setBootClassLoader(webappClassLoader);
        configuration.setHostClassLoader(webappClassLoader);

        // create the runtime bootrapper
        URL systemScdl = utils.getSystemScdl(webappClassLoader);
        configuration.setSystemScdl(systemScdl);
        
        // add the boot libraries to export as contributions. This is necessary so extension contributions can import them
        List<String> bootExports = new ArrayList<String>();
        bootExports.add("META-INF/maven/org.sca4j/sca4j-spi/pom.xml");
        bootExports.add("META-INF/maven/org.sca4j/sca4j-pojo/pom.xml");
        bootExports.add("META-INF/maven/org.sca4j/sca4j-java/pom.xml");
        bootExports.add("META-INF/maven/org.sca4j/sca4j-container-web-spi/pom.xml");
        configuration.setBootLibraryExports(bootExports);

        // process extensions
        ServletContext context = runtime.getHostInfo().getServletContext();
        List<ContributionSource> extensions = getExtensionContributions("/WEB-INF/lib/f3Extensions.properties", context);
        configuration.setExtensions(extensions);

        // process the baseline intents
        URL intentsLocation = utils.getIntentsLocation(webappClassLoader);
        if (intentsLocation == null) {
            intentsLocation = webappClassLoader.getResource("META-INF/sca4j/intents.xml");
        }
        URI uri = URI.create("StandardIntents");
        ContributionSource source = new FileContributionSource(uri, intentsLocation, -1, new byte[0]);
        configuration.setIntents(source);
        configuration.setRuntime(runtime);
        
        String systemConfig = utils.getInitParameter(SYSTEM_CONFIG_PARAM, null);
        if (systemConfig != null) {
            Reader reader = new StringReader(systemConfig);
            InputSource inputSource = new InputSource(reader);
            configuration.setSystemConfigDocument(inputSource);
        } else {
            configuration.setSystemConfig(servletContext.getResource(SYSTEM_CONFIG));
        }

        return configuration;

    }

    /*
     * Gets the extension contributions.
     */
    private List<ContributionSource> getExtensionContributions(String extensionDefinitions, ServletContext context) throws InitializationException {

        InputStream stream = context.getResourceAsStream(extensionDefinitions);
        if (stream == null) {
            return Collections.emptyList();
        }

        Properties props = new Properties();
        try {
            props.load(stream);
        } catch (IOException e) {
            throw new InitializationException(e);
        }

        List<URL> files = new ArrayList<URL>();
        for (Object key : props.keySet()) {
            try {
                URL url = context.getResource("/WEB-INF/lib/" + key).toURI().toURL();
                files.add(url);
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            } catch (URISyntaxException e) {
                throw new AssertionError(e);
            }
        }

        if (!files.isEmpty()) {
            // contribute and activate extensions if they exist in the runtime domain
            List<ContributionSource> sources = new ArrayList<ContributionSource>();
            for (URL location : files) {
                URI uri = URI.create(location.getPath());
                ContributionSource source = new FileContributionSource(uri, location, -1, new byte[0]);
                sources.add(source);

            }
            return sources;
        }

        return Collections.emptyList();

    }

    /**
     * Can be overridden for tighter host integration.
     *
     * @param servletContext Servlet context for the runtime.
     * @return Webapp util to be used.
     */
    protected WebappUtil getUtils(ServletContext servletContext) {
        return new WebappUtilImpl(servletContext);
    }

    /**
     * Invoked when the servlet context is destroyed. This is used to shutdown the runtime.
     */
    public void contextDestroyed(ServletContextEvent event) {

        ServletContext servletContext = event.getServletContext();
        WebappRuntime runtime = (WebappRuntime) servletContext.getAttribute(RUNTIME_ATTRIBUTE);

        if (runtime != null) {
            servletContext.removeAttribute(RUNTIME_ATTRIBUTE);
            runtime.shutdown();
            runtime.getMonitorFactory().getMonitor(WebAppMonitor.class).stopped();
        }

    }


}
