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
package org.sca4j.fabric.domain;

import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.allocator.Allocator;
import org.sca4j.fabric.binding.BindingSelector;
import org.sca4j.fabric.generator.PhysicalModelGenerator;
import org.sca4j.fabric.instantiator.LogicalModelInstantiator;
import org.sca4j.fabric.services.routing.RoutingService;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.lcm.LogicalComponentManager;

/**
 * Implements a domain for system components in a runtime. SCA4J runtimes are constituted using SCA components and the runtime domain manages
 * deployment of those system components. When a runtime is booted, the runtime domain is provided with a set of primoridal services for deploying
 * system components. After bootstrap, the runtime domain is reinjected with a new set of fully-configured deployment services.
 *
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
public class RuntimeDomain extends AbstractDomain {

    public RuntimeDomain(@Reference Allocator allocator,
                         @Reference MetaDataStore metadataStore,
                         @Reference PhysicalModelGenerator physicalModelGenerator,
                         @Reference LogicalModelInstantiator logicalModelInstantiator,
                         @Reference LogicalComponentManager logicalComponentManager,
                         @Reference BindingSelector bindingSelector,
                         @Reference RoutingService routingService) {
        super(metadataStore, logicalComponentManager, allocator, physicalModelGenerator, logicalModelInstantiator, bindingSelector, routingService);
    }

    /**
     * Used for reinjection.
     *
     * @param physicalModelGenerator the generator to inject
     */
    @Reference
    public void setPhysicalModelGenerator(PhysicalModelGenerator physicalModelGenerator) {
        this.physicalModelGenerator = physicalModelGenerator;
    }

    /**
     * Used for reinjection.
     *
     * @param routingService the routing service to reinject
     */
    @Reference
    public void setRoutingService(RoutingService routingService) {
        this.routingService = routingService;
    }


}
