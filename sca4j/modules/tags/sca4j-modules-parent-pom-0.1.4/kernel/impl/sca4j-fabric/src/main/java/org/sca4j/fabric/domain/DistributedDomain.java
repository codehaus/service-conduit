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
import org.sca4j.fabric.generator.PhysicalModelGenerator;
import org.sca4j.fabric.instantiator.LogicalModelInstantiator;
import org.sca4j.fabric.services.routing.RoutingService;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.lcm.LogicalComponentManager;

/**
 * Implements a distributed domain containing user-defined services.
 *
 * @version $Rev: 5271 $ $Date: 2008-08-25 22:46:23 +0100 (Mon, 25 Aug 2008) $
 */
public class DistributedDomain extends AbstractDomain implements Domain {

    public DistributedDomain(@Reference(name = "store")MetaDataStore metaDataStore,
                             @Reference(name = "logicalComponentManager")LogicalComponentManager logicalComponentManager,
                             @Reference Allocator allocator,
                             @Reference PhysicalModelGenerator physicalModelGenerator,
                             @Reference LogicalModelInstantiator logicalModelInstantiator,
                             @Reference RoutingService routingService) {
        super(metaDataStore, logicalComponentManager, allocator, physicalModelGenerator, logicalModelInstantiator, routingService);
    }

    /**
     * Used to reinject the Allocator. This allows an alternative allocation mechanism to be used by adding an optional extension to the runtime.
     *
     * @param allocator the allocator to override the default one
     */
    @Reference
    public void setAllocator(Allocator allocator) {
        this.allocator = allocator;
    }

}
