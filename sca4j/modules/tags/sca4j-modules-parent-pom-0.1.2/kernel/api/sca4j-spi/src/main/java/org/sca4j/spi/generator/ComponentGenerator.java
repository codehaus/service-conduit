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
    PhysicalWireSourceDefinition generateCallbackWireSource(C source, ServiceContract<?> serviceContract, Policy policy)
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
