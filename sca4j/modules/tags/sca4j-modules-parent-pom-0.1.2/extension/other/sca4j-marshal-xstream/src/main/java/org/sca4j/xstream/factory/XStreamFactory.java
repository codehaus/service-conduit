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
package org.sca4j.xstream.factory;

import com.thoughtworks.xstream.XStream;

/**
 * Implementations create <code>XStream</code> instances for serializing internal runtime data structures.
 *
 * @version $Rev: 2401 $ $Date: 2007-12-29 13:46:39 +0000 (Sat, 29 Dec 2007) $
 */
public interface XStreamFactory {

    /**
     * Returns a new XStream instance.
     *
     * @return a new XStream instance
     */
    XStream createInstance();

}
