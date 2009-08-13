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
package org.sca4j.rs.runtime;

import java.net.URI;

import org.sca4j.api.annotation.logging.Info;

/**
 * @version $Rev: 5452 $ $Date: 2008-09-20 11:40:07 +0100 (Sat, 20 Sep 2008) $
 */
public interface RsBindingWireAttacherMonitor {

    /**
     * Callback when a service has been provisioned as a REST endpoint
     *
     * @param address the endpoint address
     */
    @Info
    void provisionedEndpoint(String className, String type, URI address);

    /**
     * Callback when a service endpoint has been de-provisioned
     *
     * @param address the endpoint address
     */
    @Info
    void removedEndpoint(String className, String type, URI address);

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
