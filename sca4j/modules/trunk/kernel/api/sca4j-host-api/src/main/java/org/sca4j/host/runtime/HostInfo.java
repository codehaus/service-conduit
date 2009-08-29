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
package org.sca4j.host.runtime;

import java.io.File;
import java.net.URI;

/**
 * Interface that provides information on the host environment. This allows the runtime to access information about the environment in which it is
 * running. The implementation of this interface is provided to the runtime by the host during initialization. Hosts will generally extend this
 * interface to provide additional information.
 *
 * @version $Rev: 5139 $ $Date: 2008-08-02 17:18:18 +0100 (Sat, 02 Aug 2008) $
 */
public interface HostInfo {
    
    String HOST_CONFIG_DIR = "SCA4J_CONFIG_DIR";

    /**
     * Returns the SCA domain associated with this runtime. A null domain indicates that this is a standalone runtime with a self-contained domain.
     *
     * @return the SCA domain associated with this runtime; may be null
     */
    URI getDomain();

    /**
     * Gets the base directory for the runtime.
     *
     * @return The base directory for the runtime.
     */
    File getBaseDir();

    /**
     * Return the value of the named property.
     *
     * @param name         the name of the property
     * @param defaultValue default value to return if the property is not defined
     * @return the value of the named property
     */
    String getProperty(String name, String defaultValue);

    /**
     * True if the host environment supports classloader isolation.
     *
     * @return true if the host environment supports classloader isolation
     */
    boolean supportsClassLoaderIsolation();

    /**
     * Returns the temporary directory.
     *
     * @return the temporary directory.
     */
    File getTempDir();
}
