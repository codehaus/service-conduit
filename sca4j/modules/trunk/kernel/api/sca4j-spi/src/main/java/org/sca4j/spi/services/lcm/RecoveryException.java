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
package org.sca4j.spi.services.lcm;

import org.sca4j.host.SCA4JException;


/**
 * @version $Rev: 4794 $ $Date: 2008-06-08 18:14:05 +0100 (Sun, 08 Jun 2008) $
 */
public class RecoveryException extends SCA4JException {
    private static final long serialVersionUID = -7196926074584212395L;

    public RecoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecoveryException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
