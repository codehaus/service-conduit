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
package org.sca4j.host.contribution;

import org.sca4j.host.SCA4JException;

/**
 * Base exception class for contribution operations
 *
 * @version $Rev: 4336 $ $Date: 2008-05-25 10:06:15 +0100 (Sun, 25 May 2008) $
 */
public class ContributionException extends SCA4JException {
    private static final long serialVersionUID = -719954756993485191L;

    public ContributionException(String message) {
        super(message);
    }

    public ContributionException(Throwable cause) {
        super(cause);
    }

    public ContributionException(String message, String identifier) {
        super(message, identifier);
    }

    public ContributionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContributionException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

}
