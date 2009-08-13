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
package org.sca4j.fabric.executor;

import org.sca4j.spi.executor.ExecutionException;

/**
 * Denotes an error executing the {@link org.sca4j.fabric.command.InitializeComponentCommand}
 *
 * @version $Rev: 3681 $ $Date: 2008-04-19 19:00:58 +0100 (Sat, 19 Apr 2008) $
 */
public class InitializeException extends ExecutionException {
    private static final long serialVersionUID = -276254239988931352L;

    public InitializeException(String message, String identifier) {
        super(message, identifier);
    }

    public InitializeException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
