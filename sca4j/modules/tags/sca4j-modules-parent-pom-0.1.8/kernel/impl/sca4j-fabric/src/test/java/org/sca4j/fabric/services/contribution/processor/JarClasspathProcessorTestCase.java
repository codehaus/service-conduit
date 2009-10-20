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
package org.sca4j.fabric.services.contribution.processor;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;

/**
 * @version $Rev: 2450 $ $Date: 2008-01-10 22:09:41 +0000 (Thu, 10 Jan 2008) $
 */
public class JarClasspathProcessorTestCase extends TestCase {
    private JarClasspathProcessor processor;

    /**
     * Verifies processing when no jars are present in META-INF/lib
     *
     * @throws Exception
     */
    public void testExpansionNoLibraries() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL location = cl.getResource("./repository/1/test.jar");
        List<URL> urls = processor.process(location);
        assertEquals(1, urls.size());
        assertEquals(location, urls.get(0));
    }

    /**
     * Verifies jars in META-INF/lib are added to the classpath
     *
     * @throws Exception
     */
    public void testExpansionWithLibraries() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL location = cl.getResource("./repository/2/testWithLibraries.jar");
        List<URL> urls = processor.process(location);
        assertEquals(2, urls.size());
        assertEquals(location, urls.get(0));
    }

    protected void setUp() throws Exception {
        super.setUp();
        ClasspathProcessorRegistry registry = EasyMock.createNiceMock(ClasspathProcessorRegistry.class);
        HostInfo info = EasyMock.createNiceMock(HostInfo.class);
        EasyMock.expect(info.getTempDir()).andReturn(new File(System.getProperty("java.io.tmpdir"), ".sca4j"));
        EasyMock.replay(info);
        processor = new JarClasspathProcessor(registry, info);
    }
}
