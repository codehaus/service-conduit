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
package org.sca4j.junit.introspection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.java.HeuristicProcessor;
import org.sca4j.junit.scdl.JUnitImplementation;
import org.sca4j.junit.scdl.JUnitServiceContract;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;

/**
 * @version $Rev: 5438 $ $Date: 2008-09-18 23:55:29 +0100 (Thu, 18 Sep 2008) $
 */
public class JUnitServiceHeuristic implements HeuristicProcessor<JUnitImplementation> {
    private static final String TEST_SERVICE_NAME = "testService";

    private final IntrospectionHelper helper;
    private final ContractProcessor contractProcessor;

    public JUnitServiceHeuristic(@Reference IntrospectionHelper helper, @Reference ContractProcessor contractProcessor) {
        this.helper = helper;
        this.contractProcessor = contractProcessor;
    }

    public void applyHeuristics(JUnitImplementation implementation, Class<?> implClass, IntrospectionContext context) {

        JUnitServiceContract testContract = generateTestContract(implClass);
        ServiceDefinition testService = new ServiceDefinition(TEST_SERVICE_NAME, testContract);
        PojoComponentType componentType = implementation.getComponentType();
        TypeMapping typeMapping = context.getTypeMapping();
        componentType.add(testService);
        // if the class implements a single interface, use it, otherwise the contract is the class itself
        Set<Class<?>> interfaces = helper.getImplementedInterfaces(implClass);
        if (interfaces.size() > 1) {
            for (Class<?> interfaze : interfaces) {
                if (interfaze.getCanonicalName().endsWith("Test")) {
                    continue;
                }
                ServiceDefinition serviceDefinition = createServiceDefinition(interfaze, typeMapping, context);
                componentType.add(serviceDefinition);
            }
        }
    }

    ServiceDefinition createServiceDefinition(Class<?> serviceInterface, TypeMapping typeMapping, IntrospectionContext context) {
        ServiceContract contract = contractProcessor.introspect(typeMapping, serviceInterface, context);
        return new ServiceDefinition(contract.getInterfaceName(), contract);
    }

    private static final List<DataType> INPUT_TYPE;
    private static final DataType OUTPUT_TYPE;
    private static final List<DataType> FAULT_TYPE;

    static {
        List<DataType> paramDataTypes = Collections.emptyList();
        INPUT_TYPE = paramDataTypes;
        OUTPUT_TYPE = new DataType(void.class);
        FAULT_TYPE = Collections.emptyList();
    }

    JUnitServiceContract generateTestContract(Class<?> implClass) {
        List<Operation> operations = new ArrayList<Operation>();
        for (Method method : implClass.getMethods()) {
            // see if this is a test method
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            if (method.getReturnType() != void.class) {
                continue;
            }
            if (method.getParameterTypes().length != 0) {
                continue;
            }
            String name = method.getName();
            if (name.length() < 5 || !name.startsWith("test")) {
                continue;
            }
            Operation operation = new Operation(name, INPUT_TYPE, OUTPUT_TYPE, FAULT_TYPE);
            operations.add(operation);
        }
        return new JUnitServiceContract(operations);
    }
}
