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
package org.sca4j.spi.component;

/**
 * Denotes an error with a component instance
 *
 * @version $Rev: 4786 $ $Date: 2008-06-08 14:37:24 +0100 (Sun, 08 Jun 2008) $
 */
public class InstanceLifecycleException extends InstanceException {
    private static final long serialVersionUID = 5096941714950544095L;

    public InstanceLifecycleException(String message) {
        super(message);
    }

    public InstanceLifecycleException(String message, String identifier) {
        super(message, identifier);
    }

    public InstanceLifecycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstanceLifecycleException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
