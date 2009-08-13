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
package org.sca4j.spi.policy;

import java.util.List;

import org.sca4j.scdl.Operation;
import org.sca4j.scdl.definitions.PolicySet;

/**
 * Result of resolving intents and policy sets on a wire. The policies are resolved for 
 * the source and target bindings as well as the source and target component types. A wire 
 * can be between two components or between a component and a binding. 
 * 
 * For a wire between two components, the result will include,
 * 
 * 1. Implementation intents that are requested for each operation on the source side and may be 
 * provided by the source component implementation type.
 * 2. Implementation intents that are requested for each operation on the target side and may be 
 * provided by the target component implementation type.
 * 3. Policy sets that map to implementation intents on each operation on the source side and 
 * understood by the source component implementation type.
 * 4. Policy sets that map to implementation intents on each operation on the target side and 
 * understood by the target component implementation type.
 * 5. Policy sets that map to implementation intents on each operation on the source and target 
 * side that are implemented using interceptors.
 * 
 * For a wire between a binding and a component (service binding), the result will include
 * 
 * 1. Interaction intents that are requested for each operation and may be provided by the 
 * service binding type.
 * 2. Implementation intents that are requested for each operation and may be  provided by 
 * the target component implementation type.
 * 3. Policy sets that map to implementation intents on each operation and understood by the 
 * component implementation type.
 * 4. Policy sets that map to interaction intents on each operation on the source side and 
 * understood by the service binding type.
 * 5. Policy sets that map to implementation and interaction intents on each operation that 
 * are implemented using interceptors.
 * 
 * For a wire between a component and a binding (reference binding), the result will include
 * 
 * 1. Interaction intents that are requested for each operation and may be provided by the 
 * reference binding type.
 * 2. Implementation intents that are requested for each operation and may be provided by the 
 * component implementation type.
 * 3. Policy sets that map to implementation intents on each operation and understood by the 
 * component implementation type.
 * 4. Policy sets that map to interaction intents on each operation and understood by the 
 * service binding type.
 * 5. Policy sets that map to implementation and interaction intents on each operation that 
 * are implemented using interceptors.
 * 
 * @version $Revision$ $Date$
 */
public interface PolicyResult {
    
    /**
     * @return Policies and intents provided at the source end.
     */
    public Policy getSourcePolicy();
    
    /**
     * @return Policies and intents provided at the target end.
     */
    public Policy getTargetPolicy();
    
    /**
     * @param operation Operation against which interceptors are defined.
     * @return Interceptors that are defined against the operation.
     */
    public List<PolicySet> getInterceptedPolicySets(Operation<?> operation);

}
