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
package org.sca4j.itest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.surefire.report.BriefConsoleReporter;
import org.apache.maven.surefire.report.BriefFileReporter;
import org.apache.maven.surefire.report.Reporter;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.report.XMLReporter;
import org.apache.maven.surefire.suite.SurefireTestSuite;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.FileContributionSource;
import org.sca4j.host.contribution.ValidationException;
import org.sca4j.host.domain.AssemblyException;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.perf.PerformanceMonitor;
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.jmx.agent.Agent;
import org.sca4j.jmx.agent.DefaultAgent;
import org.sca4j.junit.runtime.WireHolder;
import org.sca4j.maven.runtime.ContextStartException;
import org.sca4j.maven.runtime.MavenEmbeddedRuntime;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.monitor.impl.JavaLoggingMonitorFactory;
import org.sca4j.scdl.Composite;
import org.sca4j.spi.classloader.MultiParentClassLoader;
import org.sca4j.spi.wire.Wire;
import org.xml.sax.InputSource;

import com.thoughtworks.xstream.XStream;

/**
 * Executes integration tests.
 *
 * @version $Revision$ $Date$
 */
public class TestRunner {
    
    private static final String SYSTEM_CONFIG_XML_FILE = "systemConfig.xml";
    private static final String DEFAULT_SYSTEM_CONFIG_DIR = "test-classes" + File.separator + "META-INF" + File.separator;
    
    private TestMetadata testMetadata;
    private MonitorFactory monitorFactory;
    
