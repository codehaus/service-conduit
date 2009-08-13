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
package org.sca4j.binding.burlap.control;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.binding.burlap.provision.BurlapWireSourceDefinition;
import org.sca4j.binding.burlap.provision.BurlapWireTargetDefinition;
import org.sca4j.binding.burlap.scdl.BurlapBindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;

/**
 * Implementation of the hessian binding generator.
 *
 * @version $Revision: 5215 $ $Date: 2008-08-18 05:44:46 +0100 (Mon, 18 Aug 2008) $
 */
@EagerInit
public class BurlapBindingGenerator implements BindingGenerator<BurlapWireSourceDefinition, BurlapWireTargetDefinition, BurlapBindingDefinition> {

    public BurlapWireSourceDefinition generateWireSource(LogicalBinding<BurlapBindingDefinition> logicalBinding,
                                                         Policy policy,
                                                         ServiceDefinition serviceDefinition)
            throws GenerationException {
        // TODO Pass the contract information to physical
        URI id = logicalBinding.getParent().getParent().getClassLoaderId();
        BurlapWireSourceDefinition hwsd = new BurlapWireSourceDefinition();
        hwsd.setClassLoaderId(id);
        URI targetUri = logicalBinding.getBinding().getTargetUri();
        hwsd.setUri(targetUri);
        return hwsd;
    }

    public BurlapWireTargetDefinition generateWireTarget(LogicalBinding<BurlapBindingDefinition> logicalBinding,
                                                         Policy policy,
                                                         ReferenceDefinition referenceDefinition) throws GenerationException {

        // TODO Pass the contract information to the physical
        URI id = logicalBinding.getParent().getParent().getClassLoaderId();
        BurlapWireTargetDefinition hwtd = new BurlapWireTargetDefinition(id);
        hwtd.setUri(logicalBinding.getBinding().getTargetUri());
        return hwtd;

    }


}
