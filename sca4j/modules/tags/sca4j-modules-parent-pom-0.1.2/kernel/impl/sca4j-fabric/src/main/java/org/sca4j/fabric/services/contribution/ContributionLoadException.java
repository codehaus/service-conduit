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
package org.sca4j.fabric.services.contribution;

import org.sca4j.host.contribution.ContributionException;

/**
 * Denots an error loading a contribution.
 *
 * @version $Rev: 2139 $ $Date: 2007-11-29 18:27:18 +0000 (Thu, 29 Nov 2007) $
 */
public class ContributionLoadException extends ContributionException {
    private static final long serialVersionUID = 4545049826186872284L;

    public ContributionLoadException(Throwable cause) {
        super(cause);
    }

    public ContributionLoadException(String message, String identifier) {
        super(message, identifier);
    }
}
