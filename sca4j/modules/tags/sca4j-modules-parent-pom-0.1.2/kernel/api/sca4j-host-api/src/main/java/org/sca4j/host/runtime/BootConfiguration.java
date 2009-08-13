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

import java.util.List;

import org.sca4j.host.contribution.ContributionSource;

/**
 * Encapsulates configuration needed to boostrap a runtime.
 *
 * @version $Revision$ $Date$
 */
public class BootConfiguration<RUNTIME extends SCA4JRuntime<?>, BOOTSTRAPPER extends Bootstrapper> {
    private RUNTIME runtime;
    private BOOTSTRAPPER bootstrapper;
    private ClassLoader bootClassLoader;
    private ClassLoader appClassLoader;
    private List<String> bootExports;
    private ContributionSource intents;
    private List<ContributionSource> extensions;

    public RUNTIME getRuntime() {
        return runtime;
    }

    public void setRuntime(RUNTIME runtime) {
        this.runtime = runtime;
    }

    public BOOTSTRAPPER getBootstrapper() {
        return bootstrapper;
    }

    public void setBootstrapper(BOOTSTRAPPER bootstrapper) {
        this.bootstrapper = bootstrapper;
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
