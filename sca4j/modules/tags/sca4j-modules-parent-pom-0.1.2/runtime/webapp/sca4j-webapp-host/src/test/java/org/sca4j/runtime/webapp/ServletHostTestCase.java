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
package org.sca4j.runtime.webapp;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.verify;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev: 3499 $ $Date: 2008-03-31 01:16:09 +0100 (Mon, 31 Mar 2008) $
 */
public class ServletHostTestCase extends TestCase {

    public void testDispatch() throws Exception {
        HttpServletRequest req = createMock(HttpServletRequest.class);
        expect(req.getPathInfo()).andReturn("foo");
        replay(req);
        HttpServletResponse res = createMock(HttpServletResponse.class);
        Servlet servlet = createMock(Servlet.class);
        servlet.service(req, res);
        EasyMock.expectLastCall();
        replay(servlet);
        ServletHostImpl host = new ServletHostImpl(null);
        host.registerMapping("foo", servlet);
        host.service(req, res);
        verify(servlet);
    }

    public void testDuplicateRegistration() throws Exception {
        Servlet servlet = createMock(Servlet.class);
        ServletHostImpl host = new ServletHostImpl(null);
        host.registerMapping("foo", servlet);
        assertEquals(true, host.isMappingRegistered("foo"));
        assertEquals(false, host.isMappingRegistered("bar"));
        try {
            host.registerMapping("foo", servlet);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testUnregister() throws Exception {
        HttpServletRequest req = createMock(HttpServletRequest.class);
        expect(req.getPathInfo()).andReturn("foo");
        replay(req);
        HttpServletResponse res = createMock(HttpServletResponse.class);
        Servlet servlet = createMock(Servlet.class);
        replay(servlet);
        ServletHostImpl host = new ServletHostImpl(null);
        host.registerMapping("foo", servlet);
        Servlet unregedServlet = host.unregisterMapping("foo");
        assertEquals(unregedServlet, servlet);
        try {
            host.service(req, res);
        } catch (IllegalStateException e) {
            // expected
        }
        verify(servlet);
    }

}
