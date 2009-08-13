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
package org.sca4j.fabric.instantiator.normalize;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * Merges binding and other metadata on promoted services and references down the to the leaf component they are initially defined on.
 *
 * @version $Rev: 5095 $ $Date: 2008-07-28 18:49:36 +0100 (Mon, 28 Jul 2008) $
 */
public interface PromotionNormalizer {

    /**
     * Performs the normalization operation on services and references defined by the given leaf component. The hierarchy of containing components
     * will be walked to determine the set of promoted services and references.
     *
     * @param component the leaf component
     * @param change    the logical change to update
     */
    void normalize(LogicalComponent<?> component, LogicalChange change);

}
