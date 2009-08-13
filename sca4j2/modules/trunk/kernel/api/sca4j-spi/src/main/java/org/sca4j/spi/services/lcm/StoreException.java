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
package org.sca4j.spi.services.lcm;

import org.sca4j.host.SCA4JException;


/**
 * Denotes an exception recording an logical component operation
 *
 * @version $Rev: 4805 $ $Date: 2008-06-09 13:35:29 +0100 (Mon, 09 Jun 2008) $
 */
public class StoreException extends SCA4JException {
    private static final long serialVersionUID = 652954682135057498L;

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }
}
