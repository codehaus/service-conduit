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
package org.sca4j.binding.ws.control;

import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.ws.scdl.WsBindingDefinition;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.generator.BindingGenerator;
import org.sca4j.spi.generator.BindingGeneratorDelegate;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * Implementation of the WS binding generator.
 *
 * @version $Revision: 3145 $ $Date: 2008-03-19 11:04:25 +0000 (Wed, 19 Mar 2008) $
 */
@EagerInit
public class WsBindingGenerator implements BindingGenerator<PhysicalWireSourceDefinition, PhysicalWireTargetDefinition, WsBindingDefinition> {
    
    private Map<String, BindingGeneratorDelegate<WsBindingDefinition>> delegates;

    @Reference
    public void setDelegates(Map<String, BindingGeneratorDelegate<WsBindingDefinition>> delegates) {
        this.delegates = delegates;
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalBinding<WsBindingDefinition> logicalBinding,
                                                           Policy policy,
                                                           ServiceDefinition serviceDefinition)
            throws GenerationException {
        return getDelegate(logicalBinding).generateWireSource(logicalBinding, policy, serviceDefinition);
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalBinding<WsBindingDefinition> logicalBinding,
                                                           Policy policy,
                                                           ReferenceDefinition referenceDefinition)
            throws GenerationException {
        return getDelegate(logicalBinding).generateWireTarget(logicalBinding, policy, referenceDefinition);
    }

    /*
     * Gets the delegate for the requested WS stack. If no specific implementation is specified, the first registered
     * stack is returned.
     */
    private BindingGeneratorDelegate<WsBindingDefinition> getDelegate(LogicalBinding<WsBindingDefinition> logicalBinding)
            throws WsBindingGenerationException {

        if (delegates.isEmpty()) {
            throw new MissingWebServiceExtensionException();
        }
        String implementation = logicalBinding.getBinding().getImplementation();
        BindingGeneratorDelegate<WsBindingDefinition> delegate;
        if (implementation == null) {
            delegate = delegates.values().iterator().next();
        } else {
            delegate = delegates.get(implementation);
        }
        if (delegate == null) {
            throw new WsBindingGenerationException("Unknown web services extension requested", implementation);
        }
        return delegate;

    }

}
