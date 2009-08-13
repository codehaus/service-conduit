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

import org.sca4j.host.SCA4JException;

/**
 * An checked exception encountered by an {@link org.sca4j.spi.component.Component}
 *
 * @version $Rev: 2991 $ $Date: 2008-03-02 18:55:01 +0000 (Sun, 02 Mar 2008) $
 */
public abstract class ComponentException extends SCA4JException {
    private static final long serialVersionUID = 8347960569937255812L;

    protected ComponentException() {
    }

    protected ComponentException(String message) {
        super(message);
    }

    protected ComponentException(String message, String identifier) {
        super(message, identifier);
    }

    protected ComponentException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ComponentException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    protected ComponentException(Throwable cause) {
        super(cause);
    }
}
