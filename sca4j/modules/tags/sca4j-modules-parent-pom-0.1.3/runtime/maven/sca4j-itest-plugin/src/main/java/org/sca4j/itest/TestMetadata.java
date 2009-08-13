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
