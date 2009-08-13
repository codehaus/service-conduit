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

/**
 * 
 * @version $Revision$ $Date$
 */
public class PolicyImpl implements Policy {
    
    private final Map<Operation<?>, List<Intent>> intentMap = new HashMap<Operation<?>, List<Intent>>();
    private final Map<Operation<?>, List<PolicySet>> policySetMap = new HashMap<Operation<?>, List<PolicySet>>();
    
    /**
     * Gets the intents that are provided by the component or binding types 
     * that are requested by the operation.
     * 
     * @param operation Operation against which the intent was requested.
     * @return All intents that are provided.
     */
    public List<Intent> getProvidedIntents(Operation<?> operation) {
        return intentMap.get(operation);
    }
    
    /**
     * Gets all the policy sets that are provided by the component 
     * implementation or binding type that were resolved against the intents 
     * requested against the operation.
     * 
     * @param operation Operation against which the intent was requested.
     * @return Resolved policy sets.
     */
    public List<PolicySet> getProvidedPolicySets(Operation<?> operation) {
        return policySetMap.get(operation);
    }
    
    /**
     * Adds an intent that is requested on the operation and provided by the  
     * component implementation or binding type.
     * 
     * @param operation Operation against which the intent was requested.
     * @param intents Intents that are provided.
     */
    void addIntents(Operation<?> operation, Set<Intent> intents) {
        
        if (!intentMap.containsKey(operation)) {
            intentMap.put(operation, new ArrayList<Intent>());
        }
        
        intentMap.get(operation).addAll(intents);
        
    }
    
    /**
     * Adds a policy set mapped to the intent that is requested on the operation 
     * and provided by the component implementation or binding type.
     * 
     * @param operation Operation against which the intent was requested.
     * @param policySets Resolved policy sets.
     */
    void addPolicySets(Operation<?> operation, Set<PolicySet> policySets) {
        
        if (!policySetMap.containsKey(operation)) {
            policySetMap.put(operation, new ArrayList<PolicySet>());
        }
        
        policySetMap.get(operation).addAll(policySets);
    }

}
