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
package org.sca4j.binding.burlap.runtime;

import java.net.URI;

import org.sca4j.api.annotation.logging.Info;

/**
 * @version $Rev: 4339 $ $Date: 2008-05-25 16:15:34 +0100 (Sun, 25 May 2008) $
 */
public interface BurlapWireAttacherMonitor {

    /**
     * Callback when a service has been provisioned as a Burlap endpoint
     *
     * @param address the endpoint address
     */
    @Info
    void provisionedEndpoint(URI address);

    /**
     * Callback when a service endpoint has been de-provisioned
     *
     * @param address the endpoint address
     */
    @Info
    void removedEndpoint(URI address);

    /**
     * Callback indicating the extension has been initialized.
     */
    @Info
    void extensionStarted();

    /**
     * Callback indicating the extension has been stopped.
     */
    @Info
    void extensionStopped();


}
