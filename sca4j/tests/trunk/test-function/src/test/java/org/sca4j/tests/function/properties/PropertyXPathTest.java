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
package org.sca4j.tests.function.properties;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Property;

/**
 * @version $Rev: 1712 $ $Date: 2007-11-04 09:35:10 +0000 (Sun, 04 Nov 2007) $
 */
public class PropertyXPathTest extends TestCase {
    @Property
    public String simple;
    @Property
    public String complex;
    @Property
    public String complexWithNamespace;

    public void testSimpleXPath() {
        assertEquals("Hello World", simple);
    }

    public void testComplexXPathString() {
        assertEquals("Hello World", complex);
    }

/*
    // testcase for FABRICTHREE-79
    public void testComplexXPathWithNamespace() {
        assertEquals("Hello Foo", complexWithNamespace);
    }
*/
}
