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
package org.sca4.runtime.generic.impl;

import java.net.URL;

import javax.xml.namespace.QName;

import org.sca4j.host.runtime.HostInfo;
import org.sca4j.runtime.generic.GenericRuntime;

/**
 * Default implementation of the generic runtime.
 * 
 * @author meerajk
 *
 */
public class GenericRuntimeImpl implements GenericRuntime {
    
    private HostInfo hostInfo;
    private ClassLoader rootClassLoader;

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#contributeExtension(java.net.URL)
     */
    public void contributeExtension(URL contributionUrl) {
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#contribute(java.net.URL)
     */
    public void contribute(URL contributionUrl) {
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#boot()
     */
    public void boot() {
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#getServiceProxy(java.lang.Class, javax.xml.namespace.QName)
     */
    public <T> T getServiceProxy(Class<T> serviceClass, QName serviceName) {
        return null;
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#setHostInfo(org.sca4j.host.runtime.HostInfo)
     */
    public void setHostInfo(HostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }

    /**
     * @see org.sca4j.runtime.generic.GenericRuntime#setRootClassloader(java.lang.ClassLoader)
     */
    public void setRootClassloader(ClassLoader rootClassLoader) {
        this.rootClassLoader = rootClassLoader;
    }

}
