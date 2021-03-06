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
package org.sca4j.maven.contribution;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.maven.runtime.MavenHostInfo;
import org.sca4j.spi.services.contribution.ClasspathProcessor;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;

/**
 * Fabricates a classpath for a Maven module by including the classes and test-classes directories and any module dependencies.
 *
 * @version $Rev: 4244 $ $Date: 2008-05-17 17:23:07 +0100 (Sat, 17 May 2008) $
 */
@EagerInit
public class ModuleClasspathProcessor implements ClasspathProcessor {
    public static final String CONTENT_TYPE = "application/vnd.sca4j.maven-project";
    private ClasspathProcessorRegistry registry;
    private MavenHostInfo hostInfo;

    public ModuleClasspathProcessor(@Reference ClasspathProcessorRegistry registry, @Reference MavenHostInfo hostInfo) {
        this.registry = registry;
        this.hostInfo = hostInfo;
    }

    @Init
    public void init() {
        registry.register(this);
    }

    public boolean canProcess(URL url) {
        if ("file".equals(url.getProtocol())) {
            // assume exploded directories are maven modules
            return true;
        }
        try {
            URLConnection conn = url.openConnection();
            return CONTENT_TYPE.equals(conn.getContentType());
        } catch (IOException e) {
            return false;
        }
    }

    public List<URL> process(URL url) throws IOException {
        String file = url.getFile();
        List<URL> urls = new ArrayList<URL>(2);
        urls.add(new File(file, "classes").toURI().toURL());
        urls.add(new File(file, "test-classes").toURI().toURL());
        urls.addAll(hostInfo.getDependencyUrls());
        return urls;
    }
}
