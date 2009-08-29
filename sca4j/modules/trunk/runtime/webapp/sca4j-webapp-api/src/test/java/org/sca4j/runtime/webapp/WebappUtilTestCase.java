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
 * Original Codehaus Header
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
 * Original Apache Header
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.runtime.webapp;

import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;

/**
 * @version $Rev: 1636 $ $Date: 2007-10-30 19:23:13 +0000 (Tue, 30 Oct 2007) $
 */
public class WebappUtilTestCase extends TestCase {
    private ServletContext context;
    private WebappUtilImpl util;
    private ClassLoader cl;
    private URL systemUrl;


    public void testGetInitParameterWhenSpecified() {
        String name = "name";
        String value = "default";
        expect(context.getInitParameter(name)).andReturn(value);
        replay(context);

        assertEquals(value, util.getInitParameter(name, "default"));
        verify(context);
    }

    public void testGetInitParameterUsingDefault() {
        String name = "name";
        String value = "default";
        expect(context.getInitParameter(name)).andReturn(null);
        replay(context);

        assertEquals(value, util.getInitParameter(name, value));
        verify(context);
    }

    public void testGetInitParameterWithZeroLength() {
        String name = "name";
        String value = "default";
        expect(context.getInitParameter(name)).andReturn("");
        replay(context);

        assertEquals(value, util.getInitParameter(name, value));
        verify(context);
    }

    public void testGetScdlFromWebapp() throws MalformedURLException {
        String path = "/WEB-INF/test";
        expect(context.getResource(path)).andReturn(systemUrl);
        replay(context);
        replay(cl);
        assertSame(systemUrl, util.convertToURL(path, cl));
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromWebappMissing() throws MalformedURLException {
        String path = "/WEB-INF/test";
        expect(context.getResource(path)).andReturn(null);
        replay(context);
        expect(cl.getResource(path)).andReturn(null);
        replay(cl);
        assertNull(util.convertToURL(path, cl));
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromWebappMalformed() throws MalformedURLException {
        String path = "/WEB-INF/test";
        expect(context.getResource(path)).andThrow(new MalformedURLException());
        replay(context);
        replay(cl);
        try {
            util.convertToURL(path, cl);
            fail();
        } catch (MalformedURLException e) {
            // OK
        }
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromClasspath() throws MalformedURLException {
        String path = "META-INF/test";
        replay(context);
        expect(cl.getResource(path)).andReturn(systemUrl);
        replay(cl);
        assertSame(systemUrl, util.convertToURL(path, cl));
        verify(context);
        verify(cl);
    }

    public void testGetScdlFromClasspathMissing() throws MalformedURLException {
        String path = "META-INF/test";
        replay(context);
        expect(cl.getResource(path)).andReturn(null);
        replay(cl);
        assertNull(util.convertToURL(path, cl));
        verify(context);
        verify(cl);
    }

    protected void setUp() throws Exception {
        super.setUp();
        context = createMock(ServletContext.class);
        util = new WebappUtilImpl(context);
        cl = createMock(ClassLoader.class);
        systemUrl = new URL("file:/system.scdl");
    }
}
