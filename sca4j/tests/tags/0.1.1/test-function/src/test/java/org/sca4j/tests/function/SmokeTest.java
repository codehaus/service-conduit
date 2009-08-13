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
package org.sca4j.tests.function;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.common.IdentityService;

/**
 * Simple test case that checks that the test harness is running.
 *
 * @version $Rev: 934 $ $Date: 2007-08-31 16:35:29 +0100 (Fri, 31 Aug 2007) $
 */
public class SmokeTest extends TestCase {
    @Reference
    public IdentityService service;

    public void testCallingComponent() {
        assertEquals("one", service.getIdentity());
    }
}
