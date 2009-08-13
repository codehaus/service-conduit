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
package org.sca4j.spi.services.discovery;

import java.util.List;

/**
 * A registry of DiscoveryServices. Used in runtimes that support more than one DiscoveryService.
 *
 * @version $Rev: 2315 $ $Date: 2007-12-24 10:24:11 +0000 (Mon, 24 Dec 2007) $
 */
public interface DiscoveryServiceRegistry {

    /**
     * Registers a DiscoveryService
     *
     * @param service the service to register
     */
    void register(DiscoveryService service);

    /**
     * Un-registers a DiscoveryService
     *
     * @param service the service to unregister
     */
    void unRegister(DiscoveryService service);

    /**
     * Returns all registered DiscoveryServices.
     *
     * @return all registered DiscoveryServices
     */
    List<DiscoveryService> getServices();

}
