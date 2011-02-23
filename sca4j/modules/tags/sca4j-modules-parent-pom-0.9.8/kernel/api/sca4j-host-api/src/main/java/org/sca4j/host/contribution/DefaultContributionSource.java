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
import java.net.URL;

/**
 * A contribution artifact that is on a filesystem
 *
 * @version $Rev: 4238 $ $Date: 2008-05-17 06:02:45 +0100 (Sat, 17 May 2008) $
 */
public class DefaultContributionSource implements ContributionSource {
    
    private URL location;
    private long timestamp;
    private String type;

    /**
     * Initialises the contribution source state.
     * 
     * @param location The location of the contribution.
     * @param timestamp Contribution timestamp.
     * @param type Contribution type.
     */
    public DefaultContributionSource(URL location, long timestamp, String type) {
        this.location = location;
        this.timestamp = timestamp;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     * @see org.sca4j.host.contribution.ContributionSource#getSource()
     */
    public InputStream getSource() throws IOException {
        return location.openStream();
    }

    /**
     * {@inheritDoc}
     * @see org.sca4j.host.contribution.ContributionSource#getLocation()
     */
    public URL getLocation() {
        return location;
    }

    /**
     * {@inheritDoc}
     * @see org.sca4j.host.contribution.ContributionSource#getTimestamp()
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * {@inheritDoc}
     * @see org.sca4j.host.contribution.ContributionSource#getType()
     */
    public String getType() {
        return type;
    }
}


