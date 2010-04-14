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
    public List<PolicySet> getInterceptedPolicySets(Operation operation);

}
