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
package org.sca4j.transform.dom2java.generics.list;

import java.io.StringReader;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * @version $Revision$ $Date$
 */
public class String2ListOfQNameTestCase extends TestCase {
    private static final DocumentBuilderFactory DOCUMENT_FACTORY;
    private static final String PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String NO_NAMESPACES_XML = PREFIX + "<test>zero, one, two</test>";
    private static final String NAMESPACES_XML = PREFIX + "<test>{ns}zero, {ns}one, {ns}two</test>";

    static {
        DOCUMENT_FACTORY = DocumentBuilderFactory.newInstance();
        DOCUMENT_FACTORY.setNamespaceAware(true);
    }

    private String2ListOfQName transformer;

    public void testNoNamespacesTransform() throws Exception {
        Element element = createTestNode(NO_NAMESPACES_XML);
        List<QName> list = transformer.transform(element, null);
        assertEquals(3, list.size());
        assertEquals("zero", list.get(0).getLocalPart());
        assertEquals("one", list.get(1).getLocalPart());
        assertEquals("two", list.get(2).getLocalPart());
    }

    public void testNamespacesTransform() throws Exception {
        Element element = createTestNode(NAMESPACES_XML);
        List<QName> list = transformer.transform(element, null);
        assertEquals(3, list.size());
        assertEquals("ns", list.get(0).getNamespaceURI());
        assertEquals("ns", list.get(1).getNamespaceURI());
        assertEquals("ns", list.get(2).getNamespaceURI());
    }

    private Element createTestNode(String xml) throws Exception {
        InputSource source = new InputSource(new StringReader(xml));
        Document document = DOCUMENT_FACTORY.newDocumentBuilder().parse(source);
        return document.getDocumentElement();
    }

    protected void setUp() throws Exception {
        super.setUp();
        transformer = new String2ListOfQName();

    }
}
