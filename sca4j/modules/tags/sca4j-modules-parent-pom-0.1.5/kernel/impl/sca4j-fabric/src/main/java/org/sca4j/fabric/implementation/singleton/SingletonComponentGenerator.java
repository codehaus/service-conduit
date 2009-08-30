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
package org.sca4j.fabric.implementation.singleton;

import java.net.URI;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.EagerInit;

import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.spi.generator.ComponentGenerator;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.generator.GeneratorRegistry;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.policy.Policy;

/**
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
@EagerInit
public class SingletonComponentGenerator implements ComponentGenerator<LogicalComponent<SingletonImplementation>> {

    public SingletonComponentGenerator(@Reference GeneratorRegistry registry) {
        registry.register(SingletonImplementation.class, this);
    }

    public PhysicalComponentDefinition generate(LogicalComponent<SingletonImplementation> component) throws GenerationException {
        throw new UnsupportedOperationException();
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<SingletonImplementation> source,
                                                           LogicalReference reference,
                                                           Policy policy) throws GenerationException {
        SingletonWireSourceDefinition wireDefinition = new SingletonWireSourceDefinition();
        URI uri = reference.getUri();
        wireDefinition.setOptimizable(true);
        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.REFERENCE, uri.getFragment()));

        URI classLoaderId = source.getClassLoaderId();
        wireDefinition.setClassLoaderId(classLoaderId);
        return wireDefinition;
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service, LogicalComponent<SingletonImplementation> logical, Policy policy)
            throws GenerationException {
        SingletonWireTargetDefinition wireDefinition = new SingletonWireTargetDefinition();
        URI uri = logical.getUri().resolve(service.getUri());
        wireDefinition.setUri(uri);
        wireDefinition.setOptimizable(true);
        URI classLoaderId = logical.getClassLoaderId();
        wireDefinition.setClassLoaderId(classLoaderId);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<SingletonImplementation> source, LogicalResource<?> resource)
            throws GenerationException {
        SingletonWireSourceDefinition wireDefinition = new SingletonWireSourceDefinition();
        URI uri = resource.getUri();
        wireDefinition.setOptimizable(true);
        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.RESOURCE, uri.getFragment()));
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<SingletonImplementation> source,
                                                                   ServiceContract<?> serviceContract,
                                                                   Policy policy) throws GenerationException {
        throw new UnsupportedOperationException();
    }

}
