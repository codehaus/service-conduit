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
package org.sca4j.fabric.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sca4j.scdl.Operation;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.policy.Policy;
import org.sca4j.spi.policy.PolicyResult;

/**
 * @version $Revision$ $Date$
 */
public class PolicyResultImpl implements PolicyResult {

    private final PolicyImpl sourcePolicy = new PolicyImpl();
    private final PolicyImpl targetPolicy = new PolicyImpl();

    private final Map<Operation, List<PolicySet>> interceptedPolicySets = new HashMap<Operation, List<PolicySet>>();

    /**
     * @return Policies and intents provided at the source end.
     */
    public Policy getSourcePolicy() {
        return sourcePolicy;
    }

    /**
     * @return Policies and intents provided at the target end.
     */
    public Policy getTargetPolicy() {
        return targetPolicy;
    }

    /**
     * Gets all the policy sets that are implemented as interceptors that were resolved against the intents requested against the operation.
     *
     * @param operation Operation against which the intent was requested.
     * @return Resolved policy sets.
     */
    public List<PolicySet> getInterceptedPolicySets(Operation operation) {
        return interceptedPolicySets.get(operation);
    }

    /**
     * Adds an intent that is requested on the operation and provided by the source component implementation or binding type.
     *
     * @param operation Operation against which the intent was requested.
     * @param intents   Intents that are provided.
     */
    void addSourceIntents(Operation operation, List<Intent> intents) {
        sourcePolicy.addIntents(operation, intents);
    }

    /**
     * Adds an intent that is requested on the operation and provided by the target component implementation or binding type.
     *
     * @param operation Operation against which the intent was requested.
     * @param intents   Intents that are provided.
     */
    void addTargetIntents(Operation operation, List<Intent> intents) {
        targetPolicy.addIntents(operation, intents);
    }

    /**
     * Adds a policy set mapped to the inetnt that is requested on the operation and provided by the source component implementation or binding type.
     *
     * @param operation  Operation against which the intent was requested.
     * @param policySets Resolved policy sets.
     */
    void addSourcePolicySets(Operation operation, List<PolicySet> policySets) {
        sourcePolicy.addPolicySets(operation, policySets);
    }

    /**
     * Adds a policy set mapped to the inetnt that is requested on the operation and provided by the target component implementation or binding type.
     *
     * @param operation  Operation against which the intent was requested.
     * @param policySets Resolved policy sets.
     */
    void addTargetPolicySets(Operation operation, List<PolicySet> policySets) {
        targetPolicy.addPolicySets(operation, policySets);
    }

    /**
     * Adds a policy set mapped to the intent that is requested on the operation and is implemented as an interceptor.
     *
     * @param operation  Operation against which the intent was requested.
     * @param policySets Resolved policy sets.
     */
    void addInterceptedPolicySets(Operation operation, List<PolicySet> policySets) {

        if (!interceptedPolicySets.containsKey(operation)) {
            interceptedPolicySets.put(operation, new ArrayList<PolicySet>());
        }

        List<PolicySet> interceptedSets = interceptedPolicySets.get(operation);
        for (PolicySet policySet : policySets) {
            if (!interceptedSets.contains(policySet)) {
                // Check to see if the policy set has already been added. This can happen for intents specified on service contracts, as they will
                // be picked up on both the reference and service sides of a wire.
                interceptedSets.addAll(policySets);
            }
        }

    }

}
