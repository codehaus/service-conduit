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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.rs.control;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.sca4j.rs.provision.RsWireSourceDefinition;
import org.sca4j.rs.provision.RsWireTargetDefinition;
import org.sca4j.rs.scdl.RsBindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.policy.Policy;

/**
 * Implementation of the REST binding generator.
 *
 * @version $Rev: 4739 $ $Date: 2008-06-05 19:39:31 +0100 (Thu, 05 Jun 2008) $
 */
@EagerInit
public class RsBindingGenerator implements BindingGenerator<RsWireSourceDefinition, RsWireTargetDefinition, RsBindingDefinition> {

    public RsWireSourceDefinition generateWireSource(LogicalBinding<RsBindingDefinition> binding, Policy policy, ServiceDefinition definition) {
        URI classloaderId = binding.getParent().getParent().getParent().getUri();
        URI endpointUri = binding.getBinding().getTargetUri();
        String interfaze = definition.getServiceContract().getQualifiedInterfaceName();
        return new RsWireSourceDefinition(classloaderId, endpointUri, interfaze);
    }

    public RsWireTargetDefinition generateWireTarget(LogicalBinding<RsBindingDefinition> binding, Policy policy, ReferenceDefinition definition) {
        URI classloaderId = binding.getParent().getParent().getParent().getUri();
        URI endpointUri = binding.getBinding().getTargetUri();
        String interfaze = definition.getServiceContract().getQualifiedInterfaceName();
        return new RsWireTargetDefinition(classloaderId, endpointUri, interfaze);
    }
    
}
