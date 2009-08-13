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

import java.net.URI;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamConstants;

import junit.framework.TestCase;

import org.sca4j.scdl.WireDefinition;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.loader.impl.DefaultLoaderHelper;

import org.easymock.EasyMock;

/**
 * @version $Rev: 5134 $ $Date: 2008-08-02 07:33:02 +0100 (Sat, 02 Aug 2008) $
 */
public class WireLoaderTestCase extends TestCase {
    private WireLoader wireLoader;
    private XMLStreamReader reader;

    public void testWithNoServiceName() throws LoaderException, XMLStreamException {
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "source")).andReturn("source");
        EasyMock.expect(reader.getAttributeValue(null, "target")).andReturn("target");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        WireDefinition definition = wireLoader.load(reader, null);
        EasyMock.verify(reader);
        assertEquals(URI.create("source"), definition.getSource());
        assertEquals(URI.create("target"), definition.getTarget());
    }

    public void testWithServiceName() throws LoaderException, XMLStreamException {
        EasyMock.expect(reader.getAttributeCount()).andReturn(0);
        EasyMock.expect(reader.getAttributeValue(null, "source")).andReturn("source/s");
        EasyMock.expect(reader.getAttributeValue(null, "target")).andReturn("target/t");
        EasyMock.expect(reader.next()).andReturn(XMLStreamConstants.END_ELEMENT);
        EasyMock.replay(reader);
        WireDefinition definition = wireLoader.load(reader, null);
        EasyMock.verify(reader);
        assertEquals(URI.create("source#s"), definition.getSource());
        assertEquals(URI.create("target#t"), definition.getTarget());
    }

    protected void setUp() throws Exception {
        super.setUp();
        LoaderHelper helper = new DefaultLoaderHelper();
        wireLoader = new WireLoader(helper);
        reader = EasyMock.createStrictMock(XMLStreamReader.class);
    }
}
