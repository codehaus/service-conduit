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
package org.sca4j.binding.ws.metro.control;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.sca4j.binding.ws.metro.provision.MetroWireSourceDefinition;
import org.sca4j.binding.ws.metro.provision.MetroWireTargetDefinition;
import org.sca4j.binding.ws.metro.provision.EndPointPolicy;
import org.sca4j.binding.ws.provision.WsdlElement;
import org.sca4j.binding.ws.scdl.WsBindingDefinition;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.generator.BindingGeneratorDelegate;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;

/**
 * @version $Revision$ $Date$
 * 
 */
public class MetroBindingGeneratorDelegate implements BindingGeneratorDelegate<WsBindingDefinition> {
    
    public MetroWireSourceDefinition generateWireSource(LogicalBinding<WsBindingDefinition> binding,
                                                        Policy policy,  
                                                        ServiceDefinition serviceDefinition) throws GenerationException {
        
        MetroWireSourceDefinition hwsd = new MetroWireSourceDefinition();
        hwsd.setUri(binding.getBinding().getTargetUri());
        
        ServiceContract contract = serviceDefinition.getServiceContract();
        hwsd.setServiceInterface(contract.getQualifiedInterfaceName());
        
        URI classloaderId = binding.getParent().getParent().getClassLoaderId();
        hwsd.setClassLoaderId(classloaderId);

		hwsd.setPolicyDefinition(createPolicyDefinition(contract, policy));
                
        return hwsd;
        
    }

	public MetroWireTargetDefinition generateWireTarget(LogicalBinding<WsBindingDefinition> binding,
                                                        Policy policy,
                                                        ReferenceDefinition referenceDefinition) throws GenerationException {

        MetroWireTargetDefinition hwtd = new MetroWireTargetDefinition();
        WsdlElement wsdlElement = parseWsdlElement(binding.getBinding().getWsdlElement());
        hwtd.setWsdlElement(wsdlElement);
        hwtd.setWsdlLocation(binding.getBinding().getWsdlLocation());
        hwtd.setUri(binding.getBinding().getTargetUri());
        
        ServiceContract contract = referenceDefinition.getServiceContract();
        hwtd.setReferenceInterface(contract.getQualifiedInterfaceName());
        
        URI classloaderId = binding.getParent().getParent().getClassLoaderId();
        hwtd.setClassloaderURI(classloaderId);
        
        //Set config
        hwtd.setConfig(binding.getBinding().getConfig());
        
		hwtd.setPolicyDefinition(createPolicyDefinition(contract, policy));
        
        return hwtd;
    }

    private WsdlElement parseWsdlElement(String wsdlElement) throws GenerationException {
        if(wsdlElement == null) {
            return null;
        }
        
        String[] token = wsdlElement.split("#");
        String namespaceUri = token[0];
        
        if (!token[1].startsWith("wsdl.port")) {
            throw new GenerationException("Only WSDL 1.1 ports are currently supported");
        }
        token = token[1].substring(token[1].indexOf('(') + 1, token[1].indexOf(')')).split("/");

        QName serviceName = new QName(namespaceUri, token[0]);
        QName portName = new QName(namespaceUri, token[1]);
        
        return new WsdlElement(serviceName, portName);        
    }

	private EndPointPolicy createPolicyDefinition(ServiceContract contract, Policy policy) {
		EndPointPolicy policyDefinition = new EndPointPolicy();
		for (Operation operation : contract.getOperations()) {
			List<PolicySet> providedPolicySets = policy.getProvidedPolicySets(operation);
			policyDefinition.addPolicySets(operation.getName(), providedPolicySets);
		}
		return policyDefinition;
	}

}
