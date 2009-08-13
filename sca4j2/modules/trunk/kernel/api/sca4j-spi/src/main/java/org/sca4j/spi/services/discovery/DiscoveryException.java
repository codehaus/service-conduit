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
package org.sca4j.spi.services.discovery;

import org.sca4j.host.SCA4JException;

/**
 * Denotes a general exception when performing a discovery operation.
 *
 * @version $Rev: 2315 $ $Date: 2007-12-24 10:24:11 +0000 (Mon, 24 Dec 2007) $
 */
public abstract class DiscoveryException extends SCA4JException {
    private static final long serialVersionUID = 3978739627155168352L;

    protected DiscoveryException(String message, String identifier) {
        super(message, identifier);
    }

    public DiscoveryException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
