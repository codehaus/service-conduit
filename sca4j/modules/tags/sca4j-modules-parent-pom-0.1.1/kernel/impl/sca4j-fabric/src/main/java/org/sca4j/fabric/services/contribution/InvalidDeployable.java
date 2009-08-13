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
package org.sca4j.fabric.services.contribution;

import java.net.URI;
import javax.xml.namespace.QName;

import org.sca4j.host.contribution.ValidationFailure;

/**
 * Thrown when a deployable composite specified in the sca-contribution is invalid.
 *
 * @version $Rev: 4912 $ $Date: 2008-06-26 23:28:37 +0100 (Thu, 26 Jun 2008) $
 */
public class InvalidDeployable extends ValidationFailure<URI> {
    private String message;
    private QName deployable;

    /**
     * Constructor.
     *
     * @param message    the error message
     * @param uri        the contribution URI
     * @param deployable the deployable qualified name
     */
    public InvalidDeployable(String message, URI uri, QName deployable) {
        super(uri);
        this.message = message;
        this.deployable = deployable;
    }

    public QName getDeployable() {
        return deployable;
    }

    public void setDeployable(QName deployable) {
        this.deployable = deployable;
    }

    public String getMessage() {
        return message;
    }
}
