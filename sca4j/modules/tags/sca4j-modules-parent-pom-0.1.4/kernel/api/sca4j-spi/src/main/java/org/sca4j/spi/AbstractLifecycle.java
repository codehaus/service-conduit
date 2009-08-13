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

/**
 * Base class providing a simple implementation of Lifecycle.
 *
 * @version $Rev: 212 $ $Date: 2007-06-09 19:04:21 +0100 (Sat, 09 Jun 2007) $
 */
public abstract class AbstractLifecycle implements Lifecycle {
    protected volatile int lifecycleState = UNINITIALIZED;

    public int getLifecycleState() {
        return lifecycleState;
    }

    /**
     * Set the current state of the Lifecycle.
     *
     * @param lifecycleState the new state
     */
    protected void setLifecycleState(int lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public void start() {
        setLifecycleState(RUNNING);
    }

    public void stop() {
        setLifecycleState(STOPPED);
    }

    /**
     * Returns the current lifecycle as a String (for example, "RUNNING").
     *
     * @return the current lifecycle as a String
     */
    public String toString() {
        switch (lifecycleState) {
            case Lifecycle.CONFIG_ERROR:
                return "CONFIG_ERROR";
            case Lifecycle.ERROR:
                return "ERROR";
            case Lifecycle.INITIALIZING:
                return "INITIALIZING";
            case Lifecycle.INITIALIZED:
                return "INITIALIZED";
            case Lifecycle.RUNNING:
                return "RUNNING";
            case Lifecycle.STOPPING:
                return "STOPPING";
            case Lifecycle.STOPPED:
                return "STOPPED";
            case Lifecycle.UNINITIALIZED:
                return "UNINITIALIZED";
            default:
                return "UNKNOWN";
        }
    }
}
