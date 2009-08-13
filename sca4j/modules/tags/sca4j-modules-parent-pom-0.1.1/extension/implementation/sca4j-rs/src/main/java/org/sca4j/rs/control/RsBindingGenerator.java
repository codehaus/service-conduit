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
package org.sca4j.rs.control;

import java.net.URI;
import org.sca4j.rs.scdl.RsBindingDefinition;
import org.sca4j.rs.provision.RsWireSourceDefinition;
import org.sca4j.rs.provision.RsWireTargetDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;
import org.osoa.sca.annotations.EagerInit;

/**
 * Implementation of the REST binding generator.
 *
 * @version $Rev: 4739 $ $Date: 2008-06-05 19:39:31 +0100 (Thu, 05 Jun 2008) $
 */
@EagerInit
public class RsBindingGenerator implements BindingGenerator<RsWireSourceDefinition, RsWireTargetDefinition, RsBindingDefinition> {

    public RsWireSourceDefinition generateWireSource(LogicalBinding<RsBindingDefinition> logicalBinding,
            Policy policy,
            ServiceDefinition serviceDefinition)
            throws GenerationException {

        RsWireSourceDefinition rwsd = new RsWireSourceDefinition();
        URI id = logicalBinding.getParent().getParent().getParent().getUri();
        rwsd.setClassLoaderId(id);
        rwsd.setUri(logicalBinding.getBinding().getTargetUri());
        rwsd.setInterfaceName(serviceDefinition.getServiceContract().getInterfaceName());
        rwsd.setIsResource(logicalBinding.getBinding().isResource());
        rwsd.setIsProvider(logicalBinding.getBinding().isProvider());


        return rwsd;

    }

    public RsWireTargetDefinition generateWireTarget(LogicalBinding<RsBindingDefinition> logicalBinding,
            Policy policy,
            ReferenceDefinition referenceDefinition)
            throws GenerationException {
        throw new GenerationException("Not supported");

    }
}
