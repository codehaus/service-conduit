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
package org.sca4j.fabric.services.contribution.processor;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.services.xmlfactory.impl.XMLFactoryImpl;
import org.sca4j.services.xmlfactory.XMLFactory;

/**
 * @version $Rev: 4226 $ $Date: 2008-05-15 10:38:18 +0100 (Thu, 15 May 2008) $
 */
public class XmlResourceProcessorTestCase extends TestCase {
    public static final QName QNAME = new QName("foo", "bar");
    public static final byte[] XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions xmlns=\"foo\"/>".getBytes();
    public static final byte[] XML_DTD = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<!DOCTYPE definitions>" +
            "<definitions xmlns=\"foo\"/>").getBytes();

    private XmlResourceProcessor processor;
//    private LoaderRegistry registry;

    public void testDispatch() throws Exception {
//        InputStream stream = new ByteArrayInputStream(XML);
//        processor.process(stream);
//        EasyMock.verify(registry);
    }

    public void testDTDDispatch() throws Exception {
//        InputStream stream = new ByteArrayInputStream(XML_DTD);
//        processor.process(stream);
//        EasyMock.verify(registry);
    }

    @SuppressWarnings({"unchecked"})
    protected void setUp() throws Exception {
        super.setUp();
        XMLFactory factory = new XMLFactoryImpl();
//        registry = EasyMock.createMock(LoaderRegistry.class);
//        EasyMock.expect(registry.load(EasyMock.isA(XMLStreamReader.class),
//                                      EasyMock.isA(Class.class),
//                                      EasyMock.isA(IntrospectionContext.class))).andReturn(null);
//        EasyMock.replay(registry);
        processor = new XmlResourceProcessor(null, null, null, factory);


    }
}
