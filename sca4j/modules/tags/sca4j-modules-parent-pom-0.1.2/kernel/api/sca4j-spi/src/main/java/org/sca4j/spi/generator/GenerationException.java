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
package org.sca4j.spi.generator;

import org.sca4j.host.SCA4JException;

/**
 * Base generation exception
 *
 * @version $Rev: 4240 $ $Date: 2008-05-17 09:18:54 +0100 (Sat, 17 May 2008) $
 */
public class GenerationException extends SCA4JException {
    private static final long serialVersionUID = -1307164947930260525L;

    public GenerationException() {
    }

    public GenerationException(String message) {
        super(message);
    }

    public GenerationException(String message, String identifier) {
        super(message, identifier);
    }

    public GenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerationException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public GenerationException(Throwable cause) {
        super(cause);
    }
}
