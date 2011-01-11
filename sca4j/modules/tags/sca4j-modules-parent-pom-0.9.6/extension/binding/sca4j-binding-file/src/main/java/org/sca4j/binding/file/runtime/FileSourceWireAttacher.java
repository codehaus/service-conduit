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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.file.common.FileBindingMetadata;
import org.sca4j.binding.file.provision.FileWireSourceDefinition;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.services.event.EventService;
import org.sca4j.spi.services.event.RuntimeStart;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches physical source to the wire.
 */
public class FileSourceWireAttacher implements SourceWireAttacher<FileWireSourceDefinition> {
    @Monitor protected FileBindingMonitor monitor;
    @Reference protected WorkScheduler workScheduler;
    @Reference protected EventService eventService;
    
    /**
     * {@inheritDoc} 
     */
    public void attachToSource(FileWireSourceDefinition source, PhysicalWireTargetDefinition target, final Wire wire) throws WiringException {

        final FileBindingMetadata bindingMetaData = source.getBindingMetaData();
        File endpointDir = getDirectory(source.getUri());
        File archiveDir = getDirectory(bindingMetaData.archiveUri);

        PhysicalOperationDefinition pod = wire.getInvocationChains().entrySet().iterator().next().getKey().getSourceOperation();

        List<?> parameters = pod.getParameters();
        if (parameters.size() == 2) {
            final DirectoryWatcher directoryWatcher = new DirectoryWatcher(endpointDir, wire, monitor);
            directoryWatcher.setAcquireFileLock(bindingMetaData.acquireFileLock);
            directoryWatcher.setAcquireEndpointLock(bindingMetaData.acquireEndpointLock);
            directoryWatcher.setFileNamePattern(bindingMetaData.filenamePattern);
            directoryWatcher.setArchiveDir(archiveDir);
            directoryWatcher.setPollingFrequency(bindingMetaData.pollingFrequency);
            directoryWatcher.setArchiveFileTSPattern((bindingMetaData.archiveFileTimestampPattern));
            monitor.fileExtensionStarted(endpointDir, bindingMetaData.acquireFileLock, bindingMetaData.acquireEndpointLock);
            eventService.subscribe(RuntimeStart.class, directoryWatcher);
            workScheduler.scheduleWork(directoryWatcher);

        } else {
            throw new WiringException("file binding service requires 2 arguments - <String filename, InputStream payload>");
        }
    }

    /**
     * {@inheritDoc} 
     */
    public void detachFromSource(FileWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc} 
     */
    public void attachObjectFactory(FileWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition definition)
            throws WiringException {
        throw new UnsupportedOperationException();
    }

    private File getDirectory(URI uri) throws WiringException {
        if (uri == null) {
            return null;
        }
        try {
            final URI decodedUri = URI.create(URLDecoder.decode(uri.toString(), "UTF-8"));
            
            File dir;
            if(decodedUri.getScheme() != null && decodedUri.getScheme().equalsIgnoreCase("file")) { //valid file URL
                dir = new File(decodedUri);
            } else { //URN
                dir = new File(decodedUri.toString());
            }
            
            if (dir.exists() && dir.canRead() && dir.isDirectory()) {
                return dir;
            } else {
                throw new WiringException("Error in accessing directory: ensure directory exists and can be read:" + dir);
            }
        } catch (UnsupportedEncodingException e) {
            throw new WiringException(e);
        }
    }
}
