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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.sca4j.host.contribution.ContributionSource;

/**
 * A representation of a Maven module contribution.
 *
 * @version $Rev: 5133 $ $Date: 2008-08-02 06:00:17 +0100 (Sat, 02 Aug 2008) $
 */
public class ModuleContributionSource implements ContributionSource {

    public static final String CONTENT_TYPE = "application/vnd.sca4j.maven-project";
    private URI uri;
    private URL url;
    private long timestamp;
    private byte[] checksum;

    public ModuleContributionSource(URI uri, String base) throws MalformedURLException {
        this.uri = uri;
        url = new URL("file", "", -1, base);
        checksum = new byte[0];
        timestamp = System.currentTimeMillis();
    }

    public boolean persist() {
        return false;
    }

    public URI getUri() {
        return uri;
    }

    public InputStream getSource() throws IOException {
        return url.openStream();
    }

    public URL getLocation() {
        return url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }
}
