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
package org.sca4j.spi;

import org.sca4j.host.SCA4JRuntimeException;

/**
 * Implementations adhere to runtime lifecycle semantics
 *
 * @version $Rev: 29 $ $Date: 2007-05-15 21:28:09 +0100 (Tue, 15 May 2007) $
 */
public interface Lifecycle {
    /* A configuration error state */
    int CONFIG_ERROR = -1;
    /* Has not been initialized */
    int UNINITIALIZED = 0;
    /* In the process of being configured and initialized */
    int INITIALIZING = 1;
    /* Instantiated and configured */
    int INITIALIZED = 2;
    /* Configured and initialized */
    int RUNNING = 4;
    /* In the process of being shutdown */
    int STOPPING = 5;
    /* Has been shutdown and removed from the composite */
    int STOPPED = 6;
    /* In an error state */
    int ERROR = 7;

    /**
     * Returns the lifecycle state.
     *
     * @return the lifecycle state
     */
    int getLifecycleState();

    /**
     * Starts the Lifecycle.
     *
     * @throws org.sca4j.host.SCA4JRuntimeException if a runtime exception occurs during start
     */
    void start() throws SCA4JRuntimeException;

    /**
     * Stops the Lifecycle.
     *
     * @throws org.sca4j.host.SCA4JRuntimeException if a runtime exception occurs during stop
     */
    void stop() throws SCA4JRuntimeException;
}
