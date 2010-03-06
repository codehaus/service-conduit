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
package org.sca4j.fabric.generator.wire;

import java.net.URI;

import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalWireDefinition;

/**
 * Generates physical wire definitions to provision a logical wire. The number of physical wires generated from a logical wire will vary. A
 * unidirectional wire (i.e. no callback) to a collocated target service will generate one physical wire. A bidirectional wire (i.e. with a callback)
 * to a collocated service will generate two physical wires. A unidirecitonal wire to a remote service offered by an SCA component will generate two
 * physical wires:
 * <pre>
 * <ul>
 * <li>One from the source reference to the transport
 * <li>One from the transport on the target runtime to the target service.
 * </ul>
 * </pre>
 * Likewise, a bidirectional wire to a remote service offered by an SCA component will generate four wires:
 * <pre>
 * <ul>
 * <li>One from the source reference to the transport
 * <li>One from the transport on the target runtime to the target service.
 * <li>One from the callback site on the target to the transport
 * <li>One from the transport on the source runtime to the callback service.
 * </ul>
 * </pre>
 *
 * @version $Revision$ $Date$
 */
public interface PhysicalWireGenerator {

    /**
     * Generates the physical wires for the resources in this component.
     *
     * @param source   the source component.
     * @param resource the resource definition.
     * @return the physical wire definition.
     * @throws GenerationException if an error ocurrs during generation
     */
    <C extends LogicalComponent<?>> PhysicalWireDefinition generateResourceWire(C source, LogicalResource<?> resource) throws GenerationException;


    /**
     * Generates a PhysicalWireDefinition from a bound service to a component. A physical change set for the runtime the wire will be provisioned to
     * is updated with the physical wire definition
     *
     * @param service     the logical service representing the wire source
     * @param binding     the binding the wire will be attached to at its source
     * @param target      the target lgical component for the wire
     * @param callbackUri the callback URI associated with this wire or null if the service is unidirectional
     * @return the physical wire definition.
     * @throws GenerationException if an error ocurrs during generation
     */
    <C extends LogicalComponent<?>> PhysicalWireDefinition generateBoundServiceWire(LogicalService service,
                                                                                    LogicalBinding<?> binding,
                                                                                    C target,
                                                                                    URI callbackUri) throws GenerationException;


    /**
     * Generates a PhysicalWireDefinition from a bound service to a component. A physical change set for the runtime the wire will be provisioned to
     * is updated with the physical wire definition
     *
     * @param source    the source logical component for the wire
     * @param reference the component reference the wire is associated with to at its source
     * @param binding   the binding the wire will be attached to at its terminating end
     * @return the physical wire definition.
     * @throws GenerationException if an error ocurrs during generation
     */
    <C extends LogicalComponent<?>> PhysicalWireDefinition generateBoundReferenceWire(C source,
                                                                                      LogicalReference reference,
                                                                                      LogicalBinding<?> binding) throws GenerationException;

    /**
     * Generates a PhysicalWireDefinition for an unbound wire. Unbound wires are direct connections between two components. A physical change set for
     * the runtime the wire will be provisioned to is updated with the physical wire definition
     *
     * @param source    the source component the wire will be attached to
     * @param reference the component reference the wire is associated with at its source
     * @param service   the component service the wire is associated with to at its terminating end
     * @param target    the target component the wire will be attached to
     * @return the physical wire definition.
     * @throws GenerationException if an error ocurrs during generation
     */
    <S extends LogicalComponent<?>, T extends LogicalComponent<?>> PhysicalWireDefinition generateUnboundWire(S source,
                                                                                                              LogicalReference reference,
                                                                                                              LogicalService service,
                                                                                                              T target)
            throws GenerationException;

    /**
     * Generates an unbound callback wire between two collocated components.
     *
     * @param source    the source component, which is the target of the forward wire
     * @param reference the reference the forward wire is injected on
     * @param target    the target component, which is the source of the forward wire
     * @return the physical wire definition.
     * @throws GenerationException if an error ocurrs during generation
     * @FIXME JFM passing in the LogicalReference doesn't seem right but the policy generation appears to need it. Look to remove.
     */
    public <S extends LogicalComponent<?>, T extends LogicalComponent<?>> PhysicalWireDefinition generateUnboundCallbackWire(S source,
                                                                                                                             LogicalReference reference,
                                                                                                                             T target)
            throws GenerationException;

    /**
     * Generates a callback wire from a reference to the callback service offered by the client component
     *
     * @param reference the logical reference which is the callback wire source
     * @param binding   the callback binding
     * @param component the client component which originates an invocation over the forward wire associated with the callback wire to be generated.
     * @return the physical wire definition.
     * @throws GenerationException if an error ocurrs during generation
     */
    <C extends LogicalComponent<?>> PhysicalWireDefinition generateBoundCallbackRerenceWire(LogicalReference reference,
                                                                                            LogicalBinding<?> binding,
                                                                                            C component) throws GenerationException;

    /**
     * Generates a callback wire from a component to the callback service provided by a forward service
     *
     * @param component the client component which was the target of the forward wire and source of the callback
     * @param service   the logical service which provides the callback service
     * @param binding   the callback binding
     * @return the physical wire definition.
     * @throws GenerationException if an error ocurrs during generation
     */
    public <C extends LogicalComponent<?>> PhysicalWireDefinition generateBoundCallbackServiceWire(C component,
                                                                                                   LogicalService service,
                                                                                                   LogicalBinding<?> binding)
            throws GenerationException;

}
