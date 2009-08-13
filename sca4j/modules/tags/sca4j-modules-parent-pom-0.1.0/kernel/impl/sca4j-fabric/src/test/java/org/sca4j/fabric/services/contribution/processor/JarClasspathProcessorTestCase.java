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
