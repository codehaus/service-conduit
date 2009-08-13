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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.sca4j.host.contribution.ContributionSource;

/**
 * Encapsualtes an IntelliJ module contribution location.
 *
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
public class IntelliJModuleContributionSource implements ContributionSource {
    private URI uri;
    private URL url;
    private long timestamp;
    private byte[] checksum;

    public IntelliJModuleContributionSource(URI uri) throws MalformedURLException {
        this.uri = uri;
        url = new URL("file", "", -1, "", new IntelliJModuleStreamHandler());
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
}
