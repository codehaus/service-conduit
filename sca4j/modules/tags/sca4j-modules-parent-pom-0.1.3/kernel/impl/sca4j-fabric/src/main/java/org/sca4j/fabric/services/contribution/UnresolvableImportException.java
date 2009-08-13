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
import org.sca4j.spi.services.contribution.Import;

/**
 * @version $Rev: 2081 $ $Date: 2007-11-19 16:18:09 +0000 (Mon, 19 Nov 2007) $
 */
public class UnresolvableImportException extends ContributionException {
    private static final long serialVersionUID = 6415010890788555421L;
    private final Import imprt;
    public UnresolvableImportException(String message, String identifier, Import imprt) {
        super(message, identifier);
        this.imprt = imprt;
    }

    public Import getImport() {
        return imprt;
    }
}
