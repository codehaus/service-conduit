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
package org.sca4j.binding.test;

import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

/**
 * Implementation of the test binding generator.
 *
 * @version $Revision: 3107 $ $Date: 2008-03-15 17:44:05 +0000 (Sat, 15 Mar 2008) $
 */
@EagerInit
public class TestBindingGenerator implements BindingGenerator<TestBindingSourceDefinition, TestBindingTargetDefinition, TestBindingDefinition> {

    public TestBindingSourceDefinition generateWireSource(LogicalBinding<TestBindingDefinition> logicalBinding,
                                                          Policy policy,
                                                          ServiceDefinition serviceDefinition)
            throws GenerationException {
        TestBindingSourceDefinition definition = new TestBindingSourceDefinition();
        definition.setUri(logicalBinding.getBinding().getTargetUri());
        return definition;
    }

    public TestBindingTargetDefinition generateWireTarget(LogicalBinding<TestBindingDefinition> logicalBinding,
                                                          Policy policy,
                                                          ReferenceDefinition referenceDefinition)
            throws GenerationException {

        TestBindingTargetDefinition definition = new TestBindingTargetDefinition();
        definition.setUri(logicalBinding.getBinding().getTargetUri());
        return definition;
    }


}
