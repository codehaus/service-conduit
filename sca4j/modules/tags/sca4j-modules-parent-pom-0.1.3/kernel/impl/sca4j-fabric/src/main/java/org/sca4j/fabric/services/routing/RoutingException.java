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
package org.sca4j.fabric.services.routing;

import org.sca4j.host.SCA4JException;

/**
 * Base routing exception.
 *
 * @version $Rev: 2965 $ $Date: 2008-02-29 17:47:48 +0000 (Fri, 29 Feb 2008) $
 */
public class RoutingException extends SCA4JException {
    private static final long serialVersionUID = -7865833725458046880L;

    protected RoutingException() {
    }

    protected RoutingException(String message, String identifier) {
        super(message, identifier);
    }

    protected RoutingException(Throwable cause) {
        super(cause);
    }

    protected RoutingException(String message, Throwable cause) {
        super(message, cause);
    }

    protected RoutingException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    protected RoutingException(String message) {
        super(message);
    }
}
