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

package org.sca4j.java.control;

import org.sca4j.spi.generator.GenerationException;

/**
 * Thrown when a injection sire cannot be found for a callback.
 *
 * @version $Rev: 2779 $ $Date: 2008-02-16 03:02:28 -0800 (Sat, 16 Feb 2008) $
 */
public class CallbackSiteNotFound extends GenerationException {
    private static final long serialVersionUID = 6734181652978179903L;

    public CallbackSiteNotFound(String message, String identifier) {
        super(message, identifier);
    }
}
