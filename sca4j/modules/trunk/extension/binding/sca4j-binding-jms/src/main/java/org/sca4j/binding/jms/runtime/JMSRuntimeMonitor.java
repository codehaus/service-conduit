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
package org.sca4j.binding.jms.runtime;

import org.sca4j.api.annotation.logging.Info;

/**
 * Monitor interface for JMS Host.
 * 
 * @version $Rev: 3137 $ $Date: 2008-03-18 02:31:06 +0800 (Tue, 18 Mar 2008) $
 */
public interface JMSRuntimeMonitor {

    /**
     * Callback when a service has been provisioned as a Hessian endpoint
     * 
     * @param address the endpoint address
     */
    @Info
    void registerListener(Object destination);

    /**
     * Callback when an error happens when handle message.
     * 
     * @param address the endpoint address
     */
    @Info
    void jmsListenerError(Exception address);

    /**
     * Callback indicating the extension has been stopped.
     */
    @Info
    void jmsRuntimeStop();

}
