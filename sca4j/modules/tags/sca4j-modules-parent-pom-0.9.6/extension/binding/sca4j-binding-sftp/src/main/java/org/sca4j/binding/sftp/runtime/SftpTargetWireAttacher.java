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
package org.sca4j.binding.sftp.runtime;

import org.sca4j.binding.sftp.common.SftpBindingMetadata;
import org.sca4j.binding.sftp.provision.SftpWireTargetDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches target end of the wire to the Physical target.
 * 
 */
public class SftpTargetWireAttacher implements TargetWireAttacher<SftpWireTargetDefinition> {
    /**
     * {@inheritDoc} 
     */
    public void attachToTarget(PhysicalWireSourceDefinition source, SftpWireTargetDefinition target, Wire wire) throws WiringException {
        final SftpBindingMetadata bmd = target.getBindingMetaData();
        
        final SftpTargetInterceptor interceptor = new SftpTargetInterceptor(bmd.host, bmd.port, target.getSecurityPolicy());
        interceptor.setRemotePath(bmd.remotePath);
        interceptor.setTmpFileSuffix(bmd.tmpFileSuffix);
        wire.getInvocationChains().entrySet().iterator().next().getValue().addInterceptor(interceptor);
    }

    /**
     * {@inheritDoc} 
     */
    public void detachFromTarget(PhysicalWireSourceDefinition source, SftpWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }

    /**
     * {@inheritDoc} 
     */
    public ObjectFactory<?> createObjectFactory(SftpWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }
}
