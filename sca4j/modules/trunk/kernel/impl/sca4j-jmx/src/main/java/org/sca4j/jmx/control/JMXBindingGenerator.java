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
