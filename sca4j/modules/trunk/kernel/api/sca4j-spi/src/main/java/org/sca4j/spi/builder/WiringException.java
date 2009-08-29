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
package org.sca4j.spi.builder;

import java.net.URI;


/**
 * Denotes a general error raised during wiring
 *
 * @version $Rev: 5248 $ $Date: 2008-08-21 01:33:22 +0100 (Thu, 21 Aug 2008) $
 */
public class WiringException extends BuilderException {
    private static final long serialVersionUID = 3668451213570682938L;
    private URI sourceUri;
    private URI targetUri;

    public WiringException(Throwable cause) {
        super(cause);
    }

    public WiringException(String message, Throwable cause) {
        super(message, cause);
    }

    public WiringException(String message) {
        super(message);
    }

    public WiringException(String message, URI sourceUri, URI targetUri) {
        super(message);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    public WiringException(String message, URI sourceUri, URI targetUri, Throwable cause) {
        super(message, cause);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }

    public WiringException(String message, String identifier, URI sourceUri, URI targetUri) {
        super(message, identifier);
        this.sourceUri = sourceUri;
        this.targetUri = targetUri;
    }


    public WiringException(String message, String identifier, Throwable cause) {
        super(message, identifier, cause);
    }

    public WiringException(String message, String identifier) {
        super(message, identifier);
    }

    /**
     * Returns the source name for the wire
     *
     * @return the source name the source name for the wire
     */
    public URI getSourceUri() {
        return sourceUri;
    }

    /**
     * Returns the target name for the wire
     *
     * @return the target name the source name for the wire
     */
    public URI getTargetUri() {
        return targetUri;
    }

}
