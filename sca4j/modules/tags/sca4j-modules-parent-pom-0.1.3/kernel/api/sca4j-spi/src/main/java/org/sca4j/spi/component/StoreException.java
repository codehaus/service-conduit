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

import org.sca4j.host.SCA4JRuntimeException;

/**
 * @version $Rev: 2990 $ $Date: 2008-03-02 18:49:47 +0000 (Sun, 02 Mar 2008) $
 */
public abstract class StoreException extends SCA4JRuntimeException {
    private static final long serialVersionUID = 3587233858463631351L;

    protected StoreException() {
    }

    protected StoreException(String message) {
        super(message);
    }

    protected StoreException(String message, String identifier) {
        super(message, identifier);
    }

    protected StoreException(String message, Throwable cause) {
        super(message, cause);
    }

    protected StoreException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    protected StoreException(Throwable cause) {
        super(cause);
    }
}
