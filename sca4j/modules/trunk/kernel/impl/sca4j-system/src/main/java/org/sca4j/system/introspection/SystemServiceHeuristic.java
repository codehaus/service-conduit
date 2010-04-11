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
package org.sca4j.system.introspection;

import java.lang.reflect.Type;
import java.util.Set;

import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.Management;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.jmx.scdl.JMXBinding;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * Heuristic that identifies the services provided by an implementation class.
 *
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class SystemServiceHeuristic implements HeuristicProcessor<SystemImplementation> {
    private final ContractProcessor contractProcessor;
    private final IntrospectionHelper helper;

    public SystemServiceHeuristic(@Reference ContractProcessor contractProcessor,
                                  @Reference IntrospectionHelper helper) {
        this.contractProcessor = contractProcessor;
        this.helper = helper;
    }

    public void applyHeuristics(SystemImplementation implementation, Class<?> implClass, IntrospectionContext context) {
        PojoComponentType componentType = implementation.getComponentType();
        TypeMapping typeMapping = context.getTypeMapping();

        // if the service contracts have not already been defined then introspect them
        if (componentType.getServices().isEmpty()) {
            // get the most specific interfaces implemented by the class
            Set<Class<?>> interfaces = helper.getImplementedInterfaces(implClass);

            // if the class does not implement any interfaces, then the class itself is the service contract
            // we don't have to worry about proxies because all wires to system components are optimized
            if (interfaces.isEmpty()) {
                ServiceDefinition serviceDefinition = createServiceDefinition(implClass, typeMapping, context);
                componentType.add(serviceDefinition);
            } else {
                // otherwise, expose all of the implemented interfaces
                for (Class<?> serviceInterface : interfaces) {
                    ServiceDefinition serviceDefinition = createServiceDefinition(serviceInterface, typeMapping, context);
                    componentType.add(serviceDefinition);
                }
            }
        }

        // Add the JMX Management binding to all services tagged as management
        for (ServiceDefinition service : componentType.getServices().values()) {
            if (service.isManagement()) {
                JMXBinding binding = new JMXBinding();
                service.addBinding(binding);
            }
        }
    }

    ServiceDefinition createServiceDefinition(Class<?> serviceInterface, TypeMapping typeMapping, IntrospectionContext context) {
        ServiceContract<Type> contract = contractProcessor.introspect(typeMapping, serviceInterface, context);
        ServiceDefinition service = new ServiceDefinition(contract.getInterfaceName(), contract);
        service.setManagement(serviceInterface.isAnnotationPresent(Management.class));
        return service;
    }
}
