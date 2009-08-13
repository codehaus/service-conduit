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
package org.sca4j.introspection.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev: 4262 $ $Date: 2008-05-18 05:41:33 +0100 (Sun, 18 May 2008) $
 */
public class LoaderUtilTestCase extends TestCase {
    private NamespaceContext context;
    private String uri;

    public void testQNameWithNoPrefix() {
        assertEquals(new QName(uri, "foo"), LoaderUtil.getQName("foo", uri, null));
    }

    public void testPrefixResolve() {
        EasyMock.expect(context.getNamespaceURI("prefix")).andReturn(uri);
        EasyMock.replay(context);
        QName name = LoaderUtil.getQName("prefix:foo", null, context);
        assertEquals(uri, name.getNamespaceURI());
        assertEquals("prefix", name.getPrefix());
        assertEquals("foo", name.getLocalPart());
        EasyMock.verify(context);
    }


    protected void setUp() throws Exception {
        super.setUp();
        uri = "http://example.com";
        context = EasyMock.createMock(NamespaceContext.class);
    }
}
