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
 */
package org.sca4j.binding.file.runtime;

import java.io.File;

import org.sca4j.api.annotation.logging.Fine;
import org.sca4j.api.annotation.logging.Info;
import org.sca4j.api.annotation.logging.Severe;
import org.sca4j.api.annotation.logging.Warning;

/**
 * Monitor interface to log significant events for File binding.
 */
public interface FileBindingMonitor {
    
    /**
     * Log the event of file extension started
     */
    @Info
    void fileExtensionStarted(File endpoint, boolean acquireFileLock, boolean acquireEndpointLock);

    /**
     * Log the exception
     * @param msg error message
     * @param throwable cause
     */
    @Severe
    void onException(String msg, Throwable throwable);
    
    /**
     * Log the error when not able to move the file to archive directory
     * @param file
     * @param archiveDir
     */
    @Severe
    void unableToArchive(File file, File archiveDir);
    
    /**
     * Log the error when not able to delete the source file
     * @param file
     */
    @Severe
    void unableToDelete(File file);
    
    /**
     * Log the warning when unable to acquire the lock before reading the file
     * @param fileName
     */
    @Warning
    void unableToAcquireLock(String fileName);
    
    /**
     * Log when file successfully deleted
     * @param file
     */
    @Fine
    void fileDeleted(File file);
    
    /**
     * Log when file successfully archived
     * @param file
     * @param archiveDir
     */
    @Fine
    void fileArchived(File file, File archiveDir);
    
    /**
     * Log the event when file is skipped during the polling
     * @param fileName
     */
    @Fine
    void fileSkipped(String fileName);
    
    /**
     * Log the event of endpoint lock attempt event
     * @param endpoint file endpoint
     * @param lockAquired flag to indicate if lock is acquired or not
     */
    @Fine
    void endpointLockAttempted(File endpoint, boolean lockAquired);
    
    /**
     * Log the event of endpoint lock released
     * @param endpoint
     */
    @Fine
    void endpointLockReleased(File endpoint);
    
}
