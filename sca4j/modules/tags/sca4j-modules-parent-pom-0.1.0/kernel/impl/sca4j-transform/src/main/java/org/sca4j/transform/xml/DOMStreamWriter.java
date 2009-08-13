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
package org.sca4j.transform.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @version $Rev: 31 $ $Date: 2007-05-16 04:30:32 +0100 (Wed, 16 May 2007) $
 */
public class DOMStreamWriter implements XMLStreamWriter {
    private final Document root;
    private final Node result;

    private NamespaceContext namespaceContext;
    private Node node;

    public DOMStreamWriter(Document root, Node result) {
        this.root = root;
        this.result = result;
        this.node = result;
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        // ignore encoding as in-memory DOM does not support setting it
        writeStartDocument(version);
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        writeStartDocument();
        root.setXmlVersion(version);
    }

    public void writeStartDocument() throws XMLStreamException {
        if (result != root) {
            throw new XMLStreamException("Result node is not the Document");
        }

        // remove all child nodes to reset the document
        Node child;
        while ((child = root.getLastChild()) != null) {
            root.removeChild(child);
        }
        namespaceContext = null;
    }


    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) {
        if (namespaceContext != null || node != root) {
            throw new IllegalStateException();
        }
        this.namespaceContext = namespaceContext;
    }

    public void writeEndDocument() throws XMLStreamException {
    }


    public void writeStartElement(String localName) throws XMLStreamException {
        Element element = root.createElement(localName);
        node.appendChild(element);
        node = element;
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        Element element = root.createElementNS(namespaceURI, addPrefix(namespaceURI, localName));
        node.appendChild(element);
        node = element;
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        Element element = root.createElementNS(namespaceURI, prefix + ':' + localName);
        node.appendChild(element);
        node = element;
    }

    private String addPrefix(String namespaceURI, String localName) {
        String prefix = node.lookupPrefix(namespaceURI);
        if (prefix == null) {
            // TODO default prefix
            throw new UnsupportedOperationException();
        }
        return prefix + ':' + localName;
    }

    public void writeEndElement() throws XMLStreamException {
        node = node.getParentNode();
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        writeStartElement(localName);
        writeEndElement();
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        writeStartElement(namespaceURI, localName);
        writeEndElement();
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        if (node instanceof Element) {
            Element element = (Element) node;
            element.setAttribute(localName, value);
        } else {
            throw new IllegalStateException();
        }
    }

    public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
            throws XMLStreamException {
        if (node instanceof Element) {
            Element element = (Element) node;
            element.setAttributeNS(namespaceURI, prefix + ':' + localName, value);
        } else {
            throw new IllegalStateException();
        }
    }

    public void writeAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException {
        if (node instanceof Element) {
            Element element = (Element) node;
            element.setAttributeNS(namespaceURI, addPrefix(namespaceURI, localName), value);
        } else {
            throw new IllegalStateException();
        }
    }

    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0 || "xmlns".equals(prefix)) {
            writeDefaultNamespace(namespaceURI);
        } else {
            setPrefix(prefix, namespaceURI);
        }
    }

    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        setDefaultNamespace(namespaceURI);
    }

    public void writeComment(String string) throws XMLStreamException {
        node.appendChild(root.createComment(string));
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        node.appendChild(root.createProcessingInstruction(target, null));
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        node.appendChild(root.createProcessingInstruction(target, data));
    }

    public void writeCData(String data) throws XMLStreamException {
        node.appendChild(root.createCDATASection(data));

    }

    public void writeDTD(String string) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        node.appendChild(root.createEntityReference(name));
    }

    public void writeCharacters(String text) throws XMLStreamException {
        node.appendChild(root.createTextNode(text));
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        node.appendChild(root.createTextNode(new String(text, start, len)));
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return node.lookupPrefix(uri);
    }

    public void setPrefix(String prefix, String namespaceURI) throws XMLStreamException {
        if (node instanceof Element) {
            Element element = (Element) node;
            element.setAttributeNS(namespaceURI, "xmlns:" + prefix, namespaceURI);
        } else {
            throw new IllegalStateException();
        }
    }

    public void setDefaultNamespace(String namespaceURI) throws XMLStreamException {
        if (node instanceof Element) {
            Element element = (Element) node;
            element.setAttributeNS(namespaceURI, "xmlns", namespaceURI);
        } else {
            throw new IllegalStateException();
        }
    }

    public Object getProperty(String string) throws IllegalArgumentException {
        return null;
    }

    public void flush() throws XMLStreamException {
    }

    public void close() throws XMLStreamException {
    }
}
