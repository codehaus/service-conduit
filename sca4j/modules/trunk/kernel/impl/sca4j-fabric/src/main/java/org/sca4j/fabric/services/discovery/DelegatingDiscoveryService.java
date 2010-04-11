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
