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
package org.sca4j.idea.contribution;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.idea.run.IntelliJHostInfo;
import org.sca4j.maven.runtime.MavenHostInfo;
import org.sca4j.spi.services.contribution.ClasspathProcessor;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;

/**
 * Calculates the classpath for an IntelliJ module
 *
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
@EagerInit
public class IntelliJModuleClasspathProcessor implements ClasspathProcessor {
    public static final String CONTENT_TYPE = "application/vnd.sca4j.intellij-module";
    private ClasspathProcessorRegistry registry;
    private IntelliJHostInfo hostInfo;

    public IntelliJModuleClasspathProcessor(@Reference ClasspathProcessorRegistry registry,
                                            @Reference MavenHostInfo hostInfo) {
        this.registry = registry;
        // FIXME need to cast - introspect host info type in AbstractRuntime
        this.hostInfo = (IntelliJHostInfo) hostInfo;
    }

    @Init
    public void init() {
        registry.register(this);
    }

    public boolean canProcess(URL url) {
        try {
            URLConnection conn = url.openConnection();
            return CONTENT_TYPE.equals(conn.getContentType());
        } catch (IOException e) {
            return false;
        }
    }

    public List<URL> process(URL url) throws IOException {
        List<URL> urls = new ArrayList<URL>(2);
        urls.add(hostInfo.getOutputDirectory());
        urls.add(hostInfo.getTestOutputDirectory());
        return urls;
    }


}
