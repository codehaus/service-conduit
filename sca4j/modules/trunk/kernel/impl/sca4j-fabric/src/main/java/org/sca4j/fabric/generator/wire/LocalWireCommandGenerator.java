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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.fabric.generator.wire;

import java.net.URI;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.sca4j.fabric.command.AttachWireCommand;
import org.sca4j.fabric.runtime.ComponentNames;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.spi.generator.AddCommandGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.instance.LogicalWire;
import org.sca4j.spi.model.physical.PhysicalWireDefinition;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.util.UriHelper;

/**
 * Generate commands to attach local wires between components.
 *
 * @version $Revision$ $Date$
 */
public class LocalWireCommandGenerator implements AddCommandGenerator {

    private PhysicalWireGenerator physicalWireGenerator;
    private LogicalComponentManager applicationLCM;
    private LogicalComponentManager runtimeLCM;
    private int order;

    /**
     * Constructor used during bootstrap.
     *
     * @param physicalWireGenerator the bootstrap physical wire generator
     * @param runtimeLCM            the bootstrap LogicalComponentManager
     * @param order                 the order value for commands generated
     */
    public LocalWireCommandGenerator(PhysicalWireGenerator physicalWireGenerator, LogicalComponentManager runtimeLCM, int order) {
        this.physicalWireGenerator = physicalWireGenerator;
        this.runtimeLCM = runtimeLCM;
        this.order = order;
    }

    /**
     * Constructor used for instantiation after bootstrap. After bootstrap on a controller instance, two domains will be active: the runtime domain
     * containing system components and the application domain containing end-user components. On runtime nodes, the application domain may not be
     * active, in which case a null value may be injected.
     *
     * @param physicalWireGenerator the bootstrap physical wire generator
     * @param runtimeLCM            the LogicalComponentManager associated with the runtime domain
     * @param applicationLCM        the LogicalComponentManager associated with the application domain
     * @param order                 the order value for commands generated
     */
    @Constructor
    public LocalWireCommandGenerator(@Reference PhysicalWireGenerator physicalWireGenerator,
                                     @Reference(name = "runtimeLCM")LogicalComponentManager runtimeLCM,
                                     @Reference(name = "applicationLCM")LogicalComponentManager applicationLCM,
                                     @Property(name = "order")int order) {
        this.physicalWireGenerator = physicalWireGenerator;
        this.runtimeLCM = runtimeLCM;
        this.applicationLCM = applicationLCM;
        this.order = order;
    }

    public AttachWireCommand generate(LogicalComponent<?> component) throws GenerationException {
        if (component instanceof LogicalCompositeComponent) {
            return null;
        }
        AttachWireCommand command = new AttachWireCommand(order);
        generatePhysicalWires(component, command);
        return command;
    }

    private void generatePhysicalWires(LogicalComponent<?> component, AttachWireCommand command) throws GenerationException {

        for (LogicalReference logicalReference : component.getReferences()) {
            if (logicalReference.getBindings().isEmpty()) {
                generateUnboundReferenceWires(logicalReference, command);
            }
        }
    }

    private void generateUnboundReferenceWires(LogicalReference logicalReference, AttachWireCommand command) throws GenerationException {

        LogicalComponent<?> component = logicalReference.getParent();

        for (LogicalWire logicalWire : logicalReference.getWires()) {

            if (logicalWire.isProvisioned()) {
                continue;
            }

            URI uri = logicalWire.getTargetUri();
            String serviceName = uri.getFragment();
            LogicalComponent<?> target;
            if (uri.toString().startsWith(ComponentNames.RUNTIME_NAME)) {
                target = runtimeLCM.getComponent(uri);
            } else {
                target = applicationLCM.getComponent(uri);
            }
            assert target != null;
            LogicalService targetService = target.getService(serviceName);

            assert targetService != null;
            while (CompositeImplementation.class.isInstance(target.getDefinition().getImplementation())) {
                LogicalCompositeComponent composite = (LogicalCompositeComponent) target;
                URI promoteUri = targetService.getPromotedUri();
                URI promotedComponent = UriHelper.getDefragmentedName(promoteUri);
                target = composite.getComponent(promotedComponent);
                targetService = target.getService(promoteUri.getFragment());
            }

            LogicalReference reference = logicalWire.getSource();
            PhysicalWireDefinition pwd = physicalWireGenerator.generateUnboundWire(component, reference, targetService, target);
            command.addPhysicalWireDefinition(pwd);

            // generate physical callback wires if the forward service is bidirectional
            if (reference.getDefinition().getServiceContract().getCallbackContract() != null) {
                pwd = physicalWireGenerator.generateUnboundCallbackWire(target, reference, component);
                command.addPhysicalWireDefinition(pwd);
            }

            logicalWire.setProvisioned(true);

        }

    }

    public int getOrder() {
        return order;
    }

}
