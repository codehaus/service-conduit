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
