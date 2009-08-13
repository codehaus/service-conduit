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
package org.sca4j.fabric.instantiator;

import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;

/**
 * Abstraction for resolving reference targets, reference promotions, and serivce promotions. Resolution involves determining the fully-qualified URI
 * of a promoted service, promoted reference, or a reference target.
 *
 * @version $Revision$ $Date$
 */
public interface ResolutionService {

    /**
     * Resolves promoted references and services as well as reference targets for the logical component. If the component is a composite, its children
     * will be resolved.
     *
     * @param logicalComponent logical component to be resolved.
     * @param change           the logical change associated with the deployment operation resolution is being performed for. Recoverable errors and
     *                         warnings should be reported here.
     */
    void resolve(LogicalComponent<?> logicalComponent, LogicalChange change);

    /**
     * Resolves the promotion on the specified logical service.
     *
     * @param logicalService Logical service whose promotion is to be resolved.
     * @param change         the logical change associated with the deployment operation resolution is being performed for. Recoverable errors and
     *                       warnings should be reported here.
     */
    void resolve(LogicalService logicalService, LogicalChange change);

    /**
     * Resolves the logical reference against the given composite.
     *
     * @param logicalReference Logical reference to be resolved.
     * @param composite        Composite component against which the targets are resolved.
     * @param change           the logical change associated with the deployment operation resolution is being performed for. Recoverable errors and
     *                         warnings should be reported here.
     */
    void resolve(LogicalReference logicalReference, LogicalCompositeComponent composite, LogicalChange change);
}
