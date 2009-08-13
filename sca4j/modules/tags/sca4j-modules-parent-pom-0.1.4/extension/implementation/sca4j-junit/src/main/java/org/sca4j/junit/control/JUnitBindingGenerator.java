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
package org.sca4j.junit.control;

import org.osoa.sca.annotations.EagerInit;
import org.sca4j.junit.provision.JUnitWireSourceDefinition;
import org.sca4j.junit.scdl.JUnitBindingDefinition;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * 
 */
@EagerInit
public class JUnitBindingGenerator implements BindingGenerator<JUnitWireSourceDefinition, PhysicalWireTargetDefinition, JUnitBindingDefinition> {

	public JUnitWireSourceDefinition generateWireSource(LogicalBinding<JUnitBindingDefinition> bindingDefinition,Policy policy, 
			                                            ServiceDefinition serviceDefinition) throws GenerationException {
		
		ComponentDefinition<?> definition = bindingDefinition.getParent().getParent().getDefinition();
		String testName = definition.getName();
		return new JUnitWireSourceDefinition(testName);
	}

	
	public PhysicalWireTargetDefinition generateWireTarget(LogicalBinding<JUnitBindingDefinition> bindingDefinition, 
			                                               Policy policy, ReferenceDefinition referenceDefinition) throws GenerationException {
		throw new UnsupportedOperationException();
	}
}
