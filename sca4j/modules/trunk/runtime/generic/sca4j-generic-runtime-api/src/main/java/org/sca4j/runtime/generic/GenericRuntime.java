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
package org.sca4j.runtime.generic;

import java.net.URL;

import javax.xml.namespace.QName;

import org.sca4j.host.runtime.HostInfo;

/**
 * Interface for the generic runtime.
 * 
 * @author meerajk
 *
 */
public interface GenericRuntime {
    
    /**
     * Adds a user contribution.
     * 
     * @param contributionUrl User contribution URL.
     */
    void contribute(URL contributionUrl);
    
    /**
     * Adds an extension contribution.
     * 
     * @param contributionUrl User contribution URL.
     */
    void contributeExtension(URL contributionUrl);
    
    /**
     * Sets any host specific information.
     * 
     * @param hostInfo Host specific information.
     */
    void setHostInfo(HostInfo hostInfo);
    
    /**
     * Sets the root classloader to use.
     * 
     * @param classLoader Root classloader.
     */
    void setRootClassloader(ClassLoader rootClassLoader);
    
    /**
     * Gets a service reference proxy.
     * 
     * @param <T> Type of the service.
     * @param serviceClass Class of the service.
     * @param serviceName Name of the service.
     * @return Proxy to the service.
     */
    <T> T getServiceProxy(Class<T> serviceClass, QName serviceName);
    
    /**
     * Boots the runtime.
     */
    void boot();

}
