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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.oasisopen.sca.ServiceRuntimeException;

/**
 * Represents the cluster-wide endpoint lock to ensure serialised access to a file endpoint.
 */
public class FileEndpointLock {
    private static String endpointLockFileName = ".scEndpointLock";
    private FileLock fileLock;
    private FileChannel fileChannel;

    private FileEndpointLock(FileLock fileLock, FileChannel fileChannel) {
        this.fileLock = fileLock;
        this.fileChannel = fileChannel;
    }
    
    /**
     * Create the instance of {@link FileEndpointLock}, returns <code>null</code> if endpoint lock could not be acquired.
     * @param endpointDir endpoint dir
     * @return
     */
    public static FileEndpointLock acquireEndpointLock(File endpointDir) {
        try {
            File lockFile = new File(endpointDir, endpointLockFileName);
            FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel();            
            FileLock endpointLock = channel.tryLock();
            
            return endpointLock != null ? new FileEndpointLock(endpointLock, channel) : null;

        } catch (FileNotFoundException e) {
            throw new ServiceRuntimeException(e);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void releaseEndpointLock() {
        FileUtils.closeQuietly(fileLock);
        FileUtils.closeQuietly(fileChannel);
    }
}
