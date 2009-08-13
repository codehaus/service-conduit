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
package org.sca4j.fabric.services.contenttype;

import java.net.URL;

import junit.framework.TestCase;

import org.sca4j.spi.services.contenttype.ContentTypeResolver;

/**
 * @version $Revision: 608 $ $Date: 2007-07-27 04:21:39 +0100 (Fri, 27 Jul 2007) $
 */
public class ExtensiionMapContentTypeResolverTestCase extends TestCase {
    private ContentTypeResolver resolver;

    public void testKnownContentType() throws Exception {
        URL url = getClass().getResource("test.txt");

        assertEquals("text/plain", resolver.getContentType(url));
    }

    public void testGetContentType() throws Exception {
        URL url = getClass().getResource("test.composite");

        assertEquals("text/vnd.sca4j.composite+xml", resolver.getContentType(url));
    }

    protected void setUp() throws Exception {
        super.setUp();
        resolver = new ExtensionMapContentTypeResolver();
    }
}
