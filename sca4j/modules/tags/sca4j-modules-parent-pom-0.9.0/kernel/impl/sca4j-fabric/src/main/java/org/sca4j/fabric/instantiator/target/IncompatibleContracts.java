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

import java.net.URI;

import org.sca4j.host.domain.AssemblyFailure;

/**
 * Thrown when an attempt is made to wire a reference to a service with incompatible contracts.
 *
 * @version $Revision$ $Date$
 */
public class IncompatibleContracts extends AssemblyFailure {
    private URI referenceUri;
    private URI serviceUri;

    /**
     * Constructor.
     *
     * @param componentUri the URI of the component associated with the failure.
     * @param referenceUri the URI of the reference
     * @param serviceUri   the URI of the service
     */
    public IncompatibleContracts(URI componentUri, URI referenceUri, URI serviceUri) {
        super(componentUri);
        this.referenceUri = referenceUri;
        this.serviceUri = serviceUri;
    }

    public String getMessage() {
        return "The contracts for the reference " + referenceUri + " and service " + serviceUri + " are incompatible";
    }
}
