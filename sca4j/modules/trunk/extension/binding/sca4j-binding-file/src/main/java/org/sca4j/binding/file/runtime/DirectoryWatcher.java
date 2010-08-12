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
import java.util.regex.Pattern;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

/**
 * Polls the directory to list the files and passes them to {@link FileServiceInvoker} for processing. Once file is
 * processed it will be either archived or deleted.
 * 
 * @author dhillonn
 * 
 */
public class DirectoryWatcher extends DefaultPausableWork {
    private FileBindingMonitor monitor;
    private FileServiceInvoker serviceInvoker;

    private final File endpointDir;
    private File archiveDir;
    private Pattern fileNamePattern;
    private boolean acquireLock;

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
     * Sets the flag to specify if lock must be acquired before reading the file.
     * @param acquireLock flag to indicate if lock to be acquired
     */
    public void setAcquireLock(boolean acquireLock) {
        this.acquireLock = acquireLock;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    protected void execute() {
        try {
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
                    serviceInvoker.invoke(file, archiveDir, acquireLock);
                }
            }
        } catch (Exception e) {
            monitor.onException("Unexpected error occured", e);
        }
    }
}
