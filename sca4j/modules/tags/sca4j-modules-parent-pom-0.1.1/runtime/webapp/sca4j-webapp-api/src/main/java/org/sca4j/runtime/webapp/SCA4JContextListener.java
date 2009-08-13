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
package org.sca4j.runtime.webapp;

import static org.sca4j.runtime.webapp.Constants.APPLICATION_SCDL_PATH_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.APPLICATION_SCDL_PATH_PARAM;
import static org.sca4j.runtime.webapp.Constants.BASE_DIR;
import static org.sca4j.runtime.webapp.Constants.COMPONENT_PARAM;
import static org.sca4j.runtime.webapp.Constants.COMPOSITE_NAMESPACE_PARAM;
import static org.sca4j.runtime.webapp.Constants.COMPOSITE_PARAM;
import static org.sca4j.runtime.webapp.Constants.DEFAULT_MANAGEMENT_DOMAIN;
import static org.sca4j.runtime.webapp.Constants.DOMAIN_PARAM;
import static org.sca4j.runtime.webapp.Constants.MANAGEMENT_DOMAIN_PARAM;
import static org.sca4j.runtime.webapp.Constants.RUNTIME_ATTRIBUTE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
import org.sca4j.host.runtime.Bootstrapper;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.runtime.RuntimeLifecycleCoordinator;
import org.sca4j.host.runtime.ScdlBootstrapper;
import org.sca4j.host.runtime.ShutdownException;

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

    private RuntimeLifecycleCoordinator<WebappRuntime, Bootstrapper> coordinator;

    public void contextInitialized(ServletContextEvent event) {

        ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
        ServletContext servletContext = event.getServletContext();
        WebappUtil utils = getUtils(servletContext);
        WebappRuntime runtime;
        WebAppMonitor monitor = null;
        try {
            String defaultComposite = "WebappComposite";
            String compositeNamespace = utils.getInitParameter(COMPOSITE_NAMESPACE_PARAM, null);
            String compositeName = utils.getInitParameter(COMPOSITE_PARAM, defaultComposite);
            URI componentId = new URI(utils.getInitParameter(COMPONENT_PARAM, "webapp"));
            String scdlPath = utils.getInitParameter(APPLICATION_SCDL_PATH_PARAM, APPLICATION_SCDL_PATH_DEFAULT);
            URL scdl = servletContext.getResource(scdlPath);
            if (scdl == null) {
                throw new InitializationException("Web composite not found");
            }
            runtime = createRuntime(webappClassLoader, servletContext, utils);
            monitor = runtime.getMonitorFactory().getMonitor(WebAppMonitor.class);
            coordinator = utils.getCoordinator(webappClassLoader);
            BootConfiguration<WebappRuntime, Bootstrapper> configuration = createBootConfiguration(runtime, webappClassLoader, utils);
            coordinator.setConfiguration(configuration);

            // boot the runtime
            coordinator.bootPrimordial();
            coordinator.initialize();
            Future<Void> joinFuture = coordinator.joinDomain(-1);
            joinFuture.get();
            Future<Void> recoverFuture = coordinator.recover();
            recoverFuture.get();
            Future<Void> startFuture = coordinator.start();
            startFuture.get();
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

    private WebappRuntime createRuntime(ClassLoader webappClassLoader, ServletContext context, WebappUtil utils) {

        try {

            String baseDirParam = utils.getInitParameter(BASE_DIR, null);
            File baseDir;
            if (baseDirParam == null) {
                baseDir = new File(URLDecoder.decode(context.getResource("/WEB-INF/lib/").getFile(), "UTF-8"));
            } else {
                baseDir = new File(baseDirParam);
            }

            URI domain = new URI(utils.getInitParameter(DOMAIN_PARAM, "sca4j://domain"));
            File tempDir = (File) context.getAttribute("javax.servlet.context.tempdir");
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"), ".sca4j");
            }
            WebappHostInfo info = new WebappHostInfoImpl(context, domain, baseDir, tempDir);

            WebappRuntime runtime = utils.getRuntime(webappClassLoader);
            runtime.setHostInfo(info);
            runtime.setHostClassLoader(webappClassLoader);
            String managementDomain = utils.getInitParameter(MANAGEMENT_DOMAIN_PARAM, DEFAULT_MANAGEMENT_DOMAIN);
            runtime.setJmxSubDomain(managementDomain);

            return runtime;

        } catch (URISyntaxException e) {
            throw new SCA4JInitException(e);
        } catch (UnsupportedEncodingException e) {
            throw new SCA4JInitException(e);
        } catch (MalformedURLException e) {
            throw new SCA4JInitException(e);
        }

    }

    /*
     * Creates the boot configuration.
     */
    private BootConfiguration<WebappRuntime, Bootstrapper> createBootConfiguration(WebappRuntime runtime,
                                                                                   ClassLoader webappClassLoader,
                                                                                   WebappUtil utils) throws InitializationException {

        BootConfiguration<WebappRuntime, Bootstrapper> configuration = new BootConfiguration<WebappRuntime, Bootstrapper>();
        configuration.setAppClassLoader(webappClassLoader);
        configuration.setBootClassLoader(webappClassLoader);

        // create the runtime bootrapper
        URL systemScdl = utils.getSystemScdl(webappClassLoader);
        ScdlBootstrapper bootstrapper = utils.getBootstrapper(webappClassLoader);
        bootstrapper.setScdlLocation(systemScdl);
        configuration.setBootstrapper(bootstrapper);

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
            runtime.getMonitorFactory().getMonitor(WebAppMonitor.class).stopped();
        }

        try {
            if (coordinator == null) {
                return;
            }
            Future<Void> future = coordinator.shutdown();
            future.get();
        } catch (ShutdownException e) {
            servletContext.log("Error shutting runtume down", e);
        } catch (ExecutionException e) {
            servletContext.log("Error shutting runtume down", e);
        } catch (InterruptedException e) {
            servletContext.log("Error shutting runtume down", e);
        }

    }


}
