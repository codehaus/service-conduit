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

package tests.groovy;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev: 222 $ $Date: 2007-06-14 03:07:28 +0100 (Thu, 14 Jun 2007) $
 */
public class TestClient extends TestCase {
    public @Reference EchoService service;

    public TestClient() {
        System.out.println("Hello");
    }

    public void testEcho() {
        assertEquals("Hello World", service.hello("World"));
    }
}
