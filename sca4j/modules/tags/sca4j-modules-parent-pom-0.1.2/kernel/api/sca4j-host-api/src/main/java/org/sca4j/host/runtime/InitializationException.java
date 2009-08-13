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
package org.sca4j.host.runtime;

import org.sca4j.host.SCA4JException;

/**
 * Denotes an error starting the runtime
 *
 * @version $Rev: 642 $ $Date: 2007-08-01 19:11:17 +0100 (Wed, 01 Aug 2007) $
 */
public class InitializationException extends SCA4JException {

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, String identifier) {
        super(message, identifier);
    }

    public InitializationException(Throwable cause) {
        super(cause);
    }

    public InitializationException(String message, Throwable cause) {
        super(message, null, cause);
    }

    public InitializationException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
