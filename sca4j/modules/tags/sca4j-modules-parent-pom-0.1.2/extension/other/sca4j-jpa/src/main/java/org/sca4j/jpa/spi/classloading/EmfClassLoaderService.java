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
package org.sca4j.jpa.spi.classloading;

import java.net.URI;

/**
 * Implements a strategy for creating or returning a classloader to set for entity manager factory instantiation for a given application classloader.
 *
 * @version $Revision$ $Date$
 */
public interface EmfClassLoaderService {

    /**
     * Returns or creates a classloader an entity manager factory can be instantiated in for an application
     *
     * @param classLoaderUri uri of the application classloader that will use the EMF
     * @return the classloader
     */
    ClassLoader getEmfClassLoader(URI classLoaderUri);
}
