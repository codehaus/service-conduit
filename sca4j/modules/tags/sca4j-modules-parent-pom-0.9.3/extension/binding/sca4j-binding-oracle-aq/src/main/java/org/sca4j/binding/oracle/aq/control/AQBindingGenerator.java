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
package org.sca4j.binding.oracle.aq.control;

import org.sca4j.binding.oracle.aq.provision.AQWireSourceDefinition;
import org.sca4j.binding.oracle.aq.provision.AQWireTargetDefinition;
import org.sca4j.binding.oracle.aq.scdl.AQBindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;

/**
 * The BindingGenerator for AQ. Implements {@link BindingGenerator}
 */
public class AQBindingGenerator implements BindingGenerator<AQWireSourceDefinition, AQWireTargetDefinition, AQBindingDefinition> {

    /**
     * Generates the {@link AQWireSourceDefinition}.
     *
     * @param logicalBinding the logical binding
     * @param policy the policy
     * @param service the service
     *
     * @return the AQ wire source definition
     *
     * @throws GenerationException the generation exception
     */
    public AQWireSourceDefinition generateWireSource(LogicalBinding<AQBindingDefinition> logicalBinding, Policy policy, ServiceDefinition service) { 
        AQWireSourceDefinition wireSourceDefinition = new AQWireSourceDefinition();
        wireSourceDefinition.bindingDefinition = logicalBinding.getBinding();
        return wireSourceDefinition;
    }

    /**
     * Generates the {@link AQWireTargetDefinition}.
     *
     * @param logicalBinding the logical binding
     * @param policy the policy
     * @param reference the reference
     *
     * @return the AQ wire target definition
     *
     * @throws GenerationException the generation exception
     */
    public AQWireTargetDefinition generateWireTarget(LogicalBinding<AQBindingDefinition> logicalBinding, Policy policy, ReferenceDefinition reference) {       
        AQWireTargetDefinition targetDefinition = new AQWireTargetDefinition();
        targetDefinition.bindingDefinition = logicalBinding.getBinding();
        return targetDefinition;
    }
}
