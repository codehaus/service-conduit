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
package org.sca4j.fabric.wire;

import java.util.HashMap;
import java.util.Map;

import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Default implementation of a Wire
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class WireImpl implements Wire {
    private final Map<PhysicalOperationDefinition, InvocationChain> chains =
            new HashMap<PhysicalOperationDefinition, InvocationChain>();

    public void addInvocationChain(PhysicalOperationDefinition operation, InvocationChain chain) {
        chains.put(operation, chain);
    }

    public Map<PhysicalOperationDefinition, InvocationChain> getInvocationChains() {
        return chains;
    }

}