    public static void main(String[] args) throws IOException, MojoFailureException {
        
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(args[0]);
            XStream xstream = new XStream();
            TestMetadata testMetadata = (TestMetadata) xstream.fromXML(fileInputStream);
            new TestRunner(testMetadata, null).executeTests();
        } finally {
            fileInputStream.close();
        }
        
    }
    
    public TestRunner(TestMetadata testMetadata, MavenMonitorFactory monitorFactory) {
        this.testMetadata = testMetadata;
        this.monitorFactory = monitorFactory;
    }

    public void executeTests() throws MojoFailureException {
        
        SurefireTestSuite testSuite;
        MavenEmbeddedRuntime runtime = null;
        
        try {
            
            ClassLoader parentClassLoader = getClass().getClassLoader();
            ClassLoader hostClassLoader = createHostClassLoader(parentClassLoader, testMetadata.getHostArtifacts());
            ClassLoader bootClassLoader = createBootClassLoader(hostClassLoader, testMetadata.getRuntimeArtifacts());
            
            Thread.currentThread().setContextClassLoader(bootClassLoader);
            
            runtime = createRuntime(bootClassLoader, testMetadata.getModuleDependencies());
            BootConfiguration configuration = createBootConfiguration(runtime, bootClassLoader, hostClassLoader, hostClassLoader);            
            
            PerformanceMonitor.start("Boot primodial");
            runtime.bootPrimordial(configuration);
            PerformanceMonitor.end();
            PerformanceMonitor.start("Boot system");
            runtime.bootSystem();
            PerformanceMonitor.end();
            runtime.joinDomain(-1);
            PerformanceMonitor.start("Runtime started");
            runtime.start();
            PerformanceMonitor.end();
            PerformanceMonitor.start("Test suite created");
            if (testMetadata.getCompositeName() == null) {
                testSuite = createTestSuite(runtime, testMetadata.getTestScdl().toURI().toURL());
            } else {
                testSuite = createTestSuite(runtime);
            }
            PerformanceMonitor.end();
            PerformanceMonitor.start("Test executed");
            boolean success = runSurefire(testSuite);
            PerformanceMonitor.end();
            
            if (!success) {
                String msg = "There were test failures";
                throw new MojoFailureException(msg);
            }
            
        } catch (MojoFailureException e) {
            throw e;
        } catch (Exception e) {
            // trap any other exception
            throw new AssertionError(e);
        } finally {
            runtime.shutdown();
        }
        
    }

    private BootConfiguration createBootConfiguration(MavenEmbeddedRuntime runtime, ClassLoader bootClassLoader, ClassLoader appClassLoader, ClassLoader hostClassLoader) {

        BootConfiguration configuration = new BootConfiguration();
        configuration.setAppClassLoader(appClassLoader);
        configuration.setBootClassLoader(bootClassLoader);
        configuration.setHostClassLoader(hostClassLoader);
        
        // create the runtime bootrapper

        // add the boot libraries to export as contributions. This is necessary so extension contributions can import them
        List<String> bootExports = new ArrayList<String>();
        bootExports.add("META-INF/maven/org.sca4j/sca4j-spi/pom.xml");
        bootExports.add("META-INF/maven/org.sca4j/sca4j-pojo/pom.xml");
        bootExports.add("META-INF/maven/org.sca4j/sca4j-java/pom.xml");
        configuration.setBootLibraryExports(bootExports);
        
        if (testMetadata.getSystemScdl() == null) {
            testMetadata.setSystemScdl(bootClassLoader.getResource("META-INF/sca4j/embeddedMaven.composite"));
        }
        configuration.setSystemScdl(testMetadata.getSystemScdl());
        if (testMetadata.getSystemConfig() != null) {
            Reader reader = new StringReader(testMetadata.getSystemConfig());
            InputSource source = new InputSource(reader);
            configuration.setSystemConfigDocument(source);
        } else {
            URL systemConfig = getSystemConfig();
            configuration.setSystemConfig(systemConfig);
        }

        // process extensions
        configuration.setExtensions(testMetadata.getExtensions());

        // process the baseline intents
        if (testMetadata.getIntentsLocation() == null) {
            testMetadata.setIntentsLocation(bootClassLoader.getResource("META-INF/sca4j/intents.xml"));
        }
        URI uri = URI.create("StandardIntents");
        ContributionSource source = new FileContributionSource(uri, testMetadata.getIntentsLocation(), -1, new byte[0]);
        configuration.setIntents(source);
        configuration.setRuntime(runtime);
        return configuration;
    }

    private URL getSystemConfig() {
        File systemConfig = new File(testMetadata.getOutputDirectory(), DEFAULT_SYSTEM_CONFIG_DIR + SYSTEM_CONFIG_XML_FILE);
        if (testMetadata.getSystemConfigDir() != null) {
            systemConfig = new File(testMetadata.getOutputDirectory(), testMetadata.getSystemConfigDir() + File.separator + SYSTEM_CONFIG_XML_FILE);
            if (!systemConfig.exists()) {
                throw new AssertionError("Failed to find the system config information in: " + systemConfig.getAbsolutePath());
            }
        }

        try {
            return systemConfig.exists() ? systemConfig.toURL() : null;
        } catch (MalformedURLException e) {
            throw new AssertionError("Invalid system configuration: " + systemConfig);
        }
    }

    private <T> T instantiate(Class<T> type, String impl, ClassLoader cl) {
        try {
            Class<?> implClass = cl.loadClass(impl);
            return type.cast(implClass.newInstance());
        } catch (ClassNotFoundException e) {
            // programming errror
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            // programming errror
            throw new AssertionError(e);
        } catch (InstantiationException e) {
            // programming errror
            throw new AssertionError(e);
        }
    }
    
    private MavenEmbeddedRuntime createRuntime(ClassLoader bootClassLoader, Set<URL> moduleDependencies) {
    	if (monitorFactory == null) {
    		monitorFactory = new JavaLoggingMonitorFactory();
    	}
        MavenEmbeddedRuntime runtime = instantiate(MavenEmbeddedRuntime.class, testMetadata.getRuntimeImpl(), bootClassLoader);
        runtime.setMonitorFactory(monitorFactory);

        Properties hostProperties = testMetadata.getProperties() != null ? testMetadata.getProperties() : System.getProperties();
        MavenHostInfoImpl hostInfo = new MavenHostInfoImpl(URI.create(testMetadata.getTestDomain()), hostProperties, moduleDependencies);
        runtime.setHostInfo(hostInfo);

        runtime.setJmxSubDomain(testMetadata.getManagementDomain());

        // TODO Add better host JMX support from the next release
        Agent agent = new DefaultAgent();
        runtime.setMBeanServer(agent.getMBeanServer());

        return runtime;
    }

    private ClassLoader createBootClassLoader(ClassLoader parent, Set<File> artifacts) {
        URL[] urls = new URL[artifacts.size()];
        int i = 0;
        for (File artifact : artifacts) {
            assert artifact != null;
            try {
                urls[i++] = artifact.toURI().toURL();
            } catch (MalformedURLException e) {
                // toURI should have made this valid
                throw new AssertionError(e);
            }
        }
        return new MultiParentClassLoader(URI.create("sca4j://runtime/BootClassLoader"), urls, parent);
    }

    private ClassLoader createHostClassLoader(ClassLoader parent, Set<File> hostArtifacts) {
        List<URL> urls = new ArrayList<URL>(hostArtifacts.size());
        for (File artifact : hostArtifacts) {
            try {
                URL url = artifact.toURI().toURL();
                urls.add(url);
            } catch (MalformedURLException e) {
                // toURI should have encoded the URL
                throw new AssertionError(e);
            }

        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
    }

    private boolean runSurefire(SurefireTestSuite testSuite) {

        try {
            Properties status = new Properties();
            boolean success = run(testSuite, status);
            return success;
        } catch (ReporterException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (TestSetFailedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    private boolean run(SurefireTestSuite suite, Properties status) throws ReporterException, TestSetFailedException {
        int totalTests = suite.getNumTests();

        List<Reporter> reports = new ArrayList<Reporter>();
        reports.add(new XMLReporter(testMetadata.getReportsDirectory(), testMetadata.isTrimStackTrace()));
        reports.add(new BriefFileReporter(testMetadata.getReportsDirectory(), testMetadata.isTrimStackTrace()));
        reports.add(new BriefConsoleReporter(testMetadata.isTrimStackTrace()));
        ReporterManager reporterManager = new ReporterManager(reports);
        reporterManager.initResultsFromProperties(status);

        reporterManager.runStarting(totalTests);

        if (totalTests == 0) {
            reporterManager.writeMessage("There are no tests to run.");
        } else {
            suite.execute(reporterManager, null);
        }

        reporterManager.runCompleted();
        reporterManager.updateResultsProperties(status);
        return reporterManager.getNumErrors() == 0 && reporterManager.getNumFailures() == 0;
    }

    private SurefireTestSuite createTestSuite(MavenEmbeddedRuntime runtime, URL testScdlURL)
            throws DeploymentException, ContributionException, ContextStartException {
        URI domain = URI.create(testMetadata.getTestDomain());
        Composite composite;
        try {
            PerformanceMonitor.start("Composite activated");
            composite = runtime.activate(getBuildDirectoryUrl(), testScdlURL);
            PerformanceMonitor.end();
        } catch (ValidationException e) {
            // print out the validaiton errors
            reportContributionErrors(e);
            String msg = "Contribution errors were found";
            throw new RuntimeException(msg);
        } catch (AssemblyException e) {
            reportDeploymentErrors(e);
            String msg = "Deployment errors were found";
            throw new RuntimeException(msg);
        }
        PerformanceMonitor.start("Context started");
        runtime.startContext(domain);
        PerformanceMonitor.end();
        return createTestSuite(runtime, composite, domain);
    }

    private SurefireTestSuite createTestSuite(MavenEmbeddedRuntime runtime)
            throws ContributionException, DeploymentException, ContextStartException {
        URI domain = URI.create(testMetadata.getTestDomain());
        QName qName = new QName(testMetadata.getCompositeNamespace(), testMetadata.getCompositeName());
        try {
            Composite composite;
            composite = runtime.activate(getBuildDirectoryUrl(), qName);
            runtime.startContext(domain);
            return createTestSuite(runtime, composite, domain);
        } catch (ValidationException e) {
            // print out the validation errors
            reportContributionErrors(e);
            String msg = "Contribution errors were found";
            throw new RuntimeException(msg);
        } catch (AssemblyException e) {
            reportDeploymentErrors(e);
            String msg = "Deployment errors were found";
            throw new RuntimeException(msg);
        }
    }

    private SurefireTestSuite createTestSuite(MavenEmbeddedRuntime runtime, Composite composite, URI uriBase) {
        
        WireHolder wireHolder = runtime.getSystemComponent(WireHolder.class, WireHolder.COMPONENT_URI);
        SCATestSuite suite = new SCATestSuite();
        for (Map.Entry<String, Wire> entry : wireHolder.entrySet()) {
            SCATestSet testSet = new SCATestSet(entry.getKey(), entry.getValue());
            suite.add(testSet);
        }
        return suite;
    }


    private void reportContributionErrors(ValidationException cause) {
        StringBuilder b = new StringBuilder("\n\n");
        b.append("-------------------------------------------------------\n");
        b.append("CONTRIBUTION ERRORS\n");
        b.append("-------------------------------------------------------\n\n");
        b.append(cause.getMessage());
        System.err.println(b);
    }

    private void reportDeploymentErrors(AssemblyException cause) {
        StringBuilder b = new StringBuilder("\n\n");
        b.append("-------------------------------------------------------\n");
        b.append("DEPLOYMENT ERRORS\n");
        b.append("-------------------------------------------------------\n\n");
        b.append(cause.getMessage());
        System.err.println(b);
    }

    private URL getBuildDirectoryUrl() {
        try {
            return testMetadata.getBuildDirectory().toURI().toURL();
        } catch (MalformedURLException e) {
            // this should not happen
            throw new AssertionError();
        }
    }

}
