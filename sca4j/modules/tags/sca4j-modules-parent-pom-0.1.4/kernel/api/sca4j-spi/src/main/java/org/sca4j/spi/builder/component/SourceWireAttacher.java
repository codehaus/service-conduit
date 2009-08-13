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
 * Component that handles attachment of a wire at the source side of the invocation chain.
 * <p/>
 * Implementations attach physical wires to component implementations so that the implementation can invoke other components. These may be for
 * references or for callbacks.
 *
 * @version $Rev: 4832 $ $Date: 2008-06-20 11:33:47 +0100 (Fri, 20 Jun 2008) $
 */
public interface SourceWireAttacher<PWSD extends PhysicalWireSourceDefinition> {
    /**
     * Attaches a wire to a source component or and incoming binding.
     *
     * @param source metadata for performing the attach
     * @param target metadata for performing the attach
     * @param wire   the wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    void attachToSource(PWSD source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException;


    /**
     * Detach a wire from a source component
     * @param source
     * @param target
     * @param wire
     */
    void detachFromSource(PWSD source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException;
    /**
     * Attaches an ObjectFactory to a source component.
     *
     * @param source        the definition of the component reference to attach to
     * @param objectFactory an ObjectFactory that can produce values compatible with the reference
     * @param target    the target definition for the wire
     * @throws WiringException if an exception occurs during the attach operation
     */
    void attachObjectFactory(PWSD source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target) throws WiringException;
}
