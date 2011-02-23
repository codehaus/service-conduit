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

import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.file.common.FileBindingMetadata;
import org.sca4j.binding.file.provision.FileWireTargetDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches target end of the wire to the Physical target.
 * 
 */
public class FileTargetWireAttacher implements TargetWireAttacher<FileWireTargetDefinition> {
    @Monitor FileBindingMonitor monitor;

    /**
     * {@inheritDoc} 
     */
    public void attachToTarget(PhysicalWireSourceDefinition source, FileWireTargetDefinition target, Wire wire) throws WiringException {
        final File rootDir = getRootDirIfExists(target.getUri());
        final FileBindingMetadata bindingMetaData = target.getBindingMetaData();
        final FileTargetInterceptor interceptor = new FileTargetInterceptor(rootDir, bindingMetaData.acquireFileLock, bindingMetaData.tmpFileSuffix);
        wire.getInvocationChains().entrySet().iterator().next().getValue().addInterceptor(interceptor);
    }

    /**
     * {@inheritDoc} 
     */
    public void detachFromTarget(PhysicalWireSourceDefinition source, FileWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }

    /**
     * {@inheritDoc} 
     */
    public ObjectFactory<?> createObjectFactory(FileWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }
    
    private File getRootDirIfExists(URI targetUri) throws WiringException {
        if (targetUri != null) {
            try {
                final URI decodedUri = URI.create(URLDecoder.decode(targetUri.toString(), "UTF-8"));
                File fileRoot;
                if (decodedUri.getScheme() != null && decodedUri.getScheme().equalsIgnoreCase("file")) {//valid file URL
                    fileRoot = new File(decodedUri);
                } else { //URN
                    fileRoot = new File(decodedUri.toString());
                }
                
                if (!fileRoot.isDirectory()) {
                    throw new WiringException("Location specified is not a valid directory - " + decodedUri);
                }
                return fileRoot;
            } catch (UnsupportedEncodingException e) {
                throw new WiringException(e);
            }
        }
        return null;
    }

}
