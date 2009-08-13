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
package org.sca4j.fabric.services.contribution.processor;

import org.sca4j.host.contribution.ContributionException;

/**
 * @version $Rev: 2959 $ $Date: 2008-02-29 17:23:07 +0000 (Fri, 29 Feb 2008) $
 */
public class XmlProcessorTypeNotFoundException extends ContributionException {
    private static final long serialVersionUID = -4930429145821670207L;

    public XmlProcessorTypeNotFoundException(String message, String identifier) {
        super(message, identifier);
    }
}
