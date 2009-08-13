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
package org.sca4j.fabric.allocator;

import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * Allocates a component to a service node.
 *
 * @version $Rev: 4775 $ $Date: 2008-06-08 11:40:14 +0100 (Sun, 08 Jun 2008) $
 */
public interface Allocator {

    /**
     * Performs the allocation. Composites are recursed and their children are allocated.
     *
     * @param component           the component to allocate
     * @param synchronizeTopology true if the allocator should attempt to synchronize its view of the domain topology with service nodes components
     *                            have been pre-allocated to. Synchronization will attempt to poll a set number of times for runtimes components are
     *                            pre-allocated to. If a runtime is not found, corresponding pre-allocated components will be marked for
     *                            re-allocation.
     * @throws AllocationException if an error during allocation occurs
     */
    void allocate(LogicalComponent<?> component, boolean synchronizeTopology) throws AllocationException;
}
