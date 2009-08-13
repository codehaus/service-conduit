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
package org.sca4j.spi.classloader;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class FilteringMultiparentClassLoaderTestCase extends TestCase {
    private static final URI NAME = URI.create("test");

    public void testAllowPackage() throws Exception {
        Set<String> filters = new HashSet<String>();
        filters.add(this.getClass().getPackage().getName() + ".*");
        FilteringMultiparentClassLoader cl = new FilteringMultiparentClassLoader(NAME, getClass().getClassLoader(), filters);
        assertNotNull(cl.loadClass(this.getClass().getName()));
    }

    public void testAllowWildcardPackage() throws Exception {
        Set<String> filters = new HashSet<String>();
        filters.add("org.sca4j.*");
        FilteringMultiparentClassLoader cl = new FilteringMultiparentClassLoader(NAME, getClass().getClassLoader(), filters);
        assertNotNull(cl.loadClass(this.getClass().getName()));
    }

    public void testDisAllowParentPackage() throws Exception {
        Set<String> filters = new HashSet<String>();
        filters.add("org.sca4j.jpa.someother.*");
        FilteringMultiparentClassLoader cl = new FilteringMultiparentClassLoader(NAME, getClass().getClassLoader(), filters);
        try {
            cl.loadClass(this.getClass().getName());
            fail();
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    public void testNoneAllowed() throws Exception {
        Set<String> filters = Collections.emptySet();
        FilteringMultiparentClassLoader cl = new FilteringMultiparentClassLoader(NAME, getClass().getClassLoader(), filters);
        try {
            cl.loadClass(this.getClass().getName());
            fail();
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

    public void testFilterNoPackage() throws Exception {
        Set<String> set = Collections.emptySet();
        FilteringMultiparentClassLoader cl = new FilteringMultiparentClassLoader(NAME, getClass().getClassLoader(), set);
        try {
            cl.loadClass("Foo");
            fail();
        } catch (ClassNotFoundException e) {
            // expected
        }
    }

}
