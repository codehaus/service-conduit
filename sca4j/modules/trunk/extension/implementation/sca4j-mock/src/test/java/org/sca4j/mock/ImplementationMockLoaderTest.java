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
package org.sca4j.mock;

import java.io.InputStream;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.easymock.EasyMock;
import org.sca4j.introspection.IntrospectionContext;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class ImplementationMockLoaderTest extends TestCase {

    public void testLoad() throws Exception {
        
        MockComponentTypeLoader componentTypeLoader = EasyMock.createMock(MockComponentTypeLoader.class);
        IntrospectionContext context = EasyMock.createMock(IntrospectionContext.class);
        
        ImplementationMockLoader loader = new ImplementationMockLoader(componentTypeLoader);
        
        InputStream stream = getClass().getClassLoader().getResourceAsStream("META-INF/mock.composite");
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
        
        while(reader.hasNext()) {
            if(reader.next() == XMLStreamConstants.START_ELEMENT && ImplementationMock.IMPLEMENTATION_MOCK.equals(reader.getName())) {
                break;
            }
        }
        
        ImplementationMock implementationMock = loader.load(reader, context);
        assertNotNull(implementationMock);
        
        List<String> interfaces = implementationMock.getMockedInterfaces();
        assertEquals(3, interfaces.size());
        assertEquals("org.sca4j.mock.test.Foo", interfaces.get(0));
        assertEquals("org.sca4j.mock.test.Bar", interfaces.get(1));
        assertEquals("org.sca4j.mock.test.Baz", interfaces.get(2));
        
    }

}
