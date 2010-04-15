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
package org.sca4j.transform.xml;

import static javax.xml.stream.XMLStreamConstants.CDATA;
import static javax.xml.stream.XMLStreamConstants.CHARACTERS;
import static javax.xml.stream.XMLStreamConstants.COMMENT;
import static javax.xml.stream.XMLStreamConstants.DTD;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.ENTITY_REFERENCE;
import static javax.xml.stream.XMLStreamConstants.PROCESSING_INSTRUCTION;
import static javax.xml.stream.XMLStreamConstants.SPACE;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.oasisopen.sca.annotation.EagerInit;
import org.sca4j.scdl.DataType;
import org.sca4j.spi.model.type.JavaClass;
import org.sca4j.transform.AbstractPullTransformer;
import org.sca4j.transform.TransformContext;
import org.sca4j.transform.TransformationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Pull transformer that will convert a Stax stream to a DOM representation. The transformer expects the cursor to be at the element from which the
 * info set needs to transferred into the DOM tree.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class Stream2Document extends AbstractPullTransformer<XMLStreamReader, Document> {

    private static final JavaClass<Document> TARGET = new JavaClass<Document>(Document.class);
    private static final DocumentBuilderFactory FACTORY;

    static {
        FACTORY = DocumentBuilderFactory.newInstance();
        FACTORY.setNamespaceAware(true);
    }

    public DataType getTargetType() {
        return TARGET;
    }

    public Document transform(XMLStreamReader reader, TransformContext context) throws TransformationException {

        if (reader.getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new TransformationException("The stream needs to be at te start of an element");
        }

        DocumentBuilder builder = getDocumentBuilder();
        Document document = builder.newDocument();

        QName rootName = reader.getName();
        Element root = createElement(reader, document, rootName);

        document.appendChild(root);

        try {
            while (true) {

                int next = reader.next();
                switch (next) {
                case START_ELEMENT:

                    QName childName = new QName(reader.getNamespaceURI(), reader.getLocalName());
                    Element child = createElement(reader, document, childName);

                    root.appendChild(child);
                    root = child;

                    break;

                case CHARACTERS:
                case CDATA:
                    Text text = document.createTextNode(reader.getText());
                    root.appendChild(text);
                    break;
                case END_ELEMENT:
                    if (rootName.equals(reader.getName())) {
                        return document;
                    }
                    root = (Element) root.getParentNode();
                case ENTITY_REFERENCE:
                case COMMENT:
                case SPACE:
                case PROCESSING_INSTRUCTION:
                case DTD:
                    break;
                }
            }
        } catch (XMLStreamException e) {
            throw new TransformationException(e);
        }

    }

    private DocumentBuilder getDocumentBuilder() throws TransformationException {
        try {
            return FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new TransformationException(e);
        }
    }

    /*
     * Creates the element and populates the namespace declarations and attributes.
     */
    private Element createElement(XMLStreamReader reader, Document document, QName rootName) {

        Element root = document.createElementNS(rootName.getNamespaceURI(), rootName.getLocalPart());

        // Handle namespace declarations
        for (int i = 0; i < reader.getNamespaceCount(); i++) {

            String prefix = reader.getNamespacePrefix(i);
            String uri = reader.getNamespaceURI(i);

            prefix = prefix == null ? "xmlns" : "xmlns:" + prefix;

            root.setAttribute(prefix, uri);

        }

        // Handle attributes
        for (int i = 0; i < reader.getAttributeCount(); i++) {

            String attributeNs = reader.getAttributeNamespace(i);
            String localName = reader.getAttributeLocalName(i);
            String value = reader.getAttributeValue(i);
            String attributePrefix = reader.getAttributePrefix(i);
            String qualifiedName = attributePrefix == null || "".equals(attributePrefix) ? localName : attributePrefix + ":" + localName;
            
            if (attributeNs != null) {
                root.setAttributeNS(attributeNs, qualifiedName, value);
            } else {
                root.setAttribute(qualifiedName, value);
            }

        }

        return root;

    }

}
