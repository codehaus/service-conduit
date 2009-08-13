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
package org.sca4j.proxy.jdk;

import org.sca4j.spi.services.proxy.ProxyCreationException;

/**
 * Thrown when an {@link org.sca4j.scdl.Operation} cannot be mapped to a method on an interface
 *
 * @version $Rev: 2957 $ $Date: 2008-02-29 08:59:16 -0800 (Fri, 29 Feb 2008) $
 */
public class NoMethodForOperationException extends ProxyCreationException {
    private static final long serialVersionUID = -2770346906058273180L;

    public NoMethodForOperationException() {
    }

    public NoMethodForOperationException(String message) {
        super(message);
    }

    public NoMethodForOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMethodForOperationException(Throwable cause) {
        super(cause);
    }
}
