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

import java.net.URI;

import org.sca4j.host.domain.AssemblyFailure;
import org.sca4j.spi.util.UriHelper;

public class WireSourceAmbiguousReference extends AssemblyFailure {
    private URI sourceUri;

    public WireSourceAmbiguousReference(URI sourceUri, URI compositeUri) {
        super(compositeUri);
        this.sourceUri = sourceUri;
    }

    public URI getSourceUri() {
        return sourceUri;
    }

    public String getMessage() {
        return "The component " + UriHelper.getDefragmentedName(sourceUri) + " specified as a wire source in "
                + getComponentUri() + " has more than one reference. A reference must be specified in the wire.";
    }

}
