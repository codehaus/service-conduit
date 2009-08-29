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
package org.sca4j.itest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.sca4j.featureset.FeatureSet;
import org.xml.sax.SAXException;

/**
 * Run integration tests on a SCA composite using an embedded SCA4J runtime.
 *
 * @version $Rev: 5478 $ $Date: 2008-09-26 00:54:18 +0100 (Fri, 26 Sep 2008) $
 * @goal test
 * @phase integration-test
 */
public class SCA4JITestMojo extends AbstractMojo {

    /**
     * POM
     *
     * @parameter expression="${project}"
     * @readonly
     * @required
     */
    protected MavenProject project;

    /**
     * Whether to fork the plugin or not.
     *
     * @parameter
     */
    public boolean fork;

    /**
     * JVM arguments.
     *
     * @parameter
     */
    public String jvmargs;

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


    // Resolved feature sets
    private List<FeatureSet> featureSets = new LinkedList<FeatureSet>();


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

        TestMetadata testMetadata = new TestMetadata();
        
        Set<Artifact> runtimeArtifacts = artifactHelper.calculateRuntimeArtifacts(runtimeVersion);
        Set<Artifact> dependencies = artifactHelper.calculateDependencies();
        Set<Artifact> hostArtifacts = artifactHelper.calculateHostArtifacts(runtimeArtifacts, shared, featureSets);
        Set<URL> moduleDependencies = artifactHelper.calculateModuleDependencies(dependencies, hostArtifacts);
        
        testMetadata.setModuleDependencies(moduleDependencies);
        testMetadata.setRuntimeArtifacts(toFiles(runtimeArtifacts));
        testMetadata.setHostArtifacts(toFiles(hostArtifacts));

        testMetadata.setRuntimeImpl(runtimeImpl);
        testMetadata.setManagementDomain(managementDomain);
        testMetadata.setExtensions(extensionHelper.processExtensions(extensions, featureSets));
        testMetadata.setIntentsLocation(intentsLocation);
        testMetadata.setSystemScdl(systemScdl);
        testMetadata.setSystemConfigDir(systemConfigDir);
        testMetadata.setSystemConfig(systemConfig);
        testMetadata.setBootstrapperImpl(bootstrapperImpl);
        testMetadata.setOutputDirectory(outputDirectory);
        testMetadata.setCoordinatorImpl(coordinatorImpl);
        
        testMetadata.setTestDomain(testDomain);
        testMetadata.setCompositeNamespace(compositeNamespace);
        testMetadata.setCompositeName(compositeName);
        testMetadata.setTestScdl(testScdl);
        testMetadata.setReportsDirectory(reportsDirectory);
        testMetadata.setTrimStackTrace(trimStackTrace);
        testMetadata.setBuildDirectory(buildDirectory);
        testMetadata.setProperties(properties);
        
        try {
            
            if (!fork) {
                TestRunner runner = new TestRunner(testMetadata);
                runner.executeTests();
            } else {
                new Fork().run(testMetadata, getLog(), jvmargs);
            }
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        } catch (IOException e) {
            throw new AssertionError(e);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
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
    
    private Set<File> toFiles(Set<Artifact> artifacts) {
        Set<File> files = new HashSet<File>();
        for (Artifact artifact : artifacts) {
            files.add(artifact.getFile());
        }
        return files;
    }

}
