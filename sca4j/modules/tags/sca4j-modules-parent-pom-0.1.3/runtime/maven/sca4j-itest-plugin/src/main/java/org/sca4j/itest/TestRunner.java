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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;

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
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.runtime.RuntimeLifecycleCoordinator;
import org.sca4j.host.runtime.ScdlBootstrapper;
import org.sca4j.host.runtime.ShutdownException;
import org.sca4j.host.runtime.StartException;
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
    
    public static void main(String[] args) throws IOException {
        
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(args[0]);
            XStream xstream = new XStream();
            TestMetadata testMetadata = (TestMetadata) xstream.fromXML(fileInputStream);
            new TestRunner(testMetadata).executeTests();
        } finally {
            fileInputStream.close();
        }
        
    }
    
    public TestRunner(TestMetadata testMetadata) {
        this.testMetadata = testMetadata;
    }

    @SuppressWarnings("unchecked")
    public void executeTests() {
        
        SurefireTestSuite testSuite;
        RuntimeLifecycleCoordinator<MavenEmbeddedRuntime, ScdlBootstrapper> coordinator = null;
        
        try {
            
            ClassLoader parentClassLoader = getClass().getClassLoader();
            ClassLoader hostClassLoader = createHostClassLoader(parentClassLoader, testMetadata.getHostArtifacts());
            ClassLoader bootClassLoader = createBootClassLoader(hostClassLoader, testMetadata.getRuntimeArtifacts());
            
            Thread.currentThread().setContextClassLoader(bootClassLoader);
            
            MavenEmbeddedRuntime runtime = createRuntime(bootClassLoader, hostClassLoader, testMetadata.getModuleDependencies());
            BootConfiguration<MavenEmbeddedRuntime, ScdlBootstrapper> configuration = createBootConfiguration(runtime, bootClassLoader, hostClassLoader);
            coordinator = instantiate(RuntimeLifecycleCoordinator.class, testMetadata.getCoordinatorImpl(), bootClassLoader);
            coordinator.setConfiguration(configuration);
            
            bootRuntime(coordinator);
            if (testMetadata.getCompositeName() == null) {
                testSuite = createTestSuite(runtime, testMetadata.getTestScdl().toURI().toURL());
            } else {
                testSuite = createTestSuite(runtime);
            }
            boolean success = runSurefire(testSuite);
            
            if (!success) {
                String msg = "There were test failures";
                throw new RuntimeException(msg);
            }
            
        } catch (Exception e) {
            // trap any other exception
            throw new AssertionError("Error deploying test composite: " + testMetadata.getTestScdl());
        } finally {
            try {
                if (coordinator != null) {
                    shutdownRuntime(coordinator);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        
    }

    private void shutdownRuntime(RuntimeLifecycleCoordinator<MavenEmbeddedRuntime, ScdlBootstrapper> coordinator) throws ShutdownException, InterruptedException, ExecutionException {
        Future<Void> future = coordinator.shutdown();
        future.get();
    }

    private void bootRuntime(RuntimeLifecycleCoordinator<MavenEmbeddedRuntime, ScdlBootstrapper> coordinator) {
        try {
            coordinator.bootPrimordial();
            coordinator.initialize();
            Future<Void> future = coordinator.joinDomain(-1);
            future.get();
            future = coordinator.recover();
            future.get();
            future = coordinator.start();
            future.get();
        } catch (StartException e) {
            throw new RuntimeException("Error booting SCA4J runtime", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error booting SCA4J runtime", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error booting SCA4J runtime", e);
        } catch (InitializationException e) {
            throw new RuntimeException("Error booting SCA4J runtime", e);
        }
    }

    private BootConfiguration<MavenEmbeddedRuntime, ScdlBootstrapper> createBootConfiguration(MavenEmbeddedRuntime runtime, ClassLoader bootClassLoader, ClassLoader appClassLoader) {

        BootConfiguration<MavenEmbeddedRuntime, ScdlBootstrapper> configuration = new BootConfiguration<MavenEmbeddedRuntime, ScdlBootstrapper>();
        configuration.setAppClassLoader(appClassLoader);
        configuration.setBootClassLoader(bootClassLoader);

        // create the runtime bootrapper
        ScdlBootstrapper bootstrapper = createBootstrapper(bootClassLoader);
        configuration.setBootstrapper(bootstrapper);

        // add the boot libraries to export as contributions. This is necessary so extension contributions can import them
        List<String> bootExports = new ArrayList<String>();
        bootExports.add("META-INF/maven/org.sca4j/sca4j-spi/pom.xml");
        bootExports.add("META-INF/maven/org.sca4j/sca4j-pojo/pom.xml");
        bootExports.add("META-INF/maven/org.sca4j/sca4j-java/pom.xml");
        configuration.setBootLibraryExports(bootExports);

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

    private ScdlBootstrapper createBootstrapper(ClassLoader bootClassLoader) {
        ScdlBootstrapper bootstrapper = instantiate(ScdlBootstrapper.class, testMetadata.getBootstrapperImpl(), bootClassLoader);
        if (testMetadata.getSystemScdl() == null) {
            testMetadata.setSystemScdl(bootClassLoader.getResource("META-INF/sca4j/embeddedMaven.composite"));
        }
        bootstrapper.setScdlLocation(testMetadata.getSystemScdl());
        if (testMetadata.getSystemConfig() != null) {
            Reader reader = new StringReader(testMetadata.getSystemConfig());
            InputSource source = new InputSource(reader);
            bootstrapper.setSystemConfig(source);
        } else {
            URL systemConfig = getSystemConfig();
            bootstrapper.setSystemConfig(systemConfig);
        }
        return bootstrapper;
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
    
    private MavenEmbeddedRuntime createRuntime(ClassLoader bootClassLoader, ClassLoader hostClassLoader, Set<URL> moduleDependencies) {
        MonitorFactory monitorFactory = new JavaLoggingMonitorFactory();
        MavenEmbeddedRuntime runtime = instantiate(MavenEmbeddedRuntime.class, testMetadata.getRuntimeImpl(), bootClassLoader);
        runtime.setMonitorFactory(monitorFactory);
        runtime.setHostClassLoader(hostClassLoader);

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
