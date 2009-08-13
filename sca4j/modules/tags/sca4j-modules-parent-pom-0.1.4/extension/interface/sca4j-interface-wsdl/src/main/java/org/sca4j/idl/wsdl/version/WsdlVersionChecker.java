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

package org.sca4j.idl.wsdl.version;

import java.net.URL;

/**
 * Interface for checking the WSL version.
 *
 * @version $Revsion$ $Date: 2007-05-30 12:06:55 +0100 (Wed, 30 May 2007) $
 */
public interface WsdlVersionChecker {

    /**
     * WSDL Version.
     */
    public enum WsdlVersion {
        VERSION_1_1, VERSION_2_0;
    }

    /**
     * Gets the version of the WSDL.
     *
     * @param wsdlUrl WSDL URL.
     * @return WSDL Version.
     */
    public WsdlVersion getVersion(URL wsdlUrl);

}
