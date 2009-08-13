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
package org.sca4j.introspection;

import org.sca4j.host.SCA4JException;

/**
 * @version $Rev: 4288 $ $Date: 2008-05-21 21:27:11 +0100 (Wed, 21 May 2008) $
 */
public abstract class IntrospectionException extends SCA4JException {
    private static final long serialVersionUID = -7824532555093050899L;

    protected IntrospectionException() {
    }

    protected IntrospectionException(String message) {
        super(message);
    }

    protected IntrospectionException(String message, Throwable cause) {
        super(message, cause);
    }

    protected IntrospectionException(Throwable cause) {
        super(cause);
    }
}
