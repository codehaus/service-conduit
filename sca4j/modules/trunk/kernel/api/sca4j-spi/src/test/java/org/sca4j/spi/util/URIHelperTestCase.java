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
package org.sca4j.spi.util;

import java.net.URI;

import junit.framework.TestCase;

/**
 * @version $Rev: 5274 $ $Date: 2008-08-26 05:14:56 +0100 (Tue, 26 Aug 2008) $
 */
public class URIHelperTestCase extends TestCase {

    public void testBaseName() throws Exception {
        URI uri = new URI("foo");
        assertEquals("foo", UriHelper.getBaseName(uri));
    }

    public void testBaseNameScheme() throws Exception {
        URI uri = new URI("sca://foo");
        assertEquals("foo", UriHelper.getBaseName(uri));
    }

    public void testBaseNameSchemePath() throws Exception {
        URI uri = new URI("sca://bar/foo");
        assertEquals("foo", UriHelper.getBaseName(uri));
    }

    public void testBaseNamePath() throws Exception {
        URI uri = new URI("bar/foo");
        assertEquals("foo", UriHelper.getBaseName(uri));
    }

    public void testBaseNameFragment() throws Exception {
        URI uri = new URI("#foo");
        assertEquals("#foo", UriHelper.getBaseName(uri));
    }

    public void testParentName() throws Exception {
        URI uri = new URI("sca4j://grandparent/parent/child");
        assertEquals("sca4j://grandparent/parent", UriHelper.getParentName(uri));
    }

    public void testDefragmentedNameScheme() throws Exception {
        URI uri = new URI("sca://foo/bar#bar");
        assertEquals("sca://foo/bar", UriHelper.getDefragmentedName(uri).toString());
    }

    public void testDefragmentedName() throws Exception {
        URI uri = new URI("foo/bar#bar");
        assertEquals("foo/bar", UriHelper.getDefragmentedName(uri).toString());
    }

    public void testDefragmentedNoName() throws Exception {
        URI uri = new URI("#bar");
        assertEquals("", UriHelper.getDefragmentedName(uri).toString());
    }

}
