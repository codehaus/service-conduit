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
package org.sca4j.tests.binding.harness;

import java.util.List;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 2428 $ $Date: 2008-01-04 20:08:39 +0000 (Fri, 04 Jan 2008) $
 */
public class EchoTest extends TestCase {

    @Reference protected List<EchoService> service;

    public void testString() {
    	int i = 0;
    	for (EchoService echoService : service) {
    		System.err.println("**************** executing " + (++i));
    		assertEquals("Hello", echoService.echoString("Hello"));
    	}
    }

    public void testInt() {
        assertEquals(123, service.get(0).echoInt(123));
    }

    public void testFault() {
        try {
        	service.get(0).echoFault();
            fail();
        } catch (EchoFault echoFault) {
            // OK
        }
    }
}

