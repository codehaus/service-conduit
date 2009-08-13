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
