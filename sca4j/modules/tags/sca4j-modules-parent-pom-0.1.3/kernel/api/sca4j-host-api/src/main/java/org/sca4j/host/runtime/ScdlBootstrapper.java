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
package org.sca4j.host.runtime;

import java.net.URL;

import org.xml.sax.InputSource;

/**
 * A bootstrapper subtype that instantiates a runtime from a system composite definition.
 *
 * @version $Rev: 5351 $ $Date: 2008-09-08 21:45:19 +0100 (Mon, 08 Sep 2008) $
 */
public interface ScdlBootstrapper extends Bootstrapper {

    /**
     * Sets the location of the SCDL used to boot this runtime.
     *
     * @param scdlLocation the location of the SCDL used to boot this runtime
     */
    void setScdlLocation(URL scdlLocation);

    /**
     * Sets the system configuration for the host.
     *
     * @param systemConfig System configuration.
     */
    void setSystemConfig(URL systemConfig);

    /**
     * Sets the system configuration for the host.
     *
     * @param document System configuration as an InputSource.
     */
    void setSystemConfig(InputSource document);

}
