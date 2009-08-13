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
