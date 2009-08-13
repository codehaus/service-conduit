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
package org.sca4j.fabric.services.classloading;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import junit.framework.TestCase;

import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

/**
 * @version $Rev: 2301 $ $Date: 2007-12-22 10:13:17 +0000 (Sat, 22 Dec 2007) $
 */
public class ClassLoaderRegistryImplTestCase extends TestCase {
    private ClassLoaderRegistry registry = new ClassLoaderRegistryImpl();

    public void testResolveParentUris() throws Exception {
        URL[] urls = new URL[0];
        ClassLoader parent = new URLClassLoader(urls, null);
        ClassLoader loader = new URLClassLoader(urls, parent);
        URI parentId = URI.create("parent");
        registry.register(parentId, parent);
        URI loaderId = URI.create("loader");
        registry.register(loaderId, loader);
        List<URI> parents = registry.resolveParentUris(loader);
        assertEquals(1, parents.size());
        assertEquals(parentId, parents.get(0));
    }
}
