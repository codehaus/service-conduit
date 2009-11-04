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
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.sca4j.host.contribution.ContributionSource;

/**
 * @author meerajk
 * 
 */
public class TestMetadata {

    private String testDomain;
    private String compositeNamespace;
    private String compositeName;
    private File testScdl;
    private File reportsDirectory;
    private boolean trimStackTrace;
    private File buildDirectory;
    private String runtimeImpl;
    private String bootstrapperImpl;
    private Properties properties;
    private String managementDomain;
    private URL intentsLocation;
    private URL systemScdl;
    private String systemConfigDir;
    private String systemConfig;

    private Set<File> runtimeArtifacts;
    private Set<File> hostArtifacts;
    private Set<URL> moduleDependencies;
    private Set<URL> classpath;
    private List<ContributionSource> extensions;
    private File outputDirectory;
    private String coordinatorImpl;

    public void setTestDomain(String testDomain) {
        this.testDomain = testDomain;
    }

    public String getTestDomain() {
        return testDomain;
    }

    public void setCompositeNamespace(String compositeNamespace) {
        this.compositeNamespace = compositeNamespace;
    }

    public String getCompositeNamespace() {
        return compositeNamespace;
    }

    public void setCompositeName(String compositeName) {
        this.compositeName = compositeName;
    }

    public String getCompositeName() {
        return compositeName;
    }

    public void setTestScdl(File testScdl) {
        this.testScdl = testScdl;
    }

    public File getTestScdl() {
        return testScdl;
    }

    public void setReportsDirectory(File reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }

    public File getReportsDirectory() {
        return reportsDirectory;
    }

    public void setSystemConfig(String systemConfig) {
        this.systemConfig = systemConfig;
    }

    public String getSystemConfig() {
        return systemConfig;
    }

    public void setCoordinatorImpl(String coordinatorImpl) {
        this.coordinatorImpl = coordinatorImpl;
    }

    public String getCoordinatorImpl() {
        return coordinatorImpl;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setExtensions(List<ContributionSource> extensions) {
        this.extensions = extensions;
    }

    public List<ContributionSource> getExtensions() {
        return extensions;
    }

    public void setModuleDependencies(Set<URL> moduleDependencies) {
        this.moduleDependencies = moduleDependencies;
    }

    public Set<URL> getModuleDependencies() {
        return moduleDependencies;
    }

    public void setClasspath(Set<URL> classpath) {
        this.classpath = classpath;
    }

    public Set<URL> getClasspath() {
        return classpath;
    }

    public void setHostArtifacts(Set<File> hostArtifacts) {
        this.hostArtifacts = hostArtifacts;
    }

    public Set<File> getHostArtifacts() {
        return hostArtifacts;
    }

    public void setRuntimeArtifacts(Set<File> runtimeArtifacts) {
        this.runtimeArtifacts = runtimeArtifacts;
    }

    public Set<File> getRuntimeArtifacts() {
        return runtimeArtifacts;
    }

    public void setSystemConfigDir(String systemConfigDir) {
        this.systemConfigDir = systemConfigDir;
    }

    public String getSystemConfigDir() {
        return systemConfigDir;
    }

    public void setSystemScdl(URL systemScdl) {
        this.systemScdl = systemScdl;
    }

    public URL getSystemScdl() {
        return systemScdl;
    }

    public void setIntentsLocation(URL intentsLocation) {
        this.intentsLocation = intentsLocation;
    }

    public URL getIntentsLocation() {
        return intentsLocation;
    }

    public void setManagementDomain(String managementDomain) {
        this.managementDomain = managementDomain;
    }

    public String getManagementDomain() {
        return managementDomain;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setBootstrapperImpl(String bootstrapperImpl) {
        this.bootstrapperImpl = bootstrapperImpl;
    }

    public String getBootstrapperImpl() {
        return bootstrapperImpl;
    }

    public void setRuntimeImpl(String runtimeImpl) {
        this.runtimeImpl = runtimeImpl;
    }

    public String getRuntimeImpl() {
        return runtimeImpl;
    }

    public void setBuildDirectory(File buildDirectory) {
        this.buildDirectory = buildDirectory;
    }

    public File getBuildDirectory() {
        return buildDirectory;
    }

    public void setTrimStackTrace(boolean trimStackTrace) {
        this.trimStackTrace = trimStackTrace;
    }

    public boolean isTrimStackTrace() {
        return trimStackTrace;
    }

}
