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
package org.sca4j.host.domain;

import org.sca4j.host.SCA4JException;

/**
 * Base exception for the domain package.
 *
 * @version $Rev: 5085 $ $Date: 2008-07-23 17:39:52 +0100 (Wed, 23 Jul 2008) $
 */
public class DomainException extends SCA4JException {
    private static final long serialVersionUID = -2529045209367837417L;

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, String identifier) {
        super(message, identifier);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public DomainException() {
    }

    public DomainException(Throwable cause) {
        super(cause);
    }
}
