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
package org.sca4j.jetty;

import org.sca4j.host.SCA4JException;

/**
 * @version $Rev: 5352 $ $Date: 2008-09-08 21:49:06 +0100 (Mon, 08 Sep 2008) $
 */
public class JettyInitializationException extends SCA4JException {
    private static final long serialVersionUID = 1918582897250667465L;

    public JettyInitializationException(String message) {
        super(message);
    }

    public JettyInitializationException(String message, String identifier) {
        super(message, identifier);
    }

    public JettyInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JettyInitializationException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
