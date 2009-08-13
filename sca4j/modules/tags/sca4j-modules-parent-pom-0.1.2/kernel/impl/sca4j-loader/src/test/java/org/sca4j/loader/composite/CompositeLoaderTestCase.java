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
package org.sca4j.loader.composite;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.osoa.sca.Constants.SCA_NS;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.PolicyAware;

/**
 * @version $Rev: 5134 $ $Date: 2008-08-02 07:33:02 +0100 (Sat, 02 Aug 2008) $
 */
public class CompositeLoaderTestCase extends TestCase {
    public static final QName COMPOSITE = new QName(SCA_NS, "composite");
    private CompositeLoader loader;
    private QName name;
    private IntrospectionContext introspectionContext;

    public void testLoadNameAndDefaultAutowire() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getNamespaceContext()).andStubReturn(null);
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "local")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "constrainingType")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn(null);
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader, introspectionContext);
        Composite type = loader.load(reader, introspectionContext);
        assertEquals(name, type.getName());
        assertEquals(Autowire.INHERITED, type.getAutowire());
        EasyMock.verify(reader, introspectionContext);
    }

    public void testAutowire() throws Exception {
        XMLStreamReader reader = EasyMock.createMock(XMLStreamReader.class);
        EasyMock.expect(reader.getNamespaceContext()).andStubReturn(null);
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "name")).andReturn(name.getLocalPart());
        EasyMock.expect(reader.getAttributeValue(null, "targetNamespace")).andReturn(name.getNamespaceURI());
        EasyMock.expect(reader.getAttributeValue(null, "local")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "constrainingType")).andReturn(null);
        EasyMock.expect(reader.getAttributeValue(null, "autowire")).andReturn("true");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.expect(reader.getName()).andReturn(COMPOSITE);
        EasyMock.replay(reader, introspectionContext);
        Composite type = loader.load(reader, introspectionContext);
        assertEquals(Autowire.ON, type.getAutowire());
        EasyMock.verify(reader, introspectionContext);
    }

    protected void setUp() throws Exception {
        super.setUp();
        introspectionContext = EasyMock.createMock(IntrospectionContext.class);
        EasyMock.expect(introspectionContext.getSourceBase()).andStubReturn(null);
        EasyMock.expect(introspectionContext.getTargetClassLoader()).andStubReturn(null);
        EasyMock.expect(introspectionContext.getContributionUri()).andStubReturn(null);
        EasyMock.expect(introspectionContext.getTypeMapping()).andStubReturn(null);

        LoaderHelper loaderHelper = EasyMock.createMock(LoaderHelper.class);
        loaderHelper.loadPolicySetsAndIntents(EasyMock.isA(PolicyAware.class),
                                              EasyMock.isA(XMLStreamReader.class),
                                              EasyMock.isA(IntrospectionContext.class));
        EasyMock.replay(loaderHelper);

        loader = new CompositeLoader(null, null, null, null, null, null, null, loaderHelper);
        name = new QName("http://example.com", "composite");
    }
}
