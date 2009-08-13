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
package org.sca4j.spi.builder;

import java.net.URI;


/**
 * Denotes a general error raised during wiring
 *
 * @version $Rev: 5248 $ $Date: 2008-08-21 01:33:22 +0100 (Thu, 21 Aug 2008) $
 */
public class WiringException extends BuilderException {
    private static final long serialVersionUID = 3668451213570682938L;
    private URI sourceUri;
    private URI targetUri;

    public WiringException(Throwable cause) {
        super(cause);
    }

    public WiringException(String message, Throwable cause) {
        super(message, cause);
    }

    public WiringException(String message) {
        super(message);
    }

    public WiringException(String message, URI sourceUri, URI targetUri) {
        super(message);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    public WiringException(String message, URI sourceUri, URI targetUri, Throwable cause) {
        super(message, cause);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    public WiringException(String message, String identifier, URI sourceUri, URI targetUri) {
        super(message, identifier);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }


    public WiringException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public WiringException(String message, String identifier) {
        super(message, identifier);
    }

    /**
     * Returns the source name for the wire
     *
     * @return the source name the source name for the wire
     */
    public URI getSourceUri() {
        return sourceUri;
    }

    /**
     * Returns the target name for the wire
     *
     * @return the target name the source name for the wire
     */
    public URI getTargetUri() {
        return targetUri;
    }

}
