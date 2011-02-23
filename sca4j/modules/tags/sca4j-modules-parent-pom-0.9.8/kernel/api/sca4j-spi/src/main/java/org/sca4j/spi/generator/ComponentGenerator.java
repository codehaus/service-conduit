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

import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * Implementations are responsible for generating command metadata used to provision components to service nodes.
 *
 * @version $Rev: 2932 $ $Date: 2008-02-28 14:18:34 +0000 (Thu, 28 Feb 2008) $
 */
public interface ComponentGenerator<C extends LogicalComponent<? extends Implementation<?>>> {

    /**
     * Generates an {@link org.sca4j.spi.model.physical.PhysicalComponentDefinition} based on a {@link org.sca4j.scdl.ComponentDefinition}. The
     * resulting PhysicalComponentDefinition is added to the PhysicalChangeSet associated with the current GeneratorContext.
     *
     * @param component the logical component to evaluate
     * @return the physical component definition
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalComponentDefinition generate(C component) throws GenerationException;

    /**
     * Generates a {@link PhysicalWireSourceDefinition} used to attach a wire to a source component. Metadata contained in the
     * PhysicalWireSourceDefinition is specific to the component implementation type and used when the wire is attached to its source on a service
     * node.
     *
     * @param source    the logical component for the wire source
     * @param reference the source logical reference
     * @param policy    the provided intents and policy sets
     * @return the metadata used to attach the wire to its source on the service node
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalWireSourceDefinition generateWireSource(C source, LogicalReference reference, Policy policy) throws GenerationException;

    /**
     * Generates a {@link PhysicalWireSourceDefinition} used to attach a wire for a callback service to a source component. Metadata contained in the
     * PhysicalWireSourceDefinition is specific to the component implementation type and used when the wire is attached to its source on a service
     * node.
     *
     * @param source          the logical component for the wire source
     * @param serviceContract callback service contract
     * @param policy          the provided intents and policy sets
     * @return the metadata used to attach the wire to its source on the service node
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalWireSourceDefinition generateCallbackWireSource(C source, ServiceContract serviceContract, Policy policy)
            throws GenerationException;

    /**
     * Generates a {@link PhysicalWireTargetDefinition} used to attach a wire to a target component. Metadata contained in the
     * PhysicalWireSourceDefinition is specific to the component implementation type and used when the wire is attached to its target on a service
     * node.
     *
     * @param service the target logical service
     * @param target  the logical component for the wire target
     * @param policy  the provided intents and policy sets
     * @return the metadata used to attach the wire to its target on the service node
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalWireTargetDefinition generateWireTarget(LogicalService service, C target, Policy policy) throws GenerationException;

    /**
     * Generates a {@link PhysicalWireSourceDefinition} used to attach a resource to a source component. Metadata contained in the
     * PhysicalWireSourceDefinition is specific to the component implementation type and used when the wire is attached to its source on a service
     * node.
     *
     * @param source   the logical component for the resource
     * @param resource the source logical resource
     * @return the metadata used to attach the wire to its source on the service node
     * @throws GenerationException if an error occurs during the generation process
     */
    PhysicalWireSourceDefinition generateResourceWireSource(C source, LogicalResource<?> resource) throws GenerationException;

}
