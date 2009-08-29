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
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;

import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionException;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.pojo.scdl.PojoComponentType;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.system.scdl.SystemImplementation;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class SystemServiceHeuristicTestCase extends TestCase {
    private static final Set<Class<?>> NOCLASSES = Collections.emptySet();
    private SystemServiceHeuristic heuristic;

    private ContractProcessor contractProcessor;
    private IntrospectionHelper helper;
    private IntrospectionContext context;
    private SystemImplementation impl;
    private PojoComponentType componentType;
    private ServiceContract<Type> serviceInterfaceContract;
    private ServiceContract<Type> noInterfaceContract;
    private IMocksControl control;
    private TypeMapping typeMapping;

    public void testNoInterface() throws IntrospectionException {
        EasyMock.expect(helper.getImplementedInterfaces(NoInterface.class)).andReturn(NOCLASSES);
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, typeMapping);
        EasyMock.expect(contractProcessor.introspect(typeMapping, NoInterface.class, context)).andReturn(noInterfaceContract);
        control.replay();

        heuristic.applyHeuristics(impl, NoInterface.class, context);
        Map<String, ServiceDefinition> services = componentType.getServices();
        assertEquals(1, services.size());
        assertEquals(noInterfaceContract, services.get("NoInterface").getServiceContract());
        control.verify();
    }

    public void testWithInterface() throws IntrospectionException {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        interfaces.add(ServiceInterface.class);

        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, typeMapping);
        EasyMock.expect(helper.getImplementedInterfaces(OneInterface.class)).andReturn(interfaces);
        EasyMock.expect(contractProcessor.introspect(typeMapping, ServiceInterface.class, context)).andReturn(
                serviceInterfaceContract);
        control.replay();

        heuristic.applyHeuristics(impl, OneInterface.class, context);
        Map<String, ServiceDefinition> services = componentType.getServices();
        assertEquals(1, services.size());
        assertEquals(serviceInterfaceContract, services.get("ServiceInterface").getServiceContract());
        control.verify();
    }

    public void testServiceWithExistingServices() throws IntrospectionException {
        ServiceDefinition definition = new ServiceDefinition("Contract");
        impl.getComponentType().add(definition);
        control.replay();

        heuristic.applyHeuristics(impl, NoInterface.class, context);
        Map<String, ServiceDefinition> services = impl.getComponentType().getServices();
        assertEquals(1, services.size());
        assertSame(definition, services.get("Contract"));
        control.verify();
    }

    public static interface ServiceInterface {
    }

    public static class NoInterface {
    }

    public static class OneInterface implements ServiceInterface {
    }

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        super.setUp();
        impl = new SystemImplementation();
        componentType = new PojoComponentType();
        impl.setComponentType(componentType);

        noInterfaceContract = createServiceContract(NoInterface.class);
        serviceInterfaceContract = createServiceContract(ServiceInterface.class);

        control = EasyMock.createControl();
        typeMapping = new TypeMapping();
        context = control.createMock(IntrospectionContext.class);
        EasyMock.expect(context.getTypeMapping()).andStubReturn(typeMapping);
        contractProcessor = control.createMock(ContractProcessor.class);
        helper = control.createMock(IntrospectionHelper.class);
        heuristic = new SystemServiceHeuristic(contractProcessor, helper);
    }

    private ServiceContract<Type> createServiceContract(Class<?> type) {
        @SuppressWarnings("unchecked")
        ServiceContract<Type> contract = EasyMock.createMock(ServiceContract.class);
        EasyMock.expect(contract.getInterfaceName()).andStubReturn(type.getSimpleName());
        EasyMock.replay(contract);
        return contract;
    }
}
