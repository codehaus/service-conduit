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
package org.sca4j.fabric.instantiator.target;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;

/**
 * Abstraction for resolving targets for references. Possible implementations include explicit targets, intent based auto-wiring, and type based
 * auto-wiring. Resolution strategies are exclusive. That is, only one strategy is used per reference.
 *
 * @version $Revision$ $Date$
 */
public interface TargetResolutionService {

    /**
     * Resolves the target for a logical reference.
     *
     * @param reference Logical reference whose target needs to be resolved.
     * @param component Composite component within which the targets are resolved.
     * @param change    the logical change the resolution is performed for. Errors and warnings are reported here.
     */
    void resolve(LogicalReference reference, LogicalCompositeComponent component, LogicalChange change);

}
