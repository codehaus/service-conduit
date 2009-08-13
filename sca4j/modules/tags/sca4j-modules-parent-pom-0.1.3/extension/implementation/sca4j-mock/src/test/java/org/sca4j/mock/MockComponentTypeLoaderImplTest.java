/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */
package org.sca4j.mock;

import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.contract.DefaultContractProcessor;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.scdl.ServiceDefinition;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class MockComponentTypeLoaderImplTest extends TestCase {

    public void testLoad() throws Exception {
        
        IntrospectionContext context = EasyMock.createMock(IntrospectionContext.class);
        EasyMock.expect(context.getTargetClassLoader()).andReturn(getClass().getClassLoader());
        EasyMock.replay(context);

        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        ContractProcessor processor = new DefaultContractProcessor(helper);
        MockComponentTypeLoader componentTypeLoader = new MockComponentTypeLoaderImpl(helper, processor);
        
        List<String> mockedInterfaces = new LinkedList<String>();
        mockedInterfaces.add("org.sca4j.mock.Foo");
        mockedInterfaces.add("org.sca4j.mock.Bar");
        mockedInterfaces.add("org.sca4j.mock.Baz");
        
        MockComponentType componentType = componentTypeLoader.load(mockedInterfaces, context);
        
        assertNotNull(componentType);
        java.util.Map<String, ServiceDefinition> services = componentType.getServices();
        
        assertEquals(3, services.size());
        
        ServiceDefinition service = services.get("service0");
        assertNotNull(service);
        assertEquals("Foo", service.getName());
        assertEquals("org.sca4j.mock.Foo", service.getServiceContract().getQualifiedInterfaceName());

        service = services.get("service1");
        assertNotNull(service);
        assertEquals("Bar", service.getName());
        assertEquals("org.sca4j.mock.Bar", service.getServiceContract().getQualifiedInterfaceName());

        service = services.get("service2");
        assertNotNull(service);
        assertEquals("Baz", service.getName());
        assertEquals("org.sca4j.mock.Baz", service.getServiceContract().getQualifiedInterfaceName());
        
        
    }

}
