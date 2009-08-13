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
package org.sca4j.tests.function.monitor;

import junit.framework.TestCase;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.api.annotation.logging.Info;

/**
 * @version $Rev: 4340 $ $Date: 2008-05-25 16:15:47 +0100 (Sun, 25 May 2008) $
 */
public class MonitorTest extends TestCase {

    @Monitor
    protected MyMonitor monitor;

    public void testMonitor() {
        assertNotNull(monitor);
        monitor.wasCalled();
    }

    public static interface MyMonitor {
        @Info
        void wasCalled();
    }
}
