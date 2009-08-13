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

import org.sca4j.api.annotation.logging.Info;
import org.sca4j.api.annotation.logging.Severe;

/**
 * Event monitor interface for the bootstrap sequence
 *
 * @version $Rev: 4342 $ $Date: 2008-05-25 19:08:33 +0100 (Sun, 25 May 2008) $
 */
public interface CoordinatorMonitor {

    /**
     * Called when the runtime is initialized.
     *
     * @param message a message
     */
    @Info
    void initialized(String message);

    /**
     * Called when the runtime has joined a domain.
     *
     * @param message a message
     */
    @Info
    void joinedDomain(String message);

    /**
     * Called when the runtime has performed recovery.
     *
     * @param message a message
     */
    @Info
    void recovered(String message);

    /**
     * Called when the runtime has started.
     *
     * @param message a message
     */
    @Info
    void started(String message);

    /**
     * Called when an exception was thrown during a boostrap operation
     *
     * @param e the exception
     */
    @Severe
    void error(Throwable e);

    /**
     * Called when errors are encountered processing policy intents
     *
     * @param description a description of the errors
     */
    @Severe
    void intentErrors(String description);


    /**
     * Called when errors are encountered processing extensions
     *
     * @param description a description of the errors
     */
    @Severe
    void extensionErrors(String description);

}
