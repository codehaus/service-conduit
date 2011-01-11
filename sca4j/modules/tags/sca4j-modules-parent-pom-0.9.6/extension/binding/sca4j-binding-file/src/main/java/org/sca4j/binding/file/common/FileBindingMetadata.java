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
package org.sca4j.binding.file.common;

import java.net.URI;

/**
 * Contains configuration data for <code>binding.file</code>
 * 
 * @author dhillonn
 */
public class FileBindingMetadata {
    /** URI of the directory where the file will be moved after reading */
    public URI archiveUri;
    
    /** pattern used to select file*/
    public String filenamePattern;
    
    /** lock the file during read/write operation to protect against in-flight files */
    public boolean acquireFileLock;
    
    /** Acquire the lock on endpoint so that only one service polls the endpoint across cluster */
    public boolean acquireEndpointLock;
    
    /** polling frequency */
    public long pollingFrequency;
    
    /** Timestamp pattern for archived file */
    public String archiveFileTimestampPattern;
    
    /** Suffix used to create temp file during upload, which is then renamed 
     * to original file on completion. This is to protect against in-flight files
     */
    public String tmpFileSuffix;

}
