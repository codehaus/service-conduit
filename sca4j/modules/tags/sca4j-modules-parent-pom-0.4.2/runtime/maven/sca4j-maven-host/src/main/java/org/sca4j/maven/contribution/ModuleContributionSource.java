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
 * Original Codehaus Header
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
 * Original Apache Header
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
