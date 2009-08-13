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

import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * Generates {@link PhysicalWireSourceDefinition}s and {@link PhysicalWireTargetDefinition}s for a resolved binding
 *
 * @version $Rev: 2931 $ $Date: 2008-02-28 12:49:35 +0000 (Thu, 28 Feb 2008) $
 */
public interface BindingGenerator<PWSD extends PhysicalWireSourceDefinition, PWTD extends PhysicalWireTargetDefinition, BD extends BindingDefinition> {

    /**
     * Generates a physical wire source definition from a logical binding.
     *
     * @param binding Logical binding.
     * @param intentsToBeProvided Intents to be provided explictly by the binding.
     * @param serviceDefinition Service definition for the target.
     * @return Physical wire source definition.
     * @throws GenerationException
     */
    PWSD generateWireSource(LogicalBinding<BD> binding, Policy policy, ServiceDefinition serviceDefinition) throws GenerationException;

    /**
     * Generates a physical wire target definition from a logical binding.
     *
     * @param binding Logical binding.
     * @param intentsToBeProvided Intents to be provided explictly by the binding.
     * @param referenceDefinition Reference definition for the target.
     * @return Physical wire target definition.
     * @throws GenerationException
     */
    PWTD generateWireTarget(LogicalBinding<BD> binding, Policy policy, ReferenceDefinition referenceDefinition) throws GenerationException;

}
