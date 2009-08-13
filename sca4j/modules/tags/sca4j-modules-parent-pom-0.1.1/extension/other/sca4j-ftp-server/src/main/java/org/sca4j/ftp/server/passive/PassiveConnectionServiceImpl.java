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
package org.sca4j.ftp.server.passive;

import java.util.Stack;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

/**
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class PassiveConnectionServiceImpl implements PassiveConnectionService {
    
    private int minPort;
    private int maxPort;
    private Stack<Integer> ports = new Stack<Integer>();
    
    /**
     * Sets the minimum passive port.
     * @param minPort Minimum passive port.
     */
    @Property
    public void setMinPort(int minPort) {
        this.minPort = minPort;
    }

    /**
     * Sets the maximum passive port.
     * @param maxPort Maximum passive port.
     */
    @Property
    public void setMaxPort(int maxPort) {
        this.maxPort = maxPort;
    }
    
    /**
     * Initializes the port.
     */
    @Init
    public void init() {
        for (int i = minPort;i <= maxPort;i++) {
            ports.push(i);
        }
    }
    
    /**
     * Acquires the next available pasive port.
     * 
     * @return Next available passive port.
     * @throws InterruptedException 
     */
    public synchronized int acquire() throws InterruptedException {
        while (ports.empty()) {
            wait();
        }
        return ports.pop();
    }
    
    /**
     * Release a passive port.
     * @param port Port to be released.
     */
    public synchronized void release(int port) {
        ports.push(port);
        notifyAll();
    }

}
