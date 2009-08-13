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
package org.sca4j.fabric.instantiator.component;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.scdl.Composite;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;

/**
 * Instantiates explicity wires (i.e. those declared by <wire>) in a composite and its included composites.
 *
 * @version $Revision$ $Date$
 */
public interface WireInstantiator {

    /**
     * Performs the instantiation.
     *
     * @param composite the composite
     * @param parent    the logical composite where the wires will be added
     * @param change    the current logical change set.
     */
    void instantiateWires(Composite composite, LogicalCompositeComponent parent, LogicalChange change);

}
