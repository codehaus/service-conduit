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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.fabric.instantiator.component;

import java.net.URI;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.scdl.Implementation;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;

/**
 * @version $Rev: 5344 $ $Date: 2008-09-07 18:59:55 +0100 (Sun, 07 Sep 2008) $
 */
public class AssemblyPropertyTestCase extends TestCase {
    private static final DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();
    private AbstractComponentInstantiator componentInstantiator;
    private LogicalComponent<CompositeImplementation> domain;
    private Element root;
    private Document property;

    public void testSimpleProperty() throws Exception {
        root.setTextContent("Hello World");
        Document value = componentInstantiator.deriveValueFromXPath("$domain", domain);
        Node child = value.getDocumentElement().getFirstChild();
        assertEquals(Node.TEXT_NODE, child.getNodeType());
        assertEquals("Hello World", child.getTextContent());
    }

    public void testComplexProperty() throws Exception {
        Element http = property.createElement("http");
        root.appendChild(http);
        Element port = property.createElement("port");
        http.appendChild(port);
        port.setTextContent("8080");
        Document value = componentInstantiator.deriveValueFromXPath("$domain/http/port", domain);
        Node child = value.getDocumentElement().getFirstChild();
        assertEquals(Node.ELEMENT_NODE, child.getNodeType());
        assertEquals("port", child.getNodeName());
        assertEquals("8080", child.getTextContent());
    }

    public void testAttributeProperty() throws Exception {
        Element http = property.createElement("http");
        http.setAttribute("port", "8080");
        root.appendChild(http);
        Document value = componentInstantiator.deriveValueFromXPath("$domain/http/@port", domain);
        Node child = value.getDocumentElement().getFirstChild();
        assertEquals(Node.ELEMENT_NODE, child.getNodeType());
        assertEquals("port", child.getNodeName());
        assertEquals("8080", child.getTextContent());
    }

    public void testComplexPropertyWithMultipleValues() throws Exception {
        Element http1 = property.createElement("http");
        root.appendChild(http1);
        http1.setAttribute("index", "1");
        Element http2 = property.createElement("http");
        root.appendChild(http2);
        http2.setAttribute("index", "2");
        Document value = componentInstantiator.deriveValueFromXPath("$domain/http", domain);
        Node child = value.getDocumentElement();
        NodeList list = child.getChildNodes();
        assertEquals(2, list.getLength());
        assertEquals("http", list.item(0).getNodeName());
        assertEquals("1", ((Element) list.item(0)).getAttribute("index"));
        assertEquals("http", list.item(1).getNodeName());
        assertEquals("2", ((Element) list.item(1)).getAttribute("index"));
    }

    public void testUnknownVariable() {
        try {
            componentInstantiator.deriveValueFromXPath("$foo", domain);
            fail();
        } catch (XPathExpressionException e) {
            // this is ok?
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        componentInstantiator = new AbstractComponentInstantiator(null) {

            public <I extends Implementation<?>> LogicalComponent<I> instantiate(LogicalCompositeComponent parent,
                                                                                 Map<String, Document> properties,
                                                                                 ComponentDefinition<I> definition,
                                                                                 LogicalChange change) {
                return null;
            }
        };
        domain = new LogicalComponent<CompositeImplementation>(URI.create("sca4j://domain"),
                                                               URI.create("sca4j://domain"),
                                                               null,
                                                               null);

        property = FACTORY.newDocumentBuilder().newDocument();
        root = property.createElement("value");
        property.appendChild(root);
        domain.setPropertyValue("domain", property);
    }

}
