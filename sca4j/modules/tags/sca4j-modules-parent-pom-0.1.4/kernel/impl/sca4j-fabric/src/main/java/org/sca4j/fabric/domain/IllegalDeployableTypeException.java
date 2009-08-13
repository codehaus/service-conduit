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
package org.sca4j.fabric.domain;

import org.sca4j.host.domain.DeploymentException;

/**
 * @version $Rev: 5085 $ $Date: 2008-07-23 17:39:52 +0100 (Wed, 23 Jul 2008) $
 */
public class IllegalDeployableTypeException extends DeploymentException {
    private static final long serialVersionUID = -4209666034283825327L;

    public IllegalDeployableTypeException(String message, String identifier) {
        super(message, identifier);
    }
}
