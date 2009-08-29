/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
