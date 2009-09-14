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
 *
 *
 * Original Codehaus Header
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * Original Apache Header
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
