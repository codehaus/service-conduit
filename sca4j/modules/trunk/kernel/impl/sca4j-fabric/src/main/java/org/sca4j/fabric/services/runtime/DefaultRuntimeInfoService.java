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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.fabric.services.runtime;

import java.net.URI;
import java.net.URISyntaxException;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.model.topology.RuntimeInfo;
import org.sca4j.spi.services.advertisement.AdvertisementService;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.runtime.RuntimeInfoService;

/**
 * Default implementation of the <code>RuntimeInfoService</code>. The implementation relies on the <code>AdvertisementService</code> for getting the
 * features, the <code>ComponentManager</code> for getting the list of running components and the <code>HostInfo</code> for getting the runtime id.
 *
 * @version $Revsion$ $Date$
 */
public class DefaultRuntimeInfoService implements RuntimeInfoService {
    private AdvertisementService advertService;
    private ComponentManager componentManager;
    private HostInfo hostInfo;
    private URI runtimeId;
    private String messageDestination;

    public DefaultRuntimeInfoService(@Reference(name = "advertisementService")AdvertisementService advertService,
                                     @Reference(name = "componentManager")ComponentManager componentManager,
                                     @Reference(name = "hostInfo")HostInfo hostInfo,
                                     @Property(name = "scheme", required = false)String scheme,
                                     @Property(name = "runtimeId", required = false)String runtimeId) throws URISyntaxException {
        this.advertService = advertService;
        this.componentManager = componentManager;
        this.hostInfo = hostInfo;
        if (runtimeId != null) {
            this.runtimeId = new URI(scheme, runtimeId, null);
        }
    }

    public void registerMessageDestination(String destination) {
        messageDestination = destination;
    }

    public URI getCurrentRuntimeId() {
        return runtimeId;
    }

    public RuntimeInfo getRuntimeInfo() {
        RuntimeInfo runtimeInfo = new RuntimeInfo(runtimeId);
        // add features
        runtimeInfo.setFeatures(advertService.getFeatures());
        runtimeInfo.setTransportInfo(advertService.getTransportInfo());
        // add component URIs
        for (URI componentUri : componentManager.getComponentsInHierarchy(hostInfo.getDomain())) {
            runtimeInfo.addComponent(componentUri);
        }
        runtimeInfo.setMessageDestination(messageDestination);
        return runtimeInfo;

    }

}
