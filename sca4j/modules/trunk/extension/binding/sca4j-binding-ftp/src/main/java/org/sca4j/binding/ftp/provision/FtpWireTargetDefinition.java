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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.binding.ftp.provision;

import java.net.URI;

import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;

/**
 * @version $Revision$ $Date$
 */
public class FtpWireTargetDefinition extends PhysicalWireTargetDefinition {
    private static final long serialVersionUID = -494501322715192283L;
    private final URI classLoaderId;
    private final boolean active;
    private final FtpSecurity security;
    private String tmpFileSuffix;

    /**
     * Initializes the classloader id, transfer mode, and timeout.
     *
     * @param classLoaderId  the classloader id to deserialize parameters in
     * @param active         FTP transfer mode
     * @param security       Security parameters
     * @param connectTimeout the timeout to use for opening socket connections
     * @param socketTimeout  the timeout to use for blocking connection operations
     */
    public FtpWireTargetDefinition(URI classLoaderId, boolean active, FtpSecurity security) {
        this.classLoaderId = classLoaderId;
        this.active = active;
        this.security = security;
    }

    /**
     * Returns the classloader id to deserialize parameters in.
     *
     * @return the classloader id to deserialize parameters in
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * Gets the FTP transfer mode.
     *
     * @return True if user wants active transfer mode.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Get the security parameters.
     *
     * @return Get the security parameters.
     */
    public FtpSecurity getSecurity() {
        return security;
    }

    /**
     * Gets the temporary file suffix to be used while file in transmission (i.e. during STOR operation).
     * 
     * @return temporary file suffix
     */
    public String getTmpFileSuffix() {
        return tmpFileSuffix;
    }

    /**
     * Sets the temporary file suffix to be used while file in transmission (i.e. during STOR operation).
     * 
     * @param tmpFileSuffix temporary file suffix
     */
    public void setTmpFileSuffix(String tmpFileSuffix) {
        this.tmpFileSuffix = tmpFileSuffix;
    }
    
}
