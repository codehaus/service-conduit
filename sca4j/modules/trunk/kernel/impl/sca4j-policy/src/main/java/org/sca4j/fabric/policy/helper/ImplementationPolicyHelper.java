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
package org.sca4j.fabric.policy.helper;

import java.util.List;

import org.sca4j.scdl.Operation;
import org.sca4j.scdl.definitions.Intent;
import org.sca4j.scdl.definitions.PolicySet;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.policy.PolicyResolutionException;
import org.w3c.dom.Element;

/**
 * @version $Revision$ $Date$
 */
public interface ImplementationPolicyHelper {
    
    /**
     * Returns the set of intents that need to be explictly provided by the implementation. These 
     * are the intents requested by the use and available in the <code>mayProvide</code> list of intents 
     * declared by the implementation type.
     * 
     * @param logicalComponent Logical component for which intents are to be resolved.
     * @param operation Operation for which the provided intents are to be computed.
     * @return Set of intents that need to be explictly provided by the implementation.
     * @throws PolicyResolutionException If there are any unidentified intents.
     */
    List<Intent> getProvidedIntents(LogicalComponent<?> logicalComponent, Operation<?> operation) 
    throws PolicyResolutionException;
    
    /**
     * Returns the set of policies that will address the intents that are not provided by the implementation type.
     * 
     * @param logicalComponent Logical component for which policies are to be resolved.
     * @param operation Operation for which the provided intents are to be computed.
     * @return Set of resolved policies.
     * @throws PolicyResolutionException If all intents cannot be resolved.
     */
    List<PolicySet> resolveIntents(LogicalComponent<?> logicalComponent, Operation<?> operation, Element target) throws PolicyResolutionException;

}
