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
package org.sca4j.host.runtime;

import java.net.URL;
import java.util.List;

import org.sca4j.host.contribution.ContributionSource;
import org.xml.sax.InputSource;

/**
 * Encapsulates configuration needed to boostrap a runtime.
 *
 * @version $Revision$ $Date$
 */
public class BootConfiguration {
    
    private SCA4JRuntime<?> runtime;
    private ClassLoader bootClassLoader;
    private ClassLoader appClassLoader;
    private ClassLoader hostClassLoader;
    private List<String> bootExports;
    private ContributionSource intents;
    private List<ContributionSource> extensions;
    private URL systemScdl;
    private InputSource systemConfigDocument;
    private URL systemConfig;

    public SCA4JRuntime<?> getRuntime() {
        return runtime;
    }

    public void setRuntime(SCA4JRuntime<?> runtime) {
        this.runtime = runtime;
    }

    public URL getSystemScdl() {
        return systemScdl;
    }

    public void setSystemScdl(URL systemScdl) {
        this.systemScdl = systemScdl;
    }

    public InputSource getSystemConfigDocument() {
        return systemConfigDocument;
    }

    public void setSystemConfigDocument(InputSource systemConfigDocument) {
        this.systemConfigDocument = systemConfigDocument;
    }

    public URL getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(URL systemConfig) {
        this.systemConfig = systemConfig;
    }

    public ClassLoader getHostClassLoader() {
        return hostClassLoader;
    }

    public void setHostClassLoader(ClassLoader hostClassLoader) {
        this.hostClassLoader = hostClassLoader;
    }

    public ClassLoader getBootClassLoader() {
        return bootClassLoader;
    }

    public void setBootClassLoader(ClassLoader bootClassLoader) {
        this.bootClassLoader = bootClassLoader;
    }

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public void setAppClassLoader(ClassLoader appClassLoader) {
        this.appClassLoader = appClassLoader;
    }

    public List<String> getBootLibraryExports() {
        return bootExports;
    }

    public void setBootLibraryExports(List<String> bootExports) {
        this.bootExports = bootExports;
    }

    public ContributionSource getIntents() {
        return intents;
    }

    public void setIntents(ContributionSource intents) {
        this.intents = intents;
    }

    public List<ContributionSource> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<ContributionSource> extensions) {
        this.extensions = extensions;
    }

}
