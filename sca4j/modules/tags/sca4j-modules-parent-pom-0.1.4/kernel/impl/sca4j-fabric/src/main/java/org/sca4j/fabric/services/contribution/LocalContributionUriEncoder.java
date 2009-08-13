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

import java.net.URI;

import org.sca4j.spi.services.contribution.ContributionUriEncoder;

/**
 * An no-op encoder used when provisioning contributions to the same VM. Locally provisioned contribution URIs do not need to be encoded as they can
 * be directly resolved against the local contribution store.
 *
 * @version $Rev: 4253 $ $Date: 2008-05-17 18:39:12 +0100 (Sat, 17 May 2008) $
 */
public class LocalContributionUriEncoder implements ContributionUriEncoder {

    public URI encode(URI uri) {
        return uri;
    }
}
