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
package org.sca4j.fabric.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final Map<Operation<?>, List<PolicySet>> interceptedPolicySets = new HashMap<Operation<?>, List<PolicySet>>();

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
    public List<PolicySet> getInterceptedPolicySets(Operation<?> operation) {
        return interceptedPolicySets.get(operation);
    }

    /**
     * Adds an intent that is requested on the operation and provided by the source component implementation or binding type.
     *
     * @param operation Operation against which the intent was requested.
     * @param intents   Intents that are provided.
     */
    void addSourceIntents(Operation<?> operation, Set<Intent> intents) {
        sourcePolicy.addIntents(operation, intents);
    }

    /**
     * Adds an intent that is requested on the operation and provided by the target component implementation or binding type.
     *
     * @param operation Operation against which the intent was requested.
     * @param intents   Intents that are provided.
     */
    void addTargetIntents(Operation<?> operation, Set<Intent> intents) {
        targetPolicy.addIntents(operation, intents);
    }

    /**
     * Adds a policy set mapped to the inetnt that is requested on the operation and provided by the source component implementation or binding type.
     *
     * @param operation  Operation against which the intent was requested.
     * @param policySets Resolved policy sets.
     */
    void addSourcePolicySets(Operation<?> operation, Set<PolicySet> policySets) {
        sourcePolicy.addPolicySets(operation, policySets);
    }

    /**
     * Adds a policy set mapped to the inetnt that is requested on the operation and provided by the target component implementation or binding type.
     *
     * @param operation  Operation against which the intent was requested.
     * @param policySets Resolved policy sets.
     */
    void addTargetPolicySets(Operation<?> operation, Set<PolicySet> policySets) {
        targetPolicy.addPolicySets(operation, policySets);
    }

    /**
     * Adds a policy set mapped to the intent that is requested on the operation and is implemented as an interceptor.
     *
     * @param operation  Operation against which the intent was requested.
     * @param policySets Resolved policy sets.
     */
    void addInterceptedPolicySets(Operation<?> operation, Set<PolicySet> policySets) {

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
