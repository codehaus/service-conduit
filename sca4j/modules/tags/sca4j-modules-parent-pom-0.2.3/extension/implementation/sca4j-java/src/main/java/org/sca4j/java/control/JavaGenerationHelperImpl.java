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
package org.sca4j.java.control;

import java.net.URI;
import javax.xml.namespace.QName;

import org.osoa.sca.annotations.Reference;

import org.sca4j.host.Namespaces;
import org.sca4j.java.provision.JavaComponentDefinition;
import org.sca4j.java.provision.JavaWireSourceDefinition;
import org.sca4j.java.provision.JavaWireTargetDefinition;
import org.sca4j.java.scdl.JavaImplementation;
import org.sca4j.pojo.control.InstanceFactoryGenerationHelper;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.CallbackDefinition;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.Scope;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.InteractionType;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * @version $Revision$ $Date$
 */
public class JavaGenerationHelperImpl implements JavaGenerationHelper {
    private static final QName PROPAGATES_CONVERSATION_POLICY = new QName(Namespaces.SCA4J_NS, "propagatesConversationPolicy");
    private final InstanceFactoryGenerationHelper helper;

    public JavaGenerationHelperImpl(@Reference InstanceFactoryGenerationHelper helper) {
        this.helper = helper;
    }

    public JavaComponentDefinition generate(LogicalComponent<? extends JavaImplementation> component, JavaComponentDefinition physical)
            throws GenerationException {
        ComponentDefinition<? extends JavaImplementation> logical = component.getDefinition();
        JavaImplementation implementation = logical.getImplementation();
        PojoComponentType type = implementation.getComponentType();
        String scope = type.getScope();

        // create the instance factory definition
        InstanceFactoryDefinition providerDefinition = new InstanceFactoryDefinition();
        providerDefinition.setReinjectable(Scope.COMPOSITE.getScope().equals(scope));
        providerDefinition.setConstructor(type.getConstructor());
        providerDefinition.setInitMethod(type.getInitMethod());
        providerDefinition.setDestroyMethod(type.getDestroyMethod());
        providerDefinition.setImplementationClass(implementation.getImplementationClass());
        helper.processInjectionSites(component, providerDefinition);

        // create the physical component definition
        URI componentId = component.getUri();
        physical.setComponentId(componentId);
        physical.setGroupId(component.getParent().getUri());
        physical.setScope(scope);
        physical.setInitLevel(helper.getInitLevel(logical, type));
        physical.setMaxAge(type.getMaxAge());
        physical.setMaxIdleTime(type.getMaxIdleTime());
        physical.setProviderDefinition(providerDefinition);
        helper.processPropertyValues(component, physical);
        // generate the classloader resource definition
        URI classLoaderId = component.getClassLoaderId();
        physical.setClassLoaderId(classLoaderId);
        return physical;
    }


    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<? extends JavaImplementation> source,
                                                           JavaWireSourceDefinition wireDefinition,
                                                           LogicalReference reference,
                                                           Policy policy) throws GenerationException {
        URI uri = reference.getUri();
        ServiceContract<?> serviceContract = reference.getDefinition().getServiceContract();
        String interfaceName = serviceContract.getQualifiedInterfaceName();
        URI classLoaderId = source.getClassLoaderId();

        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.REFERENCE, uri.getFragment()));
        wireDefinition.setInterfaceName(interfaceName);
        // assume for now that any wire from a Java component can be optimized
        wireDefinition.setOptimizable(true);

        wireDefinition.setClassLoaderId(classLoaderId);
        calculateConversationalPolicy(wireDefinition, serviceContract, policy);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<? extends JavaImplementation> source,
                                                                   JavaWireSourceDefinition wireDefinition,
                                                                   ServiceContract<?> serviceContract,
                                                                   Policy policy) throws GenerationException {
        String interfaceName = serviceContract.getQualifiedInterfaceName();
        URI classLoaderId = source.getClassLoaderId();
        PojoComponentType type = source.getDefinition().getImplementation().getComponentType();
        String name = null;
        for (CallbackDefinition entry : type.getCallbacks().values()) {
            // NB: This currently only supports the case where one callback injection site of the same type is on an implementation.
            // TODO clarify with the spec if having more than one callback injection site of the same type is valid
            if (entry.getServiceContract().isAssignableFrom(serviceContract)) {
                name = entry.getName();
                break;
            }
        }
        if (name == null) {
            String interfaze = serviceContract.getQualifiedInterfaceName();
            throw new CallbackSiteNotFound("Callback injection site not found for type: " + interfaze, interfaze);
        }

        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.CALLBACK, name));
        wireDefinition.setInterfaceName(interfaceName);
        wireDefinition.setUri(URI.create(source.getUri().toString() + "#" + name));
        wireDefinition.setOptimizable(false);
        wireDefinition.setClassLoaderId(classLoaderId);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<? extends JavaImplementation> source,
                                                                   LogicalResource<?> resource,
                                                                   JavaWireSourceDefinition wireDefinition) throws GenerationException {
        URI uri = resource.getUri();
        ServiceContract<?> serviceContract = resource.getResourceDefinition().getServiceContract();
        String interfaceName = serviceContract.getQualifiedInterfaceName();
        URI classLoaderId = source.getClassLoaderId();

        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.RESOURCE, uri.getFragment()));
        wireDefinition.setClassLoaderId(classLoaderId);
        wireDefinition.setInterfaceName(interfaceName);
        return wireDefinition;
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service,
                                                           LogicalComponent<? extends JavaImplementation> target,
                                                           JavaWireTargetDefinition wireDefinition,
                                                           Policy policy) throws GenerationException {
        URI uri;
        if (service != null) {
            uri = service.getUri();
        } else {
            // no service specified, use the default
            uri = target.getUri();
        }
        wireDefinition.setUri(uri);
        URI classLoaderId = target.getClassLoaderId();
        wireDefinition.setClassLoaderId(classLoaderId);

        // assume for now that only wires to composite scope components can be optimized
        String scope = target.getDefinition().getImplementation().getComponentType().getScope();
        wireDefinition.setOptimizable("COMPOSITE".equals(scope));
        return wireDefinition;
    }


    /**
     * Determines if the wire propagates conversations. Conversational propagation is handled by the source component.
     *
     * @param wireDefinition  the source wire defintion
     * @param serviceContract the wire service cotnract
     * @param policy          the set of policies for the wire
     */
    private void calculateConversationalPolicy(JavaWireSourceDefinition wireDefinition, ServiceContract<?> serviceContract, Policy policy) {
        for (Operation<?> operation : serviceContract.getOperations()) {
            for (PolicySet policySet : policy.getProvidedPolicySets(operation)) {
                if (PROPAGATES_CONVERSATION_POLICY.equals(policySet.getName())) {
                    wireDefinition.setInteractionType(InteractionType.PROPAGATES_CONVERSATION);
                    // conversational propagation is for the entire reference so set it an return
                    return;
                }
            }
        }
        if (serviceContract.isConversational()) {
            wireDefinition.setInteractionType(InteractionType.CONVERSATIONAL);
        }

    }


}
