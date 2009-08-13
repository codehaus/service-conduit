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
package org.sca4j.binding.aq.runtime.monitor;


import org.sca4j.api.annotation.logging.Info;

/**
 * Monitor interface for AQ
 * @version $Rev: 3137 $ $Date: 2008-03-18 02:31:06 +0800 (Tue, 18 Mar 2008) $
 */
public interface AQMonitor {
           
    
    /**
     * Message when in Source Wire
     */
    @Info
    void onSourceWire(String message);
    
    /**
     * Message when in Target
     */
    @Info
    void onTargetWire(String message);
    
    /**
     * Message in AQ Host
     */
    @Info
    void onAQHost(String message);
    
    
    /**
     * Log message to stop when consumers are stopped
     */
    @Info
    void stopConsumer(String message);
    
    /**
     * Logs messages for stop on COnnection
     */
    @Info
    void stopConnection(String message);
    
    /**
     * Log message to stop on AQ
     */
    @Info
    void stopOnAQHost(String message);        
    
    /**
     * Logs the Exception
     * @param exception
     */
    @Info
    void onException(Throwable exception);
}
