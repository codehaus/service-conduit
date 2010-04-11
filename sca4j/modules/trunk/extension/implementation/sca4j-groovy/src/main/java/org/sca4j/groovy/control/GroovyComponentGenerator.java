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
package org.sca4j.groovy.control;

import java.net.URI;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.groovy.provision.GroovyComponentDefinition;
import org.sca4j.groovy.provision.GroovyInstanceFactoryDefinition;
import org.sca4j.groovy.provision.GroovyWireSourceDefinition;
import org.sca4j.groovy.provision.GroovyWireTargetDefinition;
import org.sca4j.groovy.scdl.GroovyImplementation;
import org.sca4j.pojo.control.InstanceFactoryGenerationHelper;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ComponentDefinition;
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
 * @version $Rev: 5250 $ $Date: 2008-08-21 02:18:25 +0100 (Thu, 21 Aug 2008) $
 */
@EagerInit
public class GroovyComponentGenerator implements ComponentGenerator<LogicalComponent<GroovyImplementation>> {
    private final InstanceFactoryGenerationHelper helper;

    public GroovyComponentGenerator(@Reference GeneratorRegistry registry, @Reference InstanceFactoryGenerationHelper helper) {
        registry.register(GroovyImplementation.class, this);
        this.helper = helper;
    }

    public PhysicalComponentDefinition generate(LogicalComponent<GroovyImplementation> component) throws GenerationException {

        ComponentDefinition<GroovyImplementation> definition = component.getDefinition();
        GroovyImplementation implementation = definition.getImplementation();
        PojoComponentType type = implementation.getComponentType();

        // create the instance factory definition
        GroovyInstanceFactoryDefinition providerDefinition = new GroovyInstanceFactoryDefinition();
        providerDefinition.setConstructor(type.getConstructor());
        providerDefinition.setInitMethod(type.getInitMethod());
        providerDefinition.setDestroyMethod(type.getDestroyMethod());
        providerDefinition.setImplementationClass(implementation.getClassName());
        providerDefinition.setScriptName(implementation.getScriptName());
        helper.processInjectionSites(component, providerDefinition);

        // create the physical component definition
        URI componentId = component.getUri();
        GroovyComponentDefinition physical = new GroovyComponentDefinition();
        physical.setComponentId(componentId);
        physical.setGroupId(component.getParent().getUri());
        physical.setScope(type.getScope());
        physical.setInitLevel(helper.getInitLevel(definition, type));
        physical.setProviderDefinition(providerDefinition);
        helper.processPropertyValues(component, physical);
        // generate the classloader resource definition
        URI classLoaderId = component.getClassLoaderId();
        physical.setClassLoaderId(classLoaderId);

        return physical;
    }

    public PhysicalWireSourceDefinition generateWireSource(LogicalComponent<GroovyImplementation> source,
                                                           LogicalReference reference,
                                                           Policy policy)
            throws GenerationException {
        URI uri = reference.getUri();
        ServiceContract<?> serviceContract = reference.getDefinition().getServiceContract();
        String interfaceName = serviceContract.getQualifiedInterfaceName();
        URI classLoaderId = source.getClassLoaderId();

        GroovyWireSourceDefinition wireDefinition = new GroovyWireSourceDefinition();
        wireDefinition.setUri(uri);
        wireDefinition.setValueSource(new InjectableAttribute(InjectableAttributeType.REFERENCE, uri.getFragment()));
        wireDefinition.setInterfaceName(interfaceName);
        // assume for now that any wire from a Groovy component can be optimized
        wireDefinition.setOptimizable(true);

        wireDefinition.setClassLoaderId(classLoaderId);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateCallbackWireSource(LogicalComponent<GroovyImplementation> source,
                                                                   ServiceContract<?> serviceContract,
                                                                   Policy policy) throws GenerationException {
        throw new UnsupportedOperationException();
    }

    public PhysicalWireTargetDefinition generateWireTarget(LogicalService service,
                                                           LogicalComponent<GroovyImplementation> target,
                                                           Policy policy) throws GenerationException {
        GroovyWireTargetDefinition wireDefinition = new GroovyWireTargetDefinition();
        URI uri;
        if (service != null) {
            uri = service.getUri();
        } else {
            // no service specified, use the default
            uri = target.getUri();
        }
        wireDefinition.setUri(uri);
        return wireDefinition;
    }

    public PhysicalWireSourceDefinition generateResourceWireSource(LogicalComponent<GroovyImplementation> source, LogicalResource<?> resource)
            throws GenerationException {
        GroovyWireSourceDefinition wireDefinition = new GroovyWireSourceDefinition();
        wireDefinition.setUri(resource.getUri());
        return wireDefinition;
    }
}
