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
