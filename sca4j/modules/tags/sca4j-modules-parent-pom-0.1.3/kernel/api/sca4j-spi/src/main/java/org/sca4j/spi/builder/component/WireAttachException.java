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
package org.sca4j.spi.builder.component;

import java.net.URI;

import org.sca4j.spi.builder.WiringException;

/**
 * Thrown when an error is encountered attaching a wire
 *
 * @version $Rev: 5248 $ $Date: 2008-08-21 01:33:22 +0100 (Thu, 21 Aug 2008) $
 */
public class WireAttachException extends WiringException {
    private static final long serialVersionUID = 4976504310808006829L;

    public WireAttachException(String message, URI sourceUri, URI targetUri, Throwable cause) {
        super(message, sourceUri, targetUri, cause);
    }
}
