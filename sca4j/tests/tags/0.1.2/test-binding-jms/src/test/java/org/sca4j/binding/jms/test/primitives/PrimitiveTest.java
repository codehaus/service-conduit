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
package org.sca4j.binding.jms.test.primitives;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision$ $Date$
 */
public class PrimitiveTest extends TestCase {

    @Reference
    protected PrimitiveService service;

    public void testInt() {
        assertEquals(1, service.testInt(1));
    }

    public void testIntArray() {
        int[] array = new int[]{1};
        assertEquals(1, service.testArrayInt(array)[0]);
    }

    public void testString() {
        assertEquals("test", service.testString("test"));
    }

}
