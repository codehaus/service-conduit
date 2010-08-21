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
        try {
            final URI uri = URI.create(URLDecoder.decode(target.getUri().toString(), "UTF-8"));
            final File fileRoot = new File(uri);
            if (!fileRoot.isDirectory()) {
                throw new WiringException("Location specified is not a valid directory - " + uri);
            }
            final FileTargetInterceptor interceptor = new FileTargetInterceptor(fileRoot, target.getBindingMetaData().acquireLock);
            wire.getInvocationChains().entrySet().iterator().next().getValue().addInterceptor(interceptor);
        } catch (UnsupportedEncodingException e) {
            throw new WiringException(e);
        }
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

}
