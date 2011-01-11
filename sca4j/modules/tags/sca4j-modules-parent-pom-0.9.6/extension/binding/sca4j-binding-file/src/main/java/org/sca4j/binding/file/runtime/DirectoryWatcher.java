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
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.spi.services.event.RuntimeStart;
import org.sca4j.spi.services.event.SCA4JEvent;
import org.sca4j.spi.services.event.SCA4JEventListener;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

/**
 * Polls the directory to list the files and passes them to {@link FileServiceInvoker} for processing. Once file is
 * processed it will be either archived or deleted.
 * TODO: <A HREF="http://jira.codehaus.org/browse/SERVICECONDUIT-26">SERVICECONDUIT-26</A>
 * 
 * @author dhillonn
 * 
 */
public class DirectoryWatcher extends DefaultPausableWork implements SCA4JEventListener {
    private AtomicBoolean runtimeStarted;
    private FileBindingMonitor monitor;
    private FileServiceInvoker serviceInvoker;

    private final File endpointDir;
    private File archiveDir;
    private Pattern fileNamePattern;
    private boolean acquireFileLock;
    private boolean acquireEndpointLock;
    private long pollingFrequency;
    private SimpleDateFormat archiveFileTimestampFormat;

    /**
     * Constructor with mandatory fields
     * @param endpointDir directory to the polling on
     * @param wire wire to access the underlying service
     * @param monitor monitor for logging
     */
    public DirectoryWatcher(File endpointDir, Wire wire, FileBindingMonitor monitor) {
        super(true);
        this.endpointDir = endpointDir;
        this.monitor = monitor;
        Interceptor interceptor = wire.getInvocationChains().values().iterator().next().getHeadInterceptor();
        this.serviceInvoker = new FileServiceInvoker(interceptor, monitor);
        this.runtimeStarted = new AtomicBoolean();
    }

    /**
     * Set optional archive directory, where file be moved after its been read. In absence of this file will
     * be deleted.
     * 
     * @param archiveDir archive directory
     */
    public void setArchiveDir(File archiveDir) {
        this.archiveDir = archiveDir;
    }

    /**
     * Regular expression to filter the files 
     * @param fileNameRegex file name regular expression
     */
    public void setFileNamePattern(String fileNameRegex) {
        if (fileNameRegex != null) {
            this.fileNamePattern = Pattern.compile(fileNameRegex);
        }
    }

    /**
     * Sets directory polling frequency
     * @param pollingFrequency polling frequency
     */
    public void setPollingFrequency(long pollingFrequency) {
        this.pollingFrequency = pollingFrequency;
    }

    /**
     * Sets the flag to specify if lock must be acquired before reading the file. This is to protect against
     * in-flight files.
     * @param acquireFileLock flag to indicate if lock to be acquired
     */
    public void setAcquireFileLock(boolean acquireFileLock) {
        this.acquireFileLock = acquireFileLock;
    }
    
    /**
     * Sets the flag to specify if endpoint lock must be acquired for cluster-wide deployment.
     * @param acquireEndpointLock flag to indicate if endpoint lock to be acquired
     */
    public void setAcquireEndpointLock(boolean acquireEndpointLock) {
        this.acquireEndpointLock = acquireEndpointLock;
    }
    
    /**
     * Set timestamp suffix pattern for the archived file
     * @param archiveTimestampPattern time stamp pattern for archived file
     */
    public void setArchiveFileTSPattern(String archiveFileTimestampPattern) {
        if (archiveFileTimestampPattern != null) {
            this.archiveFileTimestampFormat = new SimpleDateFormat(archiveFileTimestampPattern);
        }        
    }

    /**
     * {@inheritDoc}  
     */
    @Override
    protected void execute() {
        if (!runtimeStarted.get()) {
            return;
        }

        FileEndpointLock endpointLock = null;
        try {            
            if (acquireEndpointLock) {
                endpointLock = FileEndpointLock.acquireEndpointLock(endpointDir);
                monitor.endpointLockAttempted(endpointDir, endpointLock != null);
                if (endpointLock == null) { //Unable to acquire the lock
                    return;
                }
            }
            final File[] fileList = endpointDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    final File file = new File(dir, name);

                    if (!(file.canRead() && file.exists() && file.isFile())) {
                        monitor.fileSkipped(file.getName());
                        return false;
                    }

                    if (fileNamePattern != null) {
                        return fileNamePattern.matcher(name).matches();
                    } else {
                        return true;
                    }
                }
            });

            if (fileList != null && fileList.length > 0) {
                for (File file : fileList) {
                    final File archiveFile = getArchiveFile(file);
                    try {
                        serviceInvoker.invoke(file, archiveFile, acquireFileLock);
                    } catch (Exception e) {
                        monitor.onException("Unexpected error occured during processing file:" + file, e);
                    }
                }
            }

        } catch (Exception e) {
            monitor.onException("Unexpected error occured", e);
        } finally {
            if (endpointLock != null) {
                endpointLock.releaseEndpointLock();
                monitor.endpointLockReleased(endpointDir);
            }
            delay();
        }
    }
    
    private File getArchiveFile(File file) {
        if (archiveDir != null) {
            String archiveFilename = file.getName();
            if (archiveFileTimestampFormat != null) {
                archiveFilename = archiveFilename + "." + archiveFileTimestampFormat.format(new Date());
            }
            return new File(archiveDir, archiveFilename);
            
        } else {
            return null;
        }
    }

    /* Pause for the duration of polling frequenc */
    private void delay() {
        if (pollingFrequency > 0) {
            try {
                Thread.sleep(pollingFrequency);
            } catch (InterruptedException e) {
                // Restore the interrupted status and allow thread to exit, Executor will take care of rest.
                //Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void onEvent(SCA4JEvent event) {
        if (RuntimeStart.class.isInstance(event)) {
            this.runtimeStarted.set(true);
        }
        
    }   
}
