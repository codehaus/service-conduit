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
package org.sca4j.timer.component.control;

import javax.xml.namespace.QName;

import org.osoa.sca.Constants;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

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
import org.sca4j.timer.component.provision.TimerComponentDefinition;
import org.sca4j.timer.component.provision.TriggerData;
import org.sca4j.timer.component.scdl.TimerImplementation;

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
        physical.setTransactional(implementation.getIntents().contains(MANAGED_TRANSACTION));
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
                                                                   ServiceContract<?> serviceContract,
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
