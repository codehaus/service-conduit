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
import java.net.URL;
import java.net.URLConnection;

/**
 * Connection to an IntelliJ module.
 *
 * @version $Rev: 2290 $ $Date: 2007-12-20 17:33:43 +0000 (Thu, 20 Dec 2007) $
 */
public class IntelliJModuleUrlConnection extends URLConnection {
    public static final String CONTENT_TYPE = "application/vnd.sca4j.intellij-module";

    protected IntelliJModuleUrlConnection(URL url) {
        super(url);
    }

    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getContentType() {
        return CONTENT_TYPE;
    }

    public void connect() throws IOException {

    }
}
