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
package org.sca4j.fabric.services.discovery;

import org.sca4j.spi.services.discovery.DiscoveryException;

/**
 * Denotes a timeout while performing a discovery operation.
 *
 * @version $Rev: 2315 $ $Date: 2007-12-24 10:24:11 +0000 (Mon, 24 Dec 2007) $
 */
public class DiscoveryTimeoutException extends DiscoveryException {
    private static final long serialVersionUID = 7156621402105248064L;

    protected DiscoveryTimeoutException(String message) {
        super(message, null);
    }
}
