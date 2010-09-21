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
package org.sca4j.timer.quartz.control;

import javax.xml.namespace.QName;

import org.oasisopen.sca.Constants;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.java.control.JavaGenerationHelper;
import org.sca4j.java.provision.JavaWireSourceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;
import org.sca4j.timer.quartz.provision.TimerComponentDefinition;
import org.sca4j.timer.quartz.provision.TriggerData;
import org.sca4j.timer.quartz.scdl.TimerImplementation;

/**
 * Generates a TimerComponentDefinition from a ComponentDefinition corresponding to a timer component implementation
 *
 * @version $Rev: 4833 $ $Date: 2008-06-20 03:41:57 -0700 (Fri, 20 Jun 2008) $
 */
@EagerInit
public class TimerComponentGenerator implements ComponentGenerator<LogicalComponent<TimerImplementation>> {
    private static final QName MANAGED_TRANSACTION = new QName(Constants.SCA_NS, "managedTransaction");
    private final GeneratorRegistry registry;
    private JavaGenerationHelper generationHelper;

    public TimerComponentGenerator(@Reference GeneratorRegistry registry, @Reference JavaGenerationHelper generationHelper) {
        this.registry = registry;
        this.generationHelper = generationHelper;
    }

    @Init
    public void init() {
        registry.register(TimerImplementation.class, this);
    }

    public PhysicalComponentDefinition generate(LogicalComponent<TimerImplementation> component) throws GenerationException {
        TimerComponentDefinition physical = new TimerComponentDefinition();
        generationHelper.generate(component, physical);
        TimerImplementation implementation = component.getDefinition().getImplementation();
        boolean isTransactional = component.getDefinition().getIntents().contains(MANAGED_TRANSACTION) || 
                                  implementation.getIntents().contains(MANAGED_TRANSACTION);
        physical.setTransactional(isTransactional);
        TriggerData data = implementation.getTriggerData();
        physical.setTriggerData(data);
        return physical;
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<TimerImplementation> source, LogicalReference reference, Policy policy)
            throws GenerationException {
        JavaWireSourceDefinition wireDefinition = new JavaWireSourceDefinition();
        return generationHelper.generateWireSource(source, wireDefinition, reference, policy);
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<TimerImplementation> source,
                                                                   ServiceContract serviceContract,
                                                                   Policy policy) throws GenerationException {
        JavaWireSourceDefinition wireDefinition = new JavaWireSourceDefinition();
        return generationHelper.generateCallbackWireSource(source, wireDefinition, serviceContract, policy);
    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<TimerImplementation> source, LogicalResource<?> resource)
            throws GenerationException {
        JavaWireSourceDefinition wireDefinition = new JavaWireSourceDefinition();
        return generationHelper.generateResourceWireSource(source, resource, wireDefinition);
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service, LogicalComponent<TimerImplementation> target, Policy policy)
            throws GenerationException {
        throw new UnsupportedOperationException("Cannot wire to timer components");
    }
}
