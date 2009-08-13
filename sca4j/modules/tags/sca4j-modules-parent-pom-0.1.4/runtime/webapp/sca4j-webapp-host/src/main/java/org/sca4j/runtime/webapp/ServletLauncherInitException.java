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
package org.sca4j.runtime.webapp;

import org.sca4j.host.SCA4JRuntimeException;

/**
 * Thrown when an error is encountered booting the runtme in a web app environment
 *
 * @version $Rev: 29 $ $Date: 2007-05-15 21:28:09 +0100 (Tue, 15 May 2007) $
 */
public class ServletLauncherInitException extends SCA4JRuntimeException {

    public ServletLauncherInitException() {
    }

    public ServletLauncherInitException(String message) {
        super(message);
    }

    public ServletLauncherInitException(String message, String identifier) {
        super(message, identifier);
    }

    public ServletLauncherInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServletLauncherInitException(Throwable cause) {
        super(cause);
    }
}
