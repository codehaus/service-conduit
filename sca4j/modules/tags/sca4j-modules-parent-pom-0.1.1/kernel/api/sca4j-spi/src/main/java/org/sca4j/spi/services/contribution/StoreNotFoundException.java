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

import org.sca4j.host.contribution.ContributionException;

/**
 * Thrown when an ArchiveStore or MetaDataStore is not found for a contribution operation
 *
 * @version $Rev: 599 $ $Date: 2007-07-26 21:13:14 +0100 (Thu, 26 Jul 2007) $
 */
public class StoreNotFoundException extends ContributionException {
    public StoreNotFoundException(String message, String identifier) {
        super(message, identifier);
    }
}
