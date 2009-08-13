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
package org.sca4j.spi.services.contribution;

import java.net.URL;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.spi.services.contribution.Contribution;

/**
 * Used to perform a callback operation when iterating contained artifacts in a contribution.
 *
 * @version $Rev: 3578 $ $Date: 2008-04-08 06:59:20 +0100 (Tue, 08 Apr 2008) $
 */
public interface Action {
    /**
     * Called when an artifact is reached during iteration.
     *
     * @param contribution the contribution being traversed
     * @param contentType  the artifact MIME type to process
     * @param url          the artifact url
     * @throws ContributionException if an error occurs processing the artifact
     */
    void process(Contribution contribution, String contentType, URL url) throws ContributionException;
}
