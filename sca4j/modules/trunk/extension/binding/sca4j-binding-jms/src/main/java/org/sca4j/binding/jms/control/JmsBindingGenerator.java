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
package org.sca4j.binding.jms.control;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.provision.JmsWireSourceDefinition;
import org.sca4j.binding.jms.provision.JmsWireTargetDefinition;
import org.sca4j.binding.jms.provision.PayloadType;
import org.sca4j.binding.jms.scdl.JmsBindingDefinition;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;

/**
 * Binding generator that creates the physical source and target definitions for
 * wires. Message acknowledgement is always expected to be using transactions,
 * either local or global, as expressed by the intents transactedOneWay,
 * transactedOneWay.local or transactedOneWay.global.
 * 
 * @version $Revision: 5021 $ $Date: 2008-07-12 18:36:13 +0100 (Sat, 12 Jul
 *          2008) $
 */
@EagerInit
public class JmsBindingGenerator implements BindingGenerator<JmsWireSourceDefinition, JmsWireTargetDefinition, JmsBindingDefinition> {

    // Transacted one way intent
    private static final QName TRANSACTED_ONEWAY = new QName(SCA_NS, "transactedOneWay");
    private static final QName TRANSACTED_ONEWAY_LOCAL = new QName(SCA_NS, "transactedOneWay.local");
    private static final QName TRANSACTED_ONEWAY_GLOBAL = new QName(SCA_NS, "transactedOneWay.global");
    private static final QName ONEWAY = new QName(SCA_NS, "oneWay");

    private PayloadTypeIntrospector introspector;

    public JmsBindingGenerator(@Reference PayloadTypeIntrospector introspector) {
        this.introspector = introspector;
    }

    public JmsWireSourceDefinition generateWireSource(LogicalBinding<JmsBindingDefinition> logicalBinding, Policy policy, ServiceDefinition serviceDefinition)
            throws GenerationException {

        ServiceContract<?> serviceContract = serviceDefinition.getServiceContract();
        TransactionType transactionType = getTransactionType(policy, serviceContract);
        Set<String> oneWayOperations = getOneWayOperations(policy, serviceContract);

        URI classloaderId = logicalBinding.getParent().getParent().getClassLoaderId();

        JmsBindingMetadata metadata = logicalBinding.getBinding().getMetadata();
        Map<String, PayloadType> payloadTypes = processPayloadTypes(serviceContract);
        URI uri = logicalBinding.getBinding().getTargetUri();
        return new JmsWireSourceDefinition(uri, metadata, payloadTypes, transactionType, oneWayOperations, classloaderId);
    }

    public JmsWireTargetDefinition generateWireTarget(LogicalBinding<JmsBindingDefinition> logicalBinding, Policy policy, ReferenceDefinition referenceDefinition)
            throws GenerationException {

        ServiceContract<?> serviceContract = referenceDefinition.getServiceContract();

        TransactionType transactionType = getTransactionType(policy, serviceContract);
        Set<String> oneWayOperations = getOneWayOperations(policy, serviceContract);

        URI classloaderId = logicalBinding.getParent().getParent().getClassLoaderId();

        URI uri = logicalBinding.getBinding().getTargetUri();
        JmsBindingMetadata metadata = logicalBinding.getBinding().getMetadata();
        Map<String, PayloadType> payloadTypes = processPayloadTypes(serviceContract);
        return new JmsWireTargetDefinition(uri, metadata, payloadTypes, transactionType, oneWayOperations, classloaderId);
    }

    /*
     * Gets the transaction type.
     */
    private TransactionType getTransactionType(Policy policy, ServiceContract<?> serviceContract) {

        // If any operation has the intent, return that
        for (Operation<?> operation : serviceContract.getOperations()) {
            for (Intent intent : policy.getProvidedIntents(operation)) {
                if (TRANSACTED_ONEWAY_GLOBAL.equals(intent.getName())) {
                    return TransactionType.GLOBAL;
                } else if (TRANSACTED_ONEWAY_LOCAL.equals(intent.getName())) {
                    return TransactionType.LOCAL;
                } else if (TRANSACTED_ONEWAY.equals(intent.getName())) {
                    return TransactionType.GLOBAL;
                }
            }
        }
        // no transaction policy specified, use local
        return TransactionType.LOCAL;

    }

    /*
     * Gets one way method names.
     */
    private Set<String> getOneWayOperations(Policy policy, ServiceContract<?> serviceContract) {
        Set<String> result = null;
        // If any operation has the intent, return that
        for (Operation<?> operation : serviceContract.getOperations()) {
            for (Intent intent : policy.getProvidedIntents(operation)) {
                if (ONEWAY.equals(intent.getName())) {
                    if (result == null) {
                        result = new HashSet<String>();
                    }
                    result.add(operation.getName());
                    break;
                }
            }
        }
        if (result != null) {
            return result;
        } else {
            return Collections.emptySet();
        }
    }

    /**
     * Determines the the payload type to use based on the service contract.
     * 
     * @param serviceContract the service contract
     * @return the collection of payload types keyed by operation name
     * @throws JmsGenerationException if an error occurs
     */
    private Map<String, PayloadType> processPayloadTypes(ServiceContract<?> serviceContract) throws JmsGenerationException {
        Map<String, PayloadType> types = new HashMap<String, PayloadType>();
        for (Operation<?> operation : serviceContract.getOperations()) {
            PayloadType payloadType = introspector.introspect(operation);
            types.put(operation.getName(), payloadType);
        }
        return types;
    }
}
