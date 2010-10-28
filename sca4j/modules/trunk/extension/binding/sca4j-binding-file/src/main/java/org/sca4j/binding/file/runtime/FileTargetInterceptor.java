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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.FileLock;

import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.ServiceRuntimeException;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;

/**
 * Intercepts calls to the File System form the reference end of the wire.
 * A lock could be acquired during the write operation depending upon the configuration.
 * 
 */
public class FileTargetInterceptor implements Interceptor {
    private Interceptor next;
    private File rootDir;
    private boolean acquireLock;
    private String tmpFileSuffix;
    
    /**
     * Constructor with rootDir and lock flag.
     * 
     * @param rootDir root directory for file-drops specified in binding definition. could be <b>null</b> for Dynamic endpoints.
     * @param acquireLock indicates if lock must be acquired during write operation.
     * @param tmpFileSuffix temporary file suffix used during file upload, file is rename to original name on completion.
     */
    public FileTargetInterceptor(File rootDir, boolean acquireLock, String tmpFileSuffix) {
        this.rootDir = rootDir;
        this.acquireLock = acquireLock;
        this.tmpFileSuffix = tmpFileSuffix;
    }

    /**
     * {@inheritDoc}
     */
    public Message invoke(Message msg) {
        final Object[] args = (Object[]) msg.getBody();
        final File targetFile = getTargetFile(args[0]);
        File transitionFile = tmpFileSuffix != null ? new File(targetFile.getPath() + tmpFileSuffix) : targetFile;
        
        final InputStream source = (InputStream) args[1];
        FileOutputStream target = null;
        FileLock fileLock = null;

        try {
            target = new FileOutputStream(transitionFile);
            if (acquireLock) {
                fileLock = target.getChannel().tryLock();
                if (fileLock == null) {
                    throw new IOException("Unable to acquire the lock on: " + transitionFile);
                }
            }
            IOUtils.copy(source, target);

        } catch (FileNotFoundException e) {
            throw new ServiceRuntimeException("Unable to create the file:" + transitionFile, e);
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        } finally {
            FileUtils.closeQuietly(fileLock);
            IOUtils.closeQuietly(source);
            IOUtils.closeQuietly(target);
        }
        
        // Rename to target file if tmpFileSuffix was used
        if (tmpFileSuffix != null) {
            boolean renameSucceeded = transitionFile.renameTo(targetFile);
            if (!renameSucceeded) {
                throw new ServiceRuntimeException(String.format("Unable to rename to originalFile[%s] from tmpFile[%s]", targetFile, transitionFile));
            }
        }

        return new MessageImpl();
    }

    /**
     * Sets the next interceptor
     */
    public void setNext(Interceptor next) {
        this.next = next;
    }

    /**
     * Gets the next interceptor
     */
    public Interceptor getNext() {
        return next;
    }
    
    private File getTargetFile(Object file) {
        if (rootDir == null) {
            if (URI.class.isInstance(file) && URI.class.cast(file).getScheme().equalsIgnoreCase("file")) {
                return new File(URI.class.cast(file));
            } else {
                return new File(file.toString());
            }
        } else {//Root directory had been provided statically
            return new File(rootDir, file.toString());
        }
    }
}
