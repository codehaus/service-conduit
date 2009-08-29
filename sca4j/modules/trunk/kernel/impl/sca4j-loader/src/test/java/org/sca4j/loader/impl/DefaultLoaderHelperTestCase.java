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
package org.sca4j.loader.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.sca4j.loader.impl.DefaultLoaderHelper;
import org.sca4j.introspection.xml.InvalidPrefixException;

/**
 * @version $Rev: 4301 $ $Date: 2008-05-23 06:33:58 +0100 (Fri, 23 May 2008) $
 */
public class DefaultLoaderHelperTestCase extends TestCase {

    public static final String XML = "<composite xmlns=\"http://www.osoa.org/xmlns/sca/1.0\" " +
            "xmlns:f3=\"urn:sca4j.org\"/>";
    private XMLInputFactory xmlFactory;
    private DefaultLoaderHelper helper;

    public void testCreateQName() throws Exception {
        XMLStreamReader reader = createReader(XML);
        QName qName = helper.createQName("f3:bar", reader);
        assertEquals("urn:sca4j.org", qName.getNamespaceURI());
        assertEquals("bar", qName.getLocalPart());
    }

    public void testCreateQNameContext() throws Exception {
        XMLStreamReader reader = createReader(XML);
        QName qName = helper.createQName("bar", reader);
        assertEquals("http://www.osoa.org/xmlns/sca/1.0", qName.getNamespaceURI());
    }

    public void testCreateQNameInvalidPrefix() throws Exception {
        XMLStreamReader reader = createReader(XML);
        try {
            helper.createQName("bad:bar", reader);
            fail();
        } catch (InvalidPrefixException e) {
            //expected
        }
    }

    public void testComplexProperty() throws XMLStreamException {
        String xml = "<property xmlns:foo='http://foo.com'>"
            + "<foo:a>aValue</foo:a>"
            + "<foo:b>InterestingURI</foo:b>"
            + "</property>";

        XMLStreamReader reader = createReader(xml);
        Document value = helper.loadValue(reader);
        reader.close();

        NodeList childNodes = value.getDocumentElement().getChildNodes();
        assertEquals(2, childNodes.getLength());

        Element e = (Element) childNodes.item(0);
        assertEquals("http://foo.com", e.getNamespaceURI());
        assertEquals("a", e.getLocalName());
        assertEquals("aValue", e.getTextContent());
        e = (Element) childNodes.item(1);
        assertEquals("http://foo.com", e.getNamespaceURI());
        assertEquals("b", e.getLocalName());
        assertEquals("InterestingURI", e.getTextContent());
    }

    protected void setUp() throws Exception {
        super.setUp();
        xmlFactory = XMLInputFactory.newInstance();

        helper = new DefaultLoaderHelper();
    }

    private XMLStreamReader createReader(String xml) throws XMLStreamException {

        InputStream in = new ByteArrayInputStream(xml.getBytes());
        XMLStreamReader reader = xmlFactory.createXMLStreamReader(in);
        reader.nextTag();
        return reader;

    }
}
