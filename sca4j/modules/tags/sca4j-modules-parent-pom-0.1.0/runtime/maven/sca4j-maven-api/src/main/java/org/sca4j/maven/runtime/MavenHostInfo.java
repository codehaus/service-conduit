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
package org.sca4j.maven.runtime;

import java.net.URL;
import java.util.Set;

import org.sca4j.host.runtime.HostInfo;

/**
 * @version $Rev: 2248 $ $Date: 2007-12-12 03:22:04 +0000 (Wed, 12 Dec 2007) $
 */
public interface MavenHostInfo extends HostInfo {

    /**
     * Returns dereferenceable URLs to dependencies of the current Maven module.
     *
     * @return the dependency URLs
     */
    Set<URL> getDependencyUrls();

}
