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
package org.sca4j.binding.ws.control;

import java.util.Map;

import org.sca4j.binding.ws.scdl.WsBindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.BindingGeneratorDelegate;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

/**
 * Implementation of the WS binding generator.
 *
 * @version $Revision: 3145 $ $Date: 2008-03-19 11:04:25 +0000 (Wed, 19 Mar 2008) $
 */
@EagerInit
public class WsBindingGenerator implements BindingGenerator<PhysicalWireSourceDefinition, PhysicalWireTargetDefinition, WsBindingDefinition> {
    
    private Map<String, BindingGeneratorDelegate<WsBindingDefinition>> delegates;

    @Reference
    public void setDelegates(Map<String, BindingGeneratorDelegate<WsBindingDefinition>> delegates) {
        this.delegates = delegates;
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalBinding<WsBindingDefinition> logicalBinding,
                                                           Policy policy,
                                                           ServiceDefinition serviceDefinition)
            throws GenerationException {
        return getDelegate(logicalBinding).generateWireSource(logicalBinding, policy, serviceDefinition);
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalBinding<WsBindingDefinition> logicalBinding,
                                                           Policy policy,
                                                           ReferenceDefinition referenceDefinition)
            throws GenerationException {
        return getDelegate(logicalBinding).generateWireTarget(logicalBinding, policy, referenceDefinition);
    }

    /*
     * Gets the delegate for the requested WS stack. If no specific implementation is specified, the first registered
     * stack is returned.
     */
    private BindingGeneratorDelegate<WsBindingDefinition> getDelegate(LogicalBinding<WsBindingDefinition> logicalBinding)
            throws WsBindingGenerationException {

        if (delegates.isEmpty()) {
            throw new MissingWebServiceExtensionException();
        }
        String implementation = logicalBinding.getBinding().getImplementation();
        BindingGeneratorDelegate<WsBindingDefinition> delegate;
        if (implementation == null) {
            delegate = delegates.values().iterator().next();
        } else {
            delegate = delegates.get(implementation);
        }
        if (delegate == null) {
            throw new WsBindingGenerationException("Unknown web services extension requested", implementation);
        }
        return delegate;

    }

}
