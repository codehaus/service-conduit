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
package org.sca4j.host.work;

/**
 * Defines the contract for scheduling asychronous units of work.
 * 
 * <p>
 * Units of work can be scheduled with an optional <code>NotificationListener</code>.
 * If a notification listener is specified, the caller will be notified regarding the 
 * status of the work. The unit of work can either be completed, rejected or completed 
 * with an error. If the work completed with an error, the caller is notfied with the 
 * error details.
 * </p>
 *
 */
public interface WorkScheduler {
    
    /**
     * Schedules a unit of work for future execution. The notification listener 
     * is used to register interest in callbacks regarding the status of the work.
     * 
     * @param work The unit of work that needs to be asynchronously executed.
     */
    <T extends DefaultPausableWork>void scheduleWork(T work);

}
