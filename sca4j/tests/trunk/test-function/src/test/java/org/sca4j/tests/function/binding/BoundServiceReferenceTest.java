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
package org.sca4j.tests.function.binding;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.common.HelloService;

/**
 * Verifies a service and reference explictly bound in respective component definitions (as opposed to through
 * promotion)are handled properly.
 *
 * @version $Rev: 1433 $ $Date: 2007-10-01 23:58:55 +0100 (Mon, 01 Oct 2007) $
 */
public class BoundServiceReferenceTest extends TestCase {
	
    @Reference protected HelloService helloService;
    @Reference protected List<HelloService> listOfReferences;
    @Reference protected Map<String, HelloService> mapOfReferences;

    public void testReferenceIsBound() {
        assertEquals("hello", helloService.send("hello"));
        assertEquals(2, listOfReferences.size());
        for (HelloService helloService : listOfReferences) {
            assertEquals("hello", helloService.send("hello"));
        }
    }

    public void testListOfReferenceIsBound() {
        assertEquals(2, listOfReferences.size());
        for (HelloService helloService : listOfReferences) {
            assertEquals("hello", helloService.send("hello"));
        }
    }

    public void testMapOfReferenceIsBound() {
        assertEquals(2, mapOfReferences.size());
        assertEquals("hello", mapOfReferences.get("ONE").send("hello"));
        assertEquals("hello", mapOfReferences.get("TWO").send("hello"));
    }
}

