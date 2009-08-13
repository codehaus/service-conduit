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
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.sca4j.api.annotation.logging.Severe;
import org.sca4j.featureset.FeatureSet;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.FileContributionSource;
import org.sca4j.host.runtime.BootConfiguration;
import org.sca4j.host.runtime.InitializationException;
import org.sca4j.host.runtime.RuntimeLifecycleCoordinator;
import org.sca4j.host.runtime.ScdlBootstrapper;
import org.sca4j.host.runtime.ShutdownException;
import org.sca4j.host.runtime.StartException;
import org.sca4j.jmx.agent.Agent;
import org.sca4j.jmx.agent.DefaultAgent;
import org.sca4j.maven.runtime.MavenEmbeddedRuntime;
import org.sca4j.monitor.MonitorFactory;
import org.sca4j.spi.classloader.MultiParentClassLoader;

/**
 * Run integration tests on a SCA composite using an embedded SCA4J runtime.
 *
 * @version $Rev: 5478 $ $Date: 2008-09-26 00:54:18 +0100 (Fri, 26 Sep 2008) $
 * @goal test
 * @phase integration-test
 */
public class SCA4JITestMojo extends AbstractMojo {
    private static final String SYSTEM_CONFIG_XML_FILE = "systemConfig.xml";
    private static final String DEFAULT_SYSTEM_CONFIG_DIR = "test-classes" + File.separator + "META-INF" + File.separator;

    /**
     * POM
     *
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;

    /**
     * Optional parameter for management domain.
     *
     * @parameter
     */
    public String managementDomain = "itest-host";

    /**
     * Optional parameter for thread pool size.
     *
     * @parameter
     */
    public int numWorkers = 10;

    /**
     * The optional target namespace of the composite to activate.
     *
     * @parameter
     */
    public String compositeNamespace;

    /**
     * The local name of the composite to activate, which may be null if testScdl is defined.
     *
     * @parameter
     */
    public String compositeName;

    /**
     * The location if the SCDL that defines the test harness composite. The source for this would normally be placed in the test/resources directory
     * and be copied by the resource plugin; this allows property substitution if required.
     *
     * @parameter expression="${project.build.testOutputDirectory}/itest.composite"
     */
    public File testScdl;

    /**
     * test composite .
     *
     * @parameter expression="${project.build.directory}"
     */
    public File buildDirectory;

    /**
     * Do not run if this is set to true. This usage is consistent with the surefire plugin.
     *
     * @parameter expression="${maven.test.skip}"
     */
    public boolean skip;

    /**
     * The directory where reports will be written.
     *
     * @parameter expression="${project.build.directory}/surefire-reports"
     */
    public File reportsDirectory;

    /**
     * Whether to trim the stack trace in the reports to just the lines within the test, or show the full trace.
     *
     * @parameter expression="${trimStackTrace}" default-value="true"
     */
    public boolean trimStackTrace;

    /**
     * The directory containing generated test classes of the project being tested.
     *
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    public File testClassesDirectory;

    /**
     * The SCA domain in which to deploy the test components.
     *
     * @parameter expression="sca4j://domain"
     * @required
     */
    public String testDomain;

    /**
     * The location of the SCDL that configures the SCA4J runtime. This allows the default runtime configuration supplied in this plugin to be
     * overridden.
     *
     * @parameter
     */
    public URL systemScdl;

    /**
     * Class name for the implementation of the runtime to use.
     *
     * @parameter expression="org.sca4j.maven.runtime.MavenEmbeddedRuntimeImpl"
     */
    public String runtimeImpl;

    /**
     * Class name for the implementation of the bootstrapper to use.
     *
     * @parameter expression="org.sca4j.fabric.runtime.bootstrap.ScdlBootstrapperImpl"
     */
    public String bootstrapperImpl;

    /**
     * Class name for the implementation of the coordinator to use.
     *
     * @parameter expression="org.sca4j.fabric.runtime.DefaultCoordinator"
     */
    public String coordinatorImpl;

