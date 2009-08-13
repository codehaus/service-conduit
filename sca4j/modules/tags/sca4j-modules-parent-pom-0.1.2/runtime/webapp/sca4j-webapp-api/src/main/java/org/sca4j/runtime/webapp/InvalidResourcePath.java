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

/**
 * Denotes an invalid or non-existing path for a resource required by the runtime bootstrap
 *
 * @version $Rev: 3499 $ $Date: 2008-03-31 01:16:09 +0100 (Mon, 31 Mar 2008) $
 */
public class InvalidResourcePath extends SCA4JInitException {
    private static final long serialVersionUID = 3265371682263989964L;

    public InvalidResourcePath(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
