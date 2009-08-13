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

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Implementations encode a contribution URI so that it may be dereferenced remotely in a domain, e.g. over HTTP.
 *
 * @version $Rev: 4251 $ $Date: 2008-05-17 17:36:31 +0100 (Sat, 17 May 2008) $
 */
public interface ContributionUriEncoder {

    /**
     * Encode the local contribution URI.
     *
     * @param uri the uri to encode
     * @return the encoded URI which may be dereferenced in the domain
     * @throws URISyntaxException if the URI is invalid
     */
    URI encode(URI uri) throws URISyntaxException;

}