    /**
     * The location of the default intents file for the SCA4J runtime.
     *
     * @parameter
     */
    public URL intentsLocation;

    /**
     * The version of the runtime to use.
     *
     * @parameter expression="0.6.5"
     */
    public String runtimeVersion;

    /**
     * Set of runtime extension artifacts that should be deployed to the runtime.
     *
     * @parameter
     */
    public Dependency[] extensions;

    /**
     * Set of runtime extension artifacts that should be deployed to the runtime expressed as feature sets.
     *
     * @parameter
     */
    public Dependency[] features;

    /**
     * Whether to exclude default features.
     *
     * @parameter
     */
    public boolean excludeDefaultFeatures;

    /**
     * Libraries available to application and runtime.
     *
     * @parameter
     */
    public Dependency[] shared;

    /**
     * Properties passed to the runtime throught the HostInfo interface.
     *
     * @parameter
     */
    public Properties properties;

    /**
     * @parameter expression="${project.testClasspathElements}"
     * @required
     * @readonly
     */
    public List<String> testClassPath;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    public ArtifactRepository localRepository;

    /**
     * Used to look up Artifacts in the remote repository.
     *
     * @parameter expression="${component.org.apache.maven.artifact.factory.ArtifactFactory}"
     * @required
     * @readonly
     */
    public ArtifactFactory artifactFactory;

    /**
     * @parameter expression="${component.org.sca4j.itest.ArtifactHelper}"
     * @required
     * @readonly
     */
    public ArtifactHelper artifactHelper;

    /**
     * @parameter expression="${component.org.sca4j.itest.ExtensionHelper}"
     * @required
     * @readonly
     */
    public ExtensionHelper extensionHelper;

    /**
     * The sub-directory of the project's output directory which contains the systemConfig.xml file. Users are limited to specifying the (relative)
     * directory name in this param - the file name is fixed. The fixed name is not required by the itest environment but using it retains the
     * relationship between the test config file and WEB-INF/systemConfig.xml which contains the same information for the deployed composite
     *
     * @parameter
     */
    public String systemConfigDir;

    /**
     * Allows the optional in-line specification of system configuration in the plugin configuration.
     *
     * @parameter
     */
    public String systemConfig;

