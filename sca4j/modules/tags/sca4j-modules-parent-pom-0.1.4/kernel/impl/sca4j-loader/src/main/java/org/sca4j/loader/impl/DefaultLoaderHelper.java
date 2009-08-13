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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.loader.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.sca4j.host.Namespaces;
import org.sca4j.introspection.xml.InvalidPrefixException;
import org.sca4j.introspection.xml.LoaderHelper;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.scdl.PolicyAware;
import org.sca4j.transform.TransformationException;
import org.sca4j.transform.xml.Stream2Element2;

/**
 * Default implementation of the loader helper.
 * 
 * @version $Revision$ $Date$
 */
public class DefaultLoaderHelper implements LoaderHelper {
    private final Stream2Element2 stream2Element;
    private final DocumentBuilder builder;

    public DefaultLoaderHelper() {
        stream2Element = new Stream2Element2();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Load the value of the attribute key from the current element.
     * 
     * @param reader a stream containing a property value
     * @return a standalone document containing the value
     * @throws javax.xml.stream.XMLStreamException if there was a problem reading the stream
     */
    public Document loadKey(XMLStreamReader reader) {

        String key = reader.getAttributeValue(Namespaces.SCA4J_NS, "key");
        if (key == null) {
            return null;
        }

        // create a document with a root element to hold the key value
        Document document = builder.newDocument();
        Element element = document.createElement("key");
        document.appendChild(element);

        // TODO: we should copy all in-context namespaces to the declaration if we can find what they are
        // in the mean time, see if the value looks like it might contain a prefix
        int index = key.indexOf(':');
        if (index != -1) {
            String prefix = key.substring(0, index);
            String uri = reader.getNamespaceURI(prefix);
            if (uri != null) {
                element.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + prefix, uri);
            }
        }
        // set the text value
        element.appendChild(document.createTextNode(key));
        return document;

    }

    public Document loadValue(XMLStreamReader reader) throws XMLStreamException {
        Document value = builder.newDocument();
        Element root = value.createElement("value");
        value.appendChild(root);
        try {
            stream2Element.transform(reader, root, null);
        } catch (TransformationException e) {
            throw (XMLStreamException) e.getCause();
        }
        return value;
    }

    public void loadPolicySetsAndIntents(PolicyAware policyAware, XMLStreamReader reader, IntrospectionContext context) {

        try {
            policyAware.setIntents(parseListOfQNames(reader, "requires"));
            policyAware.setPolicySets(parseListOfQNames(reader, "policySets"));
        } catch (InvalidPrefixException e) {
            context.addError(new InvalidQNamePrefix(e.getPrefix(), reader));
        }

    }

    public Set<QName> parseListOfQNames(XMLStreamReader reader, String attribute) throws InvalidPrefixException {

        Set<QName> qNames = new HashSet<QName>();

        String val = reader.getAttributeValue(null, attribute);
        if (val != null) {
            StringTokenizer tok = new StringTokenizer(val);
            while (tok.hasMoreElements()) {
                qNames.add(createQName(tok.nextToken(), reader));
            }
        }

        return qNames;

    }

    public QName createQName(String name, XMLStreamReader reader) throws InvalidPrefixException {
        QName qName;
        int index = name.indexOf(':');
        if (index != -1) {
            String prefix = name.substring(0, index);
            String localPart = name.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                throw new InvalidPrefixException("Invalid prefix: " + prefix, prefix, reader);
            }
            qName = new QName(ns, localPart, prefix);
        } else {
            String prefix = "";
            String ns = reader.getNamespaceURI();
            qName = new QName(ns, name, prefix);
        }
        return qName;
    }

    public URI getURI(String target) {
        if (target == null) {
            return null;
        }

        int index = target.lastIndexOf('/');
        if (index == -1) {
            return URI.create(target);
        } else {
            String uri = target.substring(0, index);
            String fragment = target.substring(index + 1);
            return URI.create(uri + '#' + fragment);
        }
    }

    public List<URI> parseListOfUris(XMLStreamReader reader, String attribute) {
        String value = reader.getAttributeValue(null, attribute);
        if (value == null || value.length() == 0) {
            return null;
        } else {
            StringTokenizer tok = new StringTokenizer(value);
            List<URI> result = new ArrayList<URI>(tok.countTokens());
            while (tok.hasMoreTokens()) {
                result.add(getURI(tok.nextToken().trim()));
            }
            return result;
        }
    }

}
