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
package org.sca4j.binding.sftp.control;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.sca4j.binding.sftp.common.SecurityPolicy;
import org.sca4j.binding.sftp.provision.SftpWireSourceDefinition;
import org.sca4j.binding.sftp.provision.SftpWireTargetDefinition;
import org.sca4j.binding.sftp.scdl.SftpBindingDefinition;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;
import org.w3c.dom.Element;

/**
 * SFTP binding source and target generator
 */
public class SftpBindingGenerator implements BindingGenerator<SftpWireSourceDefinition, SftpWireTargetDefinition, SftpBindingDefinition> {

    /**
     * {@inheritDoc}
     */
    public SftpWireSourceDefinition generateWireSource(LogicalBinding<SftpBindingDefinition> binding, Policy policy,
            ServiceDefinition serviceDefinition) throws GenerationException {

        ServiceContract serviceContract = serviceDefinition.getServiceContract();
        if (serviceContract.getOperations().size() != 1) {
            throw new GenerationException("Expects only one operation");
        }

        URI classLoaderId = binding.getParent().getParent().getParent().getUri();
        SftpWireSourceDefinition hwsd = new SftpWireSourceDefinition(binding.getBinding().getBindingMetadata());
        hwsd.setClassLoaderId(classLoaderId);
        URI targetUri = binding.getBinding().getTargetUri();
        hwsd.setUri(targetUri);

        return hwsd;
    }

    /**
     * {@inheritDoc}
     */
    public SftpWireTargetDefinition generateWireTarget(LogicalBinding<SftpBindingDefinition> binding, Policy policy,
            ReferenceDefinition referenceDefinition) throws GenerationException {

        ServiceContract serviceContract = referenceDefinition.getServiceContract();
        if (serviceContract.getOperations().size() != 1) {
            throw new GenerationException("Expects only one operation");
        }

        URI classLoaderId = binding.getParent().getParent().getParent().getUri();
        
        final SecurityPolicy authenticationPolicy = processPolicies(policy, serviceContract.getOperations().iterator().next());

        SftpWireTargetDefinition hwtd = new SftpWireTargetDefinition(binding.getBinding().getBindingMetadata(), authenticationPolicy);
        hwtd.setClassLoaderId(classLoaderId);
        hwtd.setUri(binding.getBinding().getTargetUri());

        return hwtd;
    }
    
    private SecurityPolicy processPolicies(Policy policy, Operation operation) throws GenerationException {
        List<PolicySet> policySets = policy.getProvidedPolicySets(operation);
        
        if (policySets == null || policySets.size() != 1) {
            throw new GenerationException("Invalid policy configuration, please specify one security policy " + policySets);
        }

        PolicySet policySet = policySets.iterator().next();

        QName policyQName = policySet.getExtensionName();
        Element policyElement = policySet.getExtension();
        
        if (policyQName.equals(SecurityPolicy.USERNAME_POLICY_QNAME)) {
            String user = policyElement.getAttribute("user");
            String password = policyElement.getAttribute("password");
            return SecurityPolicy.createUserPasswordAuthPolicy(user, password);

        } else if (policyQName.equals(SecurityPolicy.PKI_POLICY_QNAME)) {
            String user = policyElement.getAttribute("user");
            String passphrase = policyElement.getAttribute("passphrase");
            String identityFile = policyElement.getAttribute("identityFile");
            return SecurityPolicy.createPkiAuthPolicy(user, identityFile, passphrase);

        } else {
            throw new GenerationException("Unknown policy element in authentication policy");
        }        
    }
}
