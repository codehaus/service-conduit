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
package org.sca4j.fabric.policy;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.fabric.policy.helper.ImplementationPolicyHelper;
import org.sca4j.fabric.policy.helper.InteractionPolicyHelper;
import org.sca4j.fabric.policy.infoset.PolicyInfosetBuilder;
import org.sca4j.host.Namespaces;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicyPhase;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.policy.Policy;
import org.sca4j.spi.policy.PolicyResolutionException;
import org.sca4j.spi.policy.PolicyResolver;
import org.sca4j.spi.policy.PolicyResult;
import org.sca4j.util.closure.Closure;
import org.sca4j.util.closure.CollectionUtils;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public class DefaultPolicyResolver implements PolicyResolver {
    private static final QName IMPLEMENTATION_SYSTEM = new QName(Namespaces.SCA4J_NS, "implementation.system");
    private static final QName IMPLEMENTATION_SINGLETON = new QName(Namespaces.SCA4J_NS, "singleton");

    /**
     * Closure for filtering intercepted policies.
     */
    private static final Closure<PolicySet, Boolean> INTERCEPTION = new Closure<PolicySet, Boolean>() {
        public Boolean execute(PolicySet policySet) {
            return policySet.getPhase() == PolicyPhase.INTERCEPTION;
        }
    };

    /**
     * Closure for filtering provided policies by bindings or implementations.
     */
    private static final Closure<PolicySet, Boolean> PROVIDED = new Closure<PolicySet, Boolean>() {
        public Boolean execute(PolicySet policySet) {
            return policySet.getPhase() == PolicyPhase.PROVIDED;
        }
    };

    private static final PolicyResult RESULT = new PolicyResult() {

        public List<PolicySet> getInterceptedPolicySets(Operation<?> operation) {
            return Collections.emptyList();
        }

        public Policy getSourcePolicy() {
            return new Policy() {
                public List<Intent> getProvidedIntents(Operation<?> operation) {
                    return Collections.emptyList();
                }

                public List<PolicySet> getProvidedPolicySets(Operation<?> operation) {
                    return Collections.emptyList();
                }
            };
        }

        public Policy getTargetPolicy() {
            return new Policy() {
                public List<Intent> getProvidedIntents(Operation<?> operation) {
                    return Collections.emptyList();
                }

                public List<PolicySet> getProvidedPolicySets(Operation<?> operation) {
                    return Collections.emptyList();
                }
            };
        }

    };

    private final InteractionPolicyHelper interactionPolicyHelper;
    private final ImplementationPolicyHelper implementationPolicyHelper;
    private final PolicyInfosetBuilder policyInfosetBuilder;

    public DefaultPolicyResolver(@Reference InteractionPolicyHelper interactionPolicyHelper,
                                 @Reference ImplementationPolicyHelper implementationPolicyHelper,
                                 @Reference PolicyInfosetBuilder policyInfosetBuilder) {
        this.interactionPolicyHelper = interactionPolicyHelper;
        this.implementationPolicyHelper = implementationPolicyHelper;
        this.policyInfosetBuilder = policyInfosetBuilder;
    }

    /**
     * Resolves all the interaction and implementation intents for the wire.
     *
     * @param serviceContract Service contract for the wire.
     * @param sourceBinding   Source binding.
     * @param targetBinding   Target binding.
     * @param source          Source component.
     * @param target          Target component.
     * @return Policy resolution result.
     * @throws PolicyResolutionException If unable to resolve any policies.
     */
    public PolicyResult resolvePolicies(ServiceContract serviceContract,
                                        LogicalBinding<?> sourceBinding,
                                        LogicalBinding<?> targetBinding,
                                        LogicalComponent<?> source,
                                        LogicalComponent<?> target) throws PolicyResolutionException {
        if (noPolicy(source) && noPolicy(target)) {
            return RESULT;
        }
        PolicyResultImpl policyResult = new PolicyResultImpl();

        for (Operation<?> operation : serviceContract.getOperations()) {

            policyResult.addSourceIntents(operation, interactionPolicyHelper.getProvidedIntents(sourceBinding, operation));
            
            // Don't do policy resolution on souce components for implementation intents
            // if (source != null) {
            //    policyResult.addSourceIntents(operation, implementationPolicyHelper.getProvidedIntents(source, operation));
            //}

            policyResult.addTargetIntents(operation, interactionPolicyHelper.getProvidedIntents(targetBinding, operation));
            if (target != null) {
                policyResult.addSourceIntents(operation, implementationPolicyHelper.getProvidedIntents(target, operation));
            }

            List<PolicySet> policies;
            Element policyInfoset;

            // Don't do policy resolution on souce components for implementation intents
            //if (source != null) {
            //    policyInfoset = policyInfosetBuilder.buildInfoSet(source);
            //    policies = implementationPolicyHelper.resolveIntents(source, operation, policyInfoset);
            //    policyResult.addSourcePolicySets(operation, CollectionUtils.filter(policies, PROVIDED));
            //    policyResult.addInterceptedPolicySets(operation, CollectionUtils.filter(policies, INTERCEPTION));
            //}

            policyInfoset = policyInfosetBuilder.buildInfoSet(sourceBinding);
            policies = interactionPolicyHelper.resolveIntents(sourceBinding, operation, policyInfoset);
            policyResult.addSourcePolicySets(operation, CollectionUtils.filter(policies, PROVIDED));
            policyResult.addInterceptedPolicySets(operation, CollectionUtils.filter(policies, INTERCEPTION));

            policyInfoset = policyInfosetBuilder.buildInfoSet(targetBinding);
            policies = interactionPolicyHelper.resolveIntents(targetBinding, operation, policyInfoset);
            policyResult.addTargetPolicySets(operation, CollectionUtils.filter(policies, PROVIDED));
            policyResult.addInterceptedPolicySets(operation, CollectionUtils.filter(policies, INTERCEPTION));

            if (target != null) {
                policyInfoset = policyInfosetBuilder.buildInfoSet(target);
                policies = implementationPolicyHelper.resolveIntents(target, operation, policyInfoset);
                policyResult.addTargetPolicySets(operation, CollectionUtils.filter(policies, PROVIDED));
                policyResult.addInterceptedPolicySets(operation, CollectionUtils.filter(policies, INTERCEPTION));
            }

        }                       

        return policyResult;

    }

    private boolean noPolicy(LogicalComponent<?> component) {
        return component != null && (component.getDefinition().getImplementation().isType(IMPLEMENTATION_SYSTEM)
                || component.getDefinition().getImplementation().isType(IMPLEMENTATION_SINGLETON));
    }

}
