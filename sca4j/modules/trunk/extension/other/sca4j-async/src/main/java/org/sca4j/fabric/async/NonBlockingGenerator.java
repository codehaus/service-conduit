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
package org.sca4j.fabric.async;

import javax.xml.namespace.QName;

import org.sca4j.host.Namespaces;
import org.sca4j.scdl.Operation;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.generator.InterceptorDefinitionGenerator;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Element;

/**
 * Creates {@link NonBlockingInterceptorDefinition}s for one-way operations.
 *
 * @version $Rev: 2931 $ $Date: 2008-02-28 12:49:35 +0000 (Thu, 28 Feb 2008) $
 */
@EagerInit
public class NonBlockingGenerator implements InterceptorDefinitionGenerator {
    
    private static final QName QNAME = new QName(Namespaces.SCA4J_NS, "oneWayPolicy");
    
    private GeneratorRegistry registry;

    public NonBlockingGenerator(@Reference GeneratorRegistry registry) {
        this.registry = registry;
    }

    @Init
    public void init() {
        registry.register(QNAME, this);
    }

    public PhysicalInterceptorDefinition generate(Element policyDefinition,
                                                  Operation<?> operation,
                                                  LogicalBinding<?> logicalBinding) throws GenerationException {
        return new NonBlockingInterceptorDefinition();
    }
}
