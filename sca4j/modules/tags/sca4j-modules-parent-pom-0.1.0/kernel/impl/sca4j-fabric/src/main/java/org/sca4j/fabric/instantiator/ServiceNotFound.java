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
import org.sca4j.spi.model.instance.Bindable;

public class ServiceNotFound extends AssemblyFailure {
    private String message;
    private Bindable bindable;
    private URI targetUri;

    public ServiceNotFound(String message, Bindable bindable, URI targetUri) {
        super(bindable.getParent().getUri());
        this.message = message;
        this.bindable = bindable;
        this.targetUri = targetUri;
    }

    public Bindable getBindable() {
        return bindable;
    }

    public URI getTargetUri() {
        return targetUri;
    }

    public String getMessage() {
        return message;
    }
}
