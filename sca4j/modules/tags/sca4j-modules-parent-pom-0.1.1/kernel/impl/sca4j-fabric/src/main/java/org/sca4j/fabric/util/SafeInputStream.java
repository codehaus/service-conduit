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
package org.sca4j.fabric.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 */

/**
 * This class is a workaround for URL stream issue as illustrated below. InputStream is=url.getInputStream();
 * is.close(); // This line doesn't close the JAR file if the URL is a jar entry like "jar:file:/a.jar!/my.composite" We
 * also need to turn off the JarFile cache.
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 * @link http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4950148
 */
public class SafeInputStream extends InputStream {
    private JarFile jarFile;
    private InputStream is;

    public SafeInputStream(URL url) throws IOException {
        String protocol = url.getProtocol();
        if (protocol != null && (protocol.equals("jar"))) {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            // We cannot use cache
            connection.setUseCaches(false);
            try {
                is = connection.getInputStream();
            } catch (IOException e) {
                throw e;
            }
            jarFile = connection.getJarFile();
        } else {
            is = url.openStream();
        }
    }

    public SafeInputStream(JarURLConnection connection) throws IOException {
        // We cannot use cache
        connection.setUseCaches(false);
        is = connection.getInputStream();
        jarFile = connection.getJarFile();
    }

    public int available() throws IOException {
        return is.available();
    }

    public void close() throws IOException {
        is.close();
        // We need to close the JAR file
        if (jarFile != null) {
            jarFile.close();
        }
    }

    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
    }

    public boolean markSupported() {
        return is.markSupported();
    }

    public int read() throws IOException {
        return is.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        return is.read(b);
    }

    public synchronized void reset() throws IOException {
        is.reset();
    }

    public long skip(long n) throws IOException {
        return is.skip(n);
    }
}
