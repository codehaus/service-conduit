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
package org.sca4j.fabric.services.contribution.manifest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.services.xmlfactory.impl.XMLFactoryImpl;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.scdl.DefaultValidationContext;

/**
 * @version $Rev: 4312 $ $Date: 2008-05-23 23:29:23 +0100 (Fri, 23 May 2008) $
 */
public class XmlManifestProcessorTestCase extends TestCase {
    public static final QName QNAME = new QName("foo", "bar");
    public static final byte[] XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bar xmlns=\"foo\"/>".getBytes();
    public static final byte[] XML_DTD = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<!DOCTYPE bar>" +
            "<bar xmlns=\"foo\"/>").getBytes();

    private XmlManifestProcessor processor;
    private XmlManifestProcessorRegistry registry;

    public void testDispatch() throws Exception {
        InputStream stream = new ByteArrayInputStream(XML);
        ContributionManifest manifest = new ContributionManifest();
        ValidationContext context = new DefaultValidationContext();
        processor.process(manifest, stream, context);
        EasyMock.verify(registry);
    }

    public void testDTDDispatch() throws Exception {
        InputStream stream = new ByteArrayInputStream(XML_DTD);
        ContributionManifest manifest = new ContributionManifest();
        ValidationContext context = new DefaultValidationContext();
        processor.process(manifest, stream, context);
        EasyMock.verify(registry);
    }

    protected void setUp() throws Exception {
        super.setUp();
        XMLFactory factory = new XMLFactoryImpl();
        registry = EasyMock.createMock(XmlManifestProcessorRegistry.class);
        registry.process(EasyMock.eq(QNAME),
                         EasyMock.isA(ContributionManifest.class),
                         EasyMock.isA(XMLStreamReader.class),
                         EasyMock.isA(ValidationContext.class));
        EasyMock.replay(registry);
        processor = new XmlManifestProcessor(null, registry, factory);


    }
}
