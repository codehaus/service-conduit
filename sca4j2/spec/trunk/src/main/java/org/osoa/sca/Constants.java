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
package org.osoa.sca;

/**
 * Values for constants used by this API.
 *
 * @version $Rev: 877 $ $Date: 2007-08-27 18:32:08 +0100 (Mon, 27 Aug 2007) $
 */
public interface Constants {
    /**
     * Namespace for intents.
     */
    String SCA_NS = "http://www.osoa.org/xmlns/sca/1.0";

    /**
     * Prefix form of the namespace that can be prepended to intent declarations.
     */
    String SCA_PREFIX = '{' + SCA_NS + '}';
}
