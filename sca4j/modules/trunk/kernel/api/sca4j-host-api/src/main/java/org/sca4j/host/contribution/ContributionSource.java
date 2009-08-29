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
package org.sca4j.host.contribution;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Represents a source artifact that will be contributed to a domain or an updated version of an existing contribution.
 *
 * @version $Rev: 3536 $ $Date: 2008-04-01 23:03:05 +0100 (Tue, 01 Apr 2008) $
 */
public interface ContributionSource {

    /**
     * Returns the identifier for this contribution or null if one has not been assigned (i.e. it is a new contribution
     * and not an update).
     *
     * @return the identifier for this contribution
     */
    URI getUri();

    /**
     * Returns a input stream for the source.
     *
     * @return a input stream for the source
     * @throws IOException if an error occurs returning the stream
     */
    InputStream getSource() throws IOException;

    /**
     * If the source is local, returns the source URL
     *
     * @return the source URL
     */
    URL getLocation();

    /**
     * Returns the source timestamp.
     *
     * @return the source timestamp
     */
    long getTimestamp();

    /**
     * Returns the source checksum.
     *
     * @return the source checksum
     */
    byte[] getChecksum();


  /**
   * Returns the content type of the source 
   * @return
   */
    String getContentType();
}
