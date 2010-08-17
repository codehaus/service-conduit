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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.ServiceUnavailableException;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;

/**
 * Invokes the service bound to file binding.
 * 
 * @author dhillonn
 *
 */
public class FileServiceInvoker {
    private Interceptor interceptor;
    private FileBindingMonitor monitor;

    /**
     * Constructor with mandatory parameters
     * 
     * @param interceptor service interceptor
     * @param monitor monitor to log the events
     */
    public FileServiceInvoker(Interceptor interceptor, FileBindingMonitor monitor) {
        this.interceptor = interceptor;
        this.monitor = monitor;
    }

    /**
     * Read the file from file system and pass it to the bound service.
     * 
     * @param file file to be read
     * @param archiveFile directory where file to be archived after it's read
     * @param acquireLock flag to indicate if lock must be acquired before reading the file
     */
    public void invoke(File file, File archiveFile, boolean acquireLock) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            if (!acquireLock || attemptLock(file)) {
                Object[] args = new Object[] { file.getName(), fis };
                Message input = new MessageImpl(args, false, new WorkContext());
                final Message output = interceptor.invoke(input);
                if (output.isFault()) {
                    Throwable fault = (Throwable) output.getBody();
                    throw new ServiceUnavailableException(fault);
                }
            } else {
                monitor.unableToAcquireLock(file.getName());
            }
        } finally {
            IOUtils.closeQuietly(fis);
        }
        archiveOrDelete(file, archiveFile);
    }

    private void archiveOrDelete(File file, File archiveFile) {
        if (archiveFile != null) {
            final boolean archived = file.renameTo(archiveFile);
            if (archived) {
                monitor.fileArchived(file, archiveFile);
            } else {
                monitor.unableToArchive(file, archiveFile);
            }
        } else {
            final boolean deleted = file.delete();
            if (deleted) {
                monitor.fileDeleted(file);
            } else {
                monitor.unableToDelete(file);
            }
        }
    }

    private boolean attemptLock(File file) throws IOException {
        FileChannel fileChannel = null;
        FileLock fileLock = null;
        try {
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            fileLock = fileChannel.tryLock();
            return fileLock != null;
        } finally {
            FileUtils.closeQuietly(fileLock);
            FileUtils.closeQuietly(fileChannel);
        }
    }
}
