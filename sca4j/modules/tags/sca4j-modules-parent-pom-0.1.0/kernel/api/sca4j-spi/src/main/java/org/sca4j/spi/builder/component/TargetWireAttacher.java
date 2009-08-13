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
package org.sca4j.spi.builder.component;

import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Rev: 2438 $ $Date: 2008-01-06 23:05:22 +0000 (Sun, 06 Jan 2008) $
 */
public interface TargetWireAttacher<PWTD extends PhysicalWireTargetDefinition> {
    /**
     * Attaches a wire to a target component or outgoing binding.
     *
     * @param source metadata for performing the attach
     * @param target metadata for performing the attach
     * @param wire   the wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    void attachToTarget(PhysicalWireSourceDefinition source, PWTD target, Wire wire) throws WiringException;

    /**
     * Create an ObjectFactory that can return values compatible with this wire.
     *
     * @param target metadata for performing the attach
     * @return an ObjectFactory that can return values compatible with this wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    ObjectFactory<?> createObjectFactory(PWTD target) throws WiringException;
}
