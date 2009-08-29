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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
