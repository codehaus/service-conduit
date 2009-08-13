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
package org.sca4j.fabric.allocator;

import org.sca4j.host.SCA4JException;

/**
 * Denotes an error allocating a component to a service node.
 *
 * @version $Rev: 4796 $ $Date: 2008-06-08 18:27:51 +0100 (Sun, 08 Jun 2008) $
 */
public class AllocationException extends SCA4JException {
    private static final long serialVersionUID = 3960592897460184482L;

    public AllocationException(String message, String identifier) {
        super(message, identifier);
    }

    public AllocationException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public AllocationException(Throwable cause) {
        super(cause);
    }
}
