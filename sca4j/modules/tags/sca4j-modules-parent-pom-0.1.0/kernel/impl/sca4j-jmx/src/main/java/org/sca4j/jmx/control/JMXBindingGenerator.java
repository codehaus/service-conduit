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
package org.sca4j.jmx.control;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.jmx.provision.JMXWireSourceDefinition;
import org.sca4j.jmx.scdl.JMXBinding;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.Bindable;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * @version $Rev: 5257 $ $Date: 2008-08-23 21:35:57 +0100 (Sat, 23 Aug 2008) $
 */
@EagerInit
public class JMXBindingGenerator implements BindingGenerator<JMXWireSourceDefinition, PhysicalWireTargetDefinition, JMXBinding> {

    public JMXWireSourceDefinition generateWireSource(LogicalBinding<JMXBinding> binding, Policy policy, ServiceDefinition serviceDefinition)
            throws GenerationException {
        Bindable logicalService = binding.getParent();
        LogicalComponent<?> logicalComponent = logicalService.getParent();

        JMXWireSourceDefinition definition = new JMXWireSourceDefinition();
        URI uri = binding.getUri();
        if (uri == null) {
            uri = logicalService.getUri();
        }
        definition.setUri(uri);
        definition.setClassLoaderId(logicalComponent.getClassLoaderId());
        definition.setInterfaceName(serviceDefinition.getServiceContract().getQualifiedInterfaceName());
        definition.setOptimizable(true);
        return definition;
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalBinding<JMXBinding> binding, Policy policy, ReferenceDefinition referenceDefinition)
            throws GenerationException {
        // TODO we might need this for notifications but leave it out for now
        throw new UnsupportedOperationException();
    }
}
