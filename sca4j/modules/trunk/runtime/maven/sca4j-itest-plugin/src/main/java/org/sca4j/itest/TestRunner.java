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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServerFactory;
import javax.xml.namespace.QName;

import org.apache.maven.surefire.report.BriefFileReporter;
import org.apache.maven.surefire.report.DetailedConsoleReporter;
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
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.maven.runtime.ContextStartException;
import org.sca4j.maven.runtime.MavenEmbeddedRuntime;
import org.sca4j.maven.runtime.TestWire;
import org.sca4j.maven.runtime.WireHolder;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.scdl.Composite;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

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
    
    public static void main(String[] args) throws IOException, TestFailureException {
        
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(args[0]);
            XStream xstream = new XStream(new DomDriver());
            TestMetadata testMetadata = (TestMetadata) xstream.fromXML(fileInputStream);
            new TestRunner(testMetadata, null).executeTests();
        } finally {
            fileInputStream.close();
        }
        System.exit(0);
        
    }
    
    public TestRunner(TestMetadata testMetadata, MavenMonitorFactory monitorFactory) {
        this.testMetadata = testMetadata;
        this.monitorFactory = monitorFactory;
    }

    public void executeTests() throws TestFailureException {
        
        SurefireTestSuite testSuite;
        MavenEmbeddedRuntime runtime = null;
        
        try {
            
            ClassLoader parentClassLoader = getClass().getClassLoader();
            
            ClassLoader hostClassLoader = createClassLoader(parentClassLoader, testMetadata.classpath);
            ClassLoader bootClassLoader = hostClassLoader;
            
            Thread.currentThread().setContextClassLoader(bootClassLoader);
            
            runtime = createRuntime(bootClassLoader, testMetadata.classpath);
            BootConfiguration configuration = createBootConfiguration(runtime, bootClassLoader, hostClassLoader, hostClassLoader);            
            
            runtime.bootPrimordial(configuration);
            runtime.bootSystem();
            runtime.joinDomain(-1);
            runtime.start();
            if (testMetadata.compositeName == null) {
                testSuite = createTestSuite(runtime, testMetadata.testScdl.toURI().toURL());
            } else {
                testSuite = createTestSuite(runtime);
            }
            
            // Just verify the composites and don't run the tests
            if (System.getProperty("sca4j.verify") != null) {
                return;
            }
            
            boolean success = runSurefire(testSuite);
            
            if (!success) {
                String msg = "There were test failures";
                throw new TestFailureException(msg);
            }
            
            
        } catch (TestFailureException e) {
            throw e;
        } catch (Exception e) {
            // trap any other exception
            throw new AssertionError(e);
        } finally {
            if (runtime != null) {
                runtime.shutdown();
            }
        }
        
    }

    private BootConfiguration createBootConfiguration(MavenEmbeddedRuntime runtime, ClassLoader bootClassLoader, ClassLoader appClassLoader, ClassLoader hostClassLoader) throws IOException {

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
        
        if (testMetadata.systemScdl == null) {
            testMetadata.systemScdl = bootClassLoader.getResource("META-INF/sca4j/embeddedMaven.composite");
        }
        configuration.setSystemScdl(testMetadata.systemScdl);
        if (testMetadata.systemConfig != null) {
            configuration.setSystemConfig(new ByteArrayInputStream(testMetadata.systemConfig.trim().getBytes()));
        } else {
            URL systemConfig = getSystemConfig();
            if (systemConfig != null) {
                configuration.setSystemConfig(getSystemConfig().openStream());
            }
        }

        // process the baseline intents
        if (testMetadata.intentsLocation == null) {
            testMetadata.intentsLocation = bootClassLoader.getResource("META-INF/sca4j/intents.xml");
        }
        URI uri = URI.create("StandardIntents");
        ContributionSource source = new FileContributionSource(uri, testMetadata.intentsLocation, -1, new byte[0]);
        configuration.setIntents(source);
        configuration.setRuntime(runtime);
        return configuration;
    }

    private URL getSystemConfig() {
        File systemConfig = new File(testMetadata.outputDirectory, DEFAULT_SYSTEM_CONFIG_DIR + SYSTEM_CONFIG_XML_FILE);
        if (testMetadata.systemConfigDir != null) {
            systemConfig = new File(testMetadata.outputDirectory, testMetadata.systemConfigDir + File.separator + SYSTEM_CONFIG_XML_FILE);
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
        
        MavenEmbeddedRuntime runtime = instantiate(MavenEmbeddedRuntime.class, testMetadata.runtimeImpl, bootClassLoader);
        runtime.setMonitorFactory(monitorFactory);

        Properties hostProperties = testMetadata.properties != null ? testMetadata.properties: System.getProperties();
        MavenHostInfoImpl hostInfo = new MavenHostInfoImpl(URI.create(testMetadata.testDomain), hostProperties, moduleDependencies);
        runtime.setHostInfo(hostInfo);

        runtime.setJmxSubDomain(testMetadata.managementDomain);

        // TODO Add better host JMX support from the next release
        runtime.setMBeanServer(MBeanServerFactory.createMBeanServer());

        return runtime;
    }

    private ClassLoader createClassLoader(ClassLoader parent, Set<URL> hostArtifacts) {
        List<URL> urls = new ArrayList<URL>(hostArtifacts.size());
        for (URL artifact : hostArtifacts) {
            urls.add(artifact);

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
        reports.add(new XMLReporter(testMetadata.reportsDirectory, testMetadata.trimStackTrace));
        reports.add(new BriefFileReporter(testMetadata.reportsDirectory, testMetadata.trimStackTrace));
        reports.add(new DetailedConsoleReporter(testMetadata.trimStackTrace));
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
        URI domain = URI.create(testMetadata.testDomain);
        Composite composite;
        try {
            composite = runtime.activate(getBuildDirectoryUrl(), testScdlURL);
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
        runtime.startContext(domain);
        return createTestSuite(runtime, composite, domain);
    }

    private SurefireTestSuite createTestSuite(MavenEmbeddedRuntime runtime)
            throws ContributionException, DeploymentException, ContextStartException {
        URI domain = URI.create(testMetadata.testDomain);
        QName qName = new QName(testMetadata.compositeNamespace, testMetadata.compositeName);
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
        for (TestWire testWire : wireHolder.getWires()) {
            System.err.println("******************** Added test " + testWire.getName());
            SCATestSet testSet = new SCATestSet(testWire.getName(), testWire.getWire());
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
            return testMetadata.buildDirectory.toURI().toURL();
        } catch (MalformedURLException e) {
            // this should not happen
            throw new AssertionError();
        }
    }

}
