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
package org.sca4j.binding.file.control;

import java.net.URI;

import org.sca4j.binding.file.provision.FileWireSourceDefinition;
import org.sca4j.binding.file.provision.FileWireTargetDefinition;
import org.sca4j.binding.file.scdl.FileBindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;

/**
 * @version $Revision$ $Date$
 */
public class FileBindingGenerator implements
        BindingGenerator<FileWireSourceDefinition, FileWireTargetDefinition, FileBindingDefinition> {

    /**
     * {@inheritDoc}
     */
    public FileWireSourceDefinition generateWireSource(LogicalBinding<FileBindingDefinition> binding, Policy policy,
                                                      ServiceDefinition serviceDefinition) throws GenerationException {

        ServiceContract serviceContract = serviceDefinition.getServiceContract();
        if (serviceContract.getOperations().size() != 1) {
            throw new GenerationException("Expects only one operation");
        }

        URI classLoaderId = binding.getParent().getParent().getParent().getUri();
        FileWireSourceDefinition hwsd = new FileWireSourceDefinition(binding.getBinding().getBindingMetadata());
        hwsd.setClassLoaderId(classLoaderId);
        URI targetUri = binding.getBinding().getTargetUri();
        hwsd.setUri(targetUri);

        return hwsd;
    }

    /**
     * {@inheritDoc}
     */
    public FileWireTargetDefinition generateWireTarget(LogicalBinding<FileBindingDefinition> binding, Policy policy,
                                                      ReferenceDefinition referenceDefinition)
            throws GenerationException {

        ServiceContract serviceContract = referenceDefinition.getServiceContract();
        if (serviceContract.getOperations().size() != 1) {
            throw new GenerationException("Expects only one operation");
        }

        URI classLoaderId = binding.getParent().getParent().getParent().getUri();

        FileWireTargetDefinition hwtd = new FileWireTargetDefinition(binding.getBinding().getBindingMetadata());
        hwtd.setClassLoaderId(classLoaderId);
        hwtd.setUri(binding.getBinding().getTargetUri());

        return hwtd;
    }

}
