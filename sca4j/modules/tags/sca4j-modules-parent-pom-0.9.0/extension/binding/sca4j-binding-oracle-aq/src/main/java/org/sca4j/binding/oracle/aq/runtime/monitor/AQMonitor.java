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
package org.sca4j.binding.oracle.aq.runtime.monitor;

import org.sca4j.api.annotation.logging.Fine;
import org.sca4j.api.annotation.logging.Info;
import org.sca4j.api.annotation.logging.Severe;


/**
 * Monitor interface for AQ.
 */
public interface AQMonitor{
    /**
     * Log message to when the consumption is stopped.
     *
     * @param message the message
     */
    @Fine
    void stopConsumption(String message);

    /**
     * Log message when the consumers are started.
     *
     * @param message the message
     */
    @Fine
    void startConsumer(String message);

    /**
     * Logs the Exception.
     *
     * @param message the message
     * @param message stack trace of the exception 
     */
    @Severe
    void onException(String message, String stackTrace);
    
    /**
     * Message Fault
     * @param message
     */
    @Info
    void messageFault(String message);
    
    
    /**
     * Message Fault
     * @param message
     */
    @Fine
    void generalMessage(String message);
    
    /**
     * Message Fault
     * @param message
     */
    @Fine
    void inactiveQueue(String message);
    
    /**
     * Report that message has been committed
     * @param message
     */
    @Fine
    void reportOnCommit(String message);
}
