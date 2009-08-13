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
package org.sca4j.binding.aq.control;

import java.net.URI;

import org.sca4j.binding.aq.provision.AQWireSourceDefinition;
import org.sca4j.binding.aq.provision.AQWireTargetDefinition;
import org.sca4j.binding.aq.scdl.AQBindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;
import org.osoa.sca.annotations.EagerInit;

/**
 * Binding generator that creates the physical source and target definitions for wires. Message acknowledgement is
 * always expected to be using transactions, either local or global, as expressed by the intents transactedOneWay,
 * transactedOneWay.local or transactedOneWay.global.
 *
 * @version $Revision: 4817 $ $Date: 2008-06-11 20:01:35 +0100 (Wed, 11 Jun 2008) $
 */
@EagerInit
public class AQBindingGenerator implements BindingGenerator<AQWireSourceDefinition, AQWireTargetDefinition, AQBindingDefinition> {

    /**
     * Builds the Source Definition
     */
    public AQWireSourceDefinition generateWireSource(LogicalBinding<AQBindingDefinition> logicalBinding, 
                                                     Policy policy, 
                                                     ServiceDefinition serviceDefinition) {
        URI classLoaderId = logicalBinding.getParent().getParent().getParent().getUri();
        AQBindingDefinition bd = logicalBinding.getBinding();        
        return new AQWireSourceDefinition(bd.getDestinationName(), bd.getInitialState(), bd.getDataSourceKey(), bd.getConsumerCount(), classLoaderId);
    }

    /**
     * Builds the Target Definition
     */
    public AQWireTargetDefinition generateWireTarget(LogicalBinding<AQBindingDefinition> logicalBinding, 
                                                     Policy policy, 
                                                     ReferenceDefinition referenceDefinition)throws GenerationException {
        URI classLoaderId = logicalBinding.getParent().getParent().getParent().getUri();
        AQBindingDefinition bd = logicalBinding.getBinding();                
        return new AQWireTargetDefinition(bd.getDestinationName(), bd.getDataSourceKey(), classLoaderId);

    }

}
