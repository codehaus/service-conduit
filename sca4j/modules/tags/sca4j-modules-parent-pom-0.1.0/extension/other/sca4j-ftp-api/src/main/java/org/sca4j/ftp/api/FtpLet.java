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
package org.sca4j.ftp.api;

import java.io.InputStream;

/**
 * Interface for receiving FTP callbacks.
 * <p/>
 * Note: The concept is borrowed from Apache MINA FTP Server.
 *
 * @version $Revision$ $Date$
 */
public interface FtpLet {

    /**
     * Callback when data is uploaded by the remote FTP client.
     *
     * @param fileName    Name of the file being uploaded.
     * @param contentType the type of data (e.g. binary or text) being uploaded
     * @param uploadData  Stream of data that is being uploaded.
     * @return true if the operation completed
     * @throws Exception If unable to handle the data.
     */
    boolean onUpload(String fileName, String contentType, InputStream uploadData) throws Exception;

}
