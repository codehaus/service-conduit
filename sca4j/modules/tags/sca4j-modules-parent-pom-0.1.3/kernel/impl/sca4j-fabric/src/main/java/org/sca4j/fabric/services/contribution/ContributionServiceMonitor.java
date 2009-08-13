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
package org.sca4j.fabric.services.contribution;

import org.sca4j.api.annotation.logging.Severe;
import org.sca4j.api.annotation.logging.Info;

/**
 * Receives callback events from the ContributionService
 *
 * @version $Rev: 4355 $ $Date: 2008-05-26 04:05:51 +0100 (Mon, 26 May 2008) $
 */
public interface ContributionServiceMonitor {

    @Severe
    void error(String message, Throwable e);

    @Info
    void contributionWarnings(String message);

}
