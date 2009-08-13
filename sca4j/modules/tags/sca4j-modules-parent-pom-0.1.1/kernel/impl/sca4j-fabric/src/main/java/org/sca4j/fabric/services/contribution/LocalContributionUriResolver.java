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
import java.net.URL;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionUriResolver;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.ResolutionException;

/**
 * Resolves contribution URIs locally (i.e. in the same runtime VM).
 *
 * @version $Rev: 4259 $ $Date: 2008-05-17 20:34:24 +0100 (Sat, 17 May 2008) $
 */
@EagerInit
public class LocalContributionUriResolver implements ContributionUriResolver {
    private MetaDataStore store;

    public LocalContributionUriResolver(@Reference MetaDataStore store) {
        this.store = store;
    }

    public URL resolve(URI uri) throws ResolutionException {
        Contribution contribution = store.find(uri);
        if (contribution == null) {
            String id = uri.toString();
            throw new ResolutionException("Contribution not found: " + id, id);
        }
        return contribution.getLocation();
    }
}
