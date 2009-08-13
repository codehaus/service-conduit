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

import java.net.URI;

import org.sca4j.host.domain.AssemblyFailure;
import org.sca4j.spi.model.instance.LogicalReference;

public class AmbiguousReference extends AssemblyFailure {
    private LogicalReference logicalReference;
    private URI promotedComponentUri;

    /**
     * Constructor.
     *
     * @param logicalReference     the logical reference that is invalid
     * @param promotedComponentUri the promoted component URI.
     */
    public AmbiguousReference(LogicalReference logicalReference, URI promotedComponentUri) {
        super(logicalReference.getParent().getUri());
        this.logicalReference = logicalReference;
        this.promotedComponentUri = promotedComponentUri;
    }

    public String getMessage() {
        return "The promoted reference " + logicalReference.getUri() + " must explicitly specify the reference it is promoting on component "
                + promotedComponentUri + " as the component has more than one reference";
    }
}
