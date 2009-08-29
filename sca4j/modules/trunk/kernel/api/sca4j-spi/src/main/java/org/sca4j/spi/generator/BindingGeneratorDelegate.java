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
package org.sca4j.spi.generator;

import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * Delegate for implementing stack specific behavior for the binding.
 *
 * @version $Rev: 1567 $ $Date: 2007-10-20 11:34:49 +0100 (Sat, 20 Oct 2007) $
 */
public interface BindingGeneratorDelegate<BD extends BindingDefinition> {

    /**
     * Generates a physical wire source definition from a logical binding.
     *
     * @param binding Logical binding.
     * @param intentsToBeProvided Intents to be provided explictly by the binding.
     * @param serviceDefinition Service definition for the target.
     * @return Physical wire source definition.
     * @throws GenerationException
     */
    PhysicalWireSourceDefinition generateWireSource(LogicalBinding<BD> binding, Policy policy, ServiceDefinition serviceDefinition) 
        throws GenerationException;

    /**
     * Generates a physical wire target definition from a logical binding.
     *
     * @param binding Logical binding.
     * @param intentsToBeProvided Intents to be provided explictly by the binding.
     * @param referenceDefinition Reference definition for the target.
     * @return Physical wire target definition.
     * @throws GenerationException
     */
    PhysicalWireTargetDefinition generateWireTarget(LogicalBinding<BD> binding, Policy policy, ReferenceDefinition referenceDefinition) 
        throws GenerationException;

}