    /**
     * Build output directory.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected File outputDirectory;

    // JMX management agent
    private Agent agent;

    // Resolved feature sets
    private List<FeatureSet> featureSets = new LinkedList<FeatureSet>();


    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (!testScdl.exists()) {
            getLog().info("No itest SCDL found, skipping integration tests");
            return;
        }
        if (skip) {
            getLog().info("Skipping integration tests by user request.");
            return;
        }

        artifactHelper.setLocalRepository(localRepository);
        artifactHelper.setProject(project);

        List<Dependency> featurestoInstall = getFeaturesToInstall();
        if (!featurestoInstall.isEmpty()) {
            for (Dependency feature : featurestoInstall) {
                Artifact artifact = artifactHelper.resolve(feature);
                try {
                    FeatureSet featureSet = FeatureSet.deserialize(artifact.getFile());
                    featureSets.add(featureSet);
                } catch (ParserConfigurationException e) {
                    throw new MojoExecutionException("Error booting SCA4J runtime", e);
                } catch (SAXException e) {
                    throw new MojoExecutionException("Error booting SCA4J runtime", e);
                } catch (IOException e) {
                    throw new MojoExecutionException("Error booting SCA4J runtime", e);
                }
            }
        }

        Set<Artifact> runtimeArtifacts = artifactHelper.calculateRuntimeArtifacts(runtimeVersion);
        Set<Artifact> hostArtifacts = artifactHelper.calculateHostArtifacts(runtimeArtifacts, shared, featureSets);
        Set<Artifact> dependencies = artifactHelper.calculateDependencies();
        Set<URL> moduleDependencies = artifactHelper.calculateModuleDependencies(dependencies, hostArtifacts);

        ClassLoader parentClassLoader = getClass().getClassLoader();
        ClassLoader hostClassLoader = createHostClassLoader(parentClassLoader, hostArtifacts);
        ClassLoader bootClassLoader = createBootClassLoader(hostClassLoader, runtimeArtifacts);

        Thread.currentThread().setContextClassLoader(bootClassLoader);

        MavenEmbeddedRuntime runtime = createRuntime(bootClassLoader, hostClassLoader, moduleDependencies);
        BootConfiguration<MavenEmbeddedRuntime, ScdlBootstrapper> configuration =
                createBootConfiguration(runtime, bootClassLoader, hostClassLoader);
        RuntimeLifecycleCoordinator<MavenEmbeddedRuntime, ScdlBootstrapper> coordinator =
                instantiate(RuntimeLifecycleCoordinator.class, coordinatorImpl, bootClassLoader);
        coordinator.setConfiguration(configuration);
        bootRuntime(coordinator);
        try {
            TestRunner runner = new TestRunner(testDomain,
                                               compositeNamespace,
                                               compositeName,
                                               testScdl,
                                               reportsDirectory,
                                               trimStackTrace,
                                               buildDirectory,
                                               getLog());
            runner.executeTests(runtime);
        } finally {
            try {
                shutdownRuntime(coordinator);
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void bootRuntime(RuntimeLifecycleCoordinator<MavenEmbeddedRuntime, ScdlBootstrapper> coordinator) throws MojoExecutionException {
        try {
            getLog().info("Starting Embedded SCA4J Runtime ...");
            coordinator.bootPrimordial();
            coordinator.initialize();
            Future<Void> future = coordinator.joinDomain(-1);
            future.get();
            future = coordinator.recover();
            future.get();
            future = coordinator.start();
            future.get();
        } catch (StartException e) {
            throw new MojoExecutionException("Error booting SCA4J runtime", e);
        } catch (ExecutionException e) {
            throw new MojoExecutionException("Error booting SCA4J runtime", e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Error booting SCA4J runtime", e);
        } catch (InitializationException e) {
            throw new MojoExecutionException("Error booting SCA4J runtime", e);
        }
    }

    private void shutdownRuntime(RuntimeLifecycleCoordinator<MavenEmbeddedRuntime, ScdlBootstrapper> coordinator)
            throws ShutdownException, InterruptedException, ExecutionException {
        getLog().info("Stopping SCA4J Runtime ...");
        Future<Void> future = coordinator.shutdown();
        future.get();
    }

    private BootConfiguration<MavenEmbeddedRuntime, ScdlBootstrapper> createBootConfiguration(MavenEmbeddedRuntime runtime,
                                                                                              ClassLoader bootClassLoader,
                                                                                              ClassLoader appClassLoader)
            throws MojoExecutionException {

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
        extensionHelper.processExtensions(configuration, extensions, featureSets);

        // process the baseline intents
        if (intentsLocation == null) {
            intentsLocation = bootClassLoader.getResource("META-INF/sca4j/intents.xml");
        }
        URI uri = URI.create("StandardIntents");
        ContributionSource source = new FileContributionSource(uri, intentsLocation, -1, new byte[0]);
        configuration.setIntents(source);
        configuration.setRuntime(runtime);
        return configuration;
    }

    private ScdlBootstrapper createBootstrapper(ClassLoader bootClassLoader) throws MojoExecutionException {
        ScdlBootstrapper bootstrapper = instantiate(ScdlBootstrapper.class, bootstrapperImpl, bootClassLoader);
        if (systemScdl == null) {
            systemScdl = bootClassLoader.getResource("META-INF/sca4j/embeddedMaven.composite");
        }
        bootstrapper.setScdlLocation(systemScdl);
        if (systemConfig != null) {
            Reader reader = new StringReader(systemConfig);
            InputSource source = new InputSource(reader);
            bootstrapper.setSystemConfig(source);
        } else {
            URL systemConfig = getSystemConfig();
            bootstrapper.setSystemConfig(systemConfig);
        }
        return bootstrapper;
    }

    private MavenEmbeddedRuntime createRuntime(ClassLoader bootClassLoader, ClassLoader hostClassLoader, Set<URL> moduleDependencies) {
        MonitorFactory monitorFactory = new MavenMonitorFactory(getLog(), "f3");
        MavenEmbeddedRuntime runtime = instantiate(MavenEmbeddedRuntime.class, runtimeImpl, bootClassLoader);
        runtime.setMonitorFactory(monitorFactory);
        runtime.setHostClassLoader(hostClassLoader);

        Properties hostProperties = properties != null ? properties : System.getProperties();
        MavenHostInfoImpl hostInfo = new MavenHostInfoImpl(URI.create(testDomain), hostProperties, moduleDependencies);
        runtime.setHostInfo(hostInfo);

        runtime.setJmxSubDomain(managementDomain);

        // TODO Add better host JMX support from the next release
        agent = new DefaultAgent();
        runtime.setMBeanServer(agent.getMBeanServer());

        return runtime;
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

    private ClassLoader createBootClassLoader(ClassLoader parent, Set<Artifact> artifacts)
            throws MojoExecutionException {
        URL[] urls = new URL[artifacts.size()];
        int i = 0;
        for (Artifact artifact : artifacts) {
            File file = artifact.getFile();
            assert file != null;
            try {
                urls[i++] = file.toURI().toURL();
            } catch (MalformedURLException e) {
                // toURI should have made this valid
                throw new AssertionError(e);
            }
        }

        Log log = getLog();
        if (log.isDebugEnabled()) {
            log.debug("SCA4J extension classpath:");
            for (URL url : urls) {
                log.debug("  " + url);
            }
        }
        return new MultiParentClassLoader(URI.create("sca4j://runtime/BootClassLoader"), urls, parent);
    }

    /**
     * Creates the host classloader based on the given set of artifacts.
     *
     * @param parent        the parent classloader
     * @param hostArtifacts the  artifacts
     * @return the host classloader
     */
    private ClassLoader createHostClassLoader(ClassLoader parent, Set<Artifact> hostArtifacts) {
        List<URL> urls = new ArrayList<URL>(hostArtifacts.size());
        for (Artifact artifact : hostArtifacts) {
            try {
                File pathElement = artifact.getFile();
                URL url = pathElement.toURI().toURL();
                getLog().debug("Adding artifact URL: " + url);
                urls.add(url);
            } catch (MalformedURLException e) {
                // toURI should have encoded the URL
                throw new AssertionError(e);
            }

        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), parent);
    }

    private URL getSystemConfig() throws MojoExecutionException {
        File systemConfig = new File(outputDirectory, DEFAULT_SYSTEM_CONFIG_DIR + SYSTEM_CONFIG_XML_FILE);
        if (systemConfigDir != null) {
            systemConfig = new File(outputDirectory, systemConfigDir + File.separator + SYSTEM_CONFIG_XML_FILE);
            if (!systemConfig.exists()) {
                //The user has explicitly attempted to configure the system config location but the information is incorrect
                throw new MojoExecutionException("Failed to find the system config information in: " + systemConfig.getAbsolutePath());
            }
        }

        Log log = getLog();
        if (log.isDebugEnabled()) {
            log.debug("Using system config information from: " + systemConfig.getAbsolutePath());
        }

        try {
            return systemConfig.exists() ? systemConfig.toURL() : null;
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Invalid system configuration: " + systemConfig, e);
        }
    }

    private List<Dependency> getFeaturesToInstall() {
        List<Dependency> featuresToInstall = new ArrayList<Dependency>();

        if (features != null) {
            featuresToInstall.addAll(Arrays.asList(features));
        }
        if (!excludeDefaultFeatures) {
            Dependency dependency = new Dependency();
            dependency.setArtifactId("sca4j-default-feature");
            dependency.setGroupId("org.sca4j");
            dependency.setVersion(runtimeVersion);
            dependency.setType("xml");
            featuresToInstall.add(dependency);
        }
        return featuresToInstall;
    }

    public interface MojoMonitor {
        @Severe
        void runError(Exception e);
    }

}
