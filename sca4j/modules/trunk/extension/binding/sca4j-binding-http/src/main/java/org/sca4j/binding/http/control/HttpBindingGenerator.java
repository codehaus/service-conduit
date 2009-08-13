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
package org.sca4j.binding.http.control;

import java.net.URI;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.http.provision.HttpSourceWireDefinition;
import org.sca4j.binding.http.provision.HttpTargetWireDefinition;
import org.sca4j.binding.http.provision.PolicyAware;
import org.sca4j.binding.http.scdl.HttpBindingDefinition;
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
import org.w3c.dom.NodeList;

/**
 * Implementation of the HTTP binding generator.
 */
@EagerInit
public class HttpBindingGenerator implements BindingGenerator<HttpSourceWireDefinition, HttpTargetWireDefinition, HttpBindingDefinition> {
    
    private static final String HTTP_BINDING_POLICY = "httpBindingConfig";
    
    @Reference protected Map<String, PolicyApplier> policyAppliers;

    /**
     * {@inheritDoc}
     */
    public HttpSourceWireDefinition generateWireSource(LogicalBinding<HttpBindingDefinition> binding, Policy policy, ServiceDefinition definition) throws GenerationException {
        URI classloaderId = binding.getParent().getParent().getParent().getUri();
        URI endpointUri = binding.getBinding().getTargetUri();
        String interfaze = definition.getServiceContract().getQualifiedInterfaceName();
        HttpSourceWireDefinition source =  new HttpSourceWireDefinition(classloaderId, endpointUri, interfaze);
        applyTargetPolicies(definition.getServiceContract(), policy, source);
        return source;
    }

    /**
     * {@inheritDoc}
     */
    public HttpTargetWireDefinition generateWireTarget(LogicalBinding<HttpBindingDefinition> binding, Policy policy, ReferenceDefinition definition) throws GenerationException {
        URI classloaderId = binding.getParent().getParent().getParent().getUri();
        URI endpointUri = binding.getBinding().getTargetUri();
        String interfaze = definition.getServiceContract().getQualifiedInterfaceName();
        HttpTargetWireDefinition target = new HttpTargetWireDefinition(classloaderId, endpointUri, interfaze);
        applyTargetPolicies(definition.getServiceContract(), policy, target);
        return target;
    }
    
    private void applyTargetPolicies(ServiceContract<?> serviceContract, Policy policy, PolicyAware policyAware) throws GenerationException {
        
        for (Operation<?> operation : serviceContract.getOperations()) {
            
            for (PolicySet policySet : policy.getProvidedPolicySets(operation)) {
                
                Element element = policySet.getExtension();
                if (!HTTP_BINDING_POLICY.equals(element.getLocalName())) {
                    throw new GenerationException("Unknown policy " + element.getLocalName());
                }
                Element policyElement = getPolicyElement(element);                
                PolicyApplier policyApplier = policyAppliers.get(policyElement.getLocalName());
                if (policyApplier == null) {
                    throw new GenerationException("Unknown policy element " + element.getLocalName());
                }
                
                policyApplier.applyPolicy(policyAware, policyElement);
                
            }
        }
        
    }

    private Element getPolicyElement(Element element) {
        NodeList nodeList = element.getChildNodes();               
        for (int i = 0; nodeList != null && i < nodeList.getLength();i++) {
            if (nodeList.item(i) instanceof Element) {
                return (Element) nodeList.item(i);
            }
        }
        return null;
    }
    
}
