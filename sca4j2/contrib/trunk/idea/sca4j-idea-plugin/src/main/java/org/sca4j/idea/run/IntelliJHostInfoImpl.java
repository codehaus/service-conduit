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
package org.sca4j.idea.run;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Service;

import org.sca4j.maven.runtime.MavenHostInfo;

/**
 * Default implementation of IntelliJHostInfo.
 *
 * @version $Rev: 2324 $ $Date: 2007-12-24 13:24:43 +0000 (Mon, 24 Dec 2007) $
 */
@Service(interfaces = {IntelliJHostInfo.class, MavenHostInfo.class})
public class IntelliJHostInfoImpl implements IntelliJHostInfo {
    public static final URI LOCAL_URI = URI.create("localhost://intellij");
    private final URI domain;
    private final Properties hostProperties;
    private final Set<URL> dependencyUrls;
    private URL outputDirectory;
    private URL testOutputDirectory;
    private List<String> implementations;
    private List<QName> composites;

    public IntelliJHostInfoImpl(URI domain,
                                Properties hostProperties,
                                Set<URL> dependencyUrls,
                                URL outputDirectory,
                                URL testOutputDirectory,
                                List<String> implementations,
                                List<QName> composites) {
        this.domain = domain;
        this.hostProperties = hostProperties;
        this.dependencyUrls = dependencyUrls;
        this.outputDirectory = outputDirectory;
        this.testOutputDirectory = testOutputDirectory;
        this.implementations = implementations;
        this.composites = composites;
    }

    public URL getBaseURL() {
        return null;
    }

    public boolean isOnline() {
        throw new UnsupportedOperationException();
    }

    public String getProperty(String name, String defaultValue) {
        return hostProperties.getProperty(name, defaultValue);
    }

    public URI getDomain() {
        return domain;
    }

    public URI getRuntimeId() {
        return LOCAL_URI;
    }

    public Set<URL> getDependencyUrls() {
        return dependencyUrls;
    }

    public URL getOutputDirectory() {
        return outputDirectory;
    }

    public URL getTestOutputDirectory() {
        return testOutputDirectory;
    }

    public List<String> getJUnitComponentImplementations() {
        return implementations;
    }

    public List<QName> getIncludedComposites() {
        return composites;
    }
}
