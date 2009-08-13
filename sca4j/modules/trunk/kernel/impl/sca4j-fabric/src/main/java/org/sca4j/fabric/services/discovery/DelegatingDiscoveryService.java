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
package org.sca4j.fabric.services.discovery;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.model.topology.RuntimeInfo;
import org.sca4j.spi.services.discovery.DiscoveryException;
import org.sca4j.spi.services.discovery.DiscoveryService;
import org.sca4j.spi.services.discovery.DiscoveryServiceRegistry;
import org.sca4j.spi.services.runtime.RuntimeInfoService;

/**
 * A DiscoveryService implementaton that delegates accross mutliple discovery protocols.
 *
 * @version $Rev: 2380 $ $Date: 2007-12-28 09:42:33 +0000 (Fri, 28 Dec 2007) $
 */
public class DelegatingDiscoveryService implements DiscoveryService {
    private DiscoveryServiceRegistry registry;
    private RuntimeInfoService runtimeInfoService;

    public DelegatingDiscoveryService(@Reference DiscoveryServiceRegistry registry,
                                      @Reference RuntimeInfoService runtimeInfoService) {
        this.registry = registry;
        this.runtimeInfoService = runtimeInfoService;
    }

    public Set<RuntimeInfo> getParticipatingRuntimes() {
        Set<RuntimeInfo> infos = new HashSet<RuntimeInfo>();
        for (DiscoveryService service : registry.getServices()) {
            infos.addAll(service.getParticipatingRuntimes());
        }
        return infos;
    }

    public RuntimeInfo getRuntimeInfo(URI runtimeId) {
        if (runtimeId == null) {
            // null runtime id denotes the current runtime
            return runtimeInfoService.getRuntimeInfo();
        }
        for (DiscoveryService service : registry.getServices()) {
            RuntimeInfo info = service.getRuntimeInfo(runtimeId);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    public void joinDomain(long timeout) throws DiscoveryException {
        long elapsed;
        long start = System.currentTimeMillis();
        for (DiscoveryService service : registry.getServices()) {
            if (timeout > -1) {
                elapsed = System.currentTimeMillis() - start;
                if (elapsed > timeout) {
                    throw new DiscoveryTimeoutException("Timeout joining domain");
                }
                service.joinDomain(timeout - elapsed);

            } else {
                service.joinDomain(timeout);
            }
        }
    }
}
