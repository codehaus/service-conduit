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
package org.sca4j.fabric.domain;

import org.osoa.sca.annotations.Reference;
import org.sca4j.fabric.allocator.Allocator;
import org.sca4j.fabric.generator.PhysicalModelGenerator;
import org.sca4j.fabric.instantiator.LogicalModelInstantiator;
import org.sca4j.fabric.services.routing.RoutingService;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.lcm.LogicalComponentManager;

/**
 * Implements a domain for system components in a runtime. SCA4J runtimes are constituted using SCA components and the runtime domain manages
 * deployment of those system components. When a runtime is booted, the runtime domain is provided with a set of primoridal services for deploying
 * system components. After bootstrap, the runtime domain is reinjected with a new set of fully-configured deployment services.
 *
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
public class RuntimeDomain extends AbstractDomain {

    public RuntimeDomain(@Reference Allocator allocator,
                         @Reference MetaDataStore metadataStore,
                         @Reference PhysicalModelGenerator physicalModelGenerator,
                         @Reference LogicalModelInstantiator logicalModelInstantiator,
                         @Reference LogicalComponentManager logicalComponentManager,
                         @Reference RoutingService routingService) {
        super(metadataStore, logicalComponentManager, allocator, physicalModelGenerator, logicalModelInstantiator, routingService);
    }

    /**
     * Used for reinjection.
     *
     * @param physicalModelGenerator the generator to inject
     */
    @Reference
    public void setPhysicalModelGenerator(PhysicalModelGenerator physicalModelGenerator) {
        this.physicalModelGenerator = physicalModelGenerator;
    }

    /**
     * Used for reinjection.
     *
     * @param routingService the routing service to reinject
     */
    @Reference
    public void setRoutingService(RoutingService routingService) {
        this.routingService = routingService;
    }


}
