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

import java.util.Collections;
import java.util.Set;
import java.net.URI;

import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.model.topology.RuntimeInfo;
import org.sca4j.spi.services.discovery.DiscoveryService;
import org.sca4j.spi.services.runtime.RuntimeInfoService;

/**
 * A single node discovery service
 *
 * @version $Rev: 2328 $ $Date: 2007-12-24 14:17:29 +0000 (Mon, 24 Dec 2007) $
 */
public class SingleVMDiscoveryService implements DiscoveryService {
    private RuntimeInfoService service;

    public SingleVMDiscoveryService(@Reference RuntimeInfoService service) {
        this.service = service;
    }

    public Set<RuntimeInfo> getParticipatingRuntimes() {
        return Collections.emptySet();
    }

    public RuntimeInfo getRuntimeInfo(URI runtimeId) {
        return service.getRuntimeInfo();
    }

    public void joinDomain(long timeout) {

    }
}
