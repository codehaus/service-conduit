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
package org.sca4j.services.xmlfactory.impl;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.expression.ExpressionExpander;

public class DecoratingStreamReader implements XMLStreamReader {
    
    private final XMLStreamReader wrappedStreamReader;
    private final ExpressionExpander expressionExpander;
    
    public DecoratingStreamReader(XMLStreamReader wrappedStreamReader, ExpressionExpander expressionExpander) {
        this.wrappedStreamReader = wrappedStreamReader;
        this.expressionExpander = expressionExpander;
    }
    
    public void close() throws XMLStreamException {
        wrappedStreamReader.close();
    }

    public int getAttributeCount() {
        return wrappedStreamReader.getAttributeCount();
    }

    public String getAttributeLocalName(int arg0) {
        return wrappedStreamReader.getAttributeLocalName(arg0);
    }

    public QName getAttributeName(int arg0) {
        return wrappedStreamReader.getAttributeName(arg0);
    }

    public String getAttributeNamespace(int arg0) {
        return wrappedStreamReader.getAttributeNamespace(arg0);
    }

    public String getAttributePrefix(int arg0) {
        return wrappedStreamReader.getAttributePrefix(arg0);
    }

    public String getAttributeType(int arg0) {
        return wrappedStreamReader.getAttributeType(arg0);
    }

    public String getText() {
        String val = wrappedStreamReader.getText();
        return val == null ? null : expressionExpander.expand(val);
    }

    public String getElementText() throws XMLStreamException {
        String val = wrappedStreamReader.getElementText();
        return val == null ? null : expressionExpander.expand(val);
    }

    public char[] getTextCharacters() {
        char[] val = wrappedStreamReader.getTextCharacters();
        return val == null ? null : expressionExpander.expand(new String(val)).toCharArray();
    }

    public String getAttributeValue(int arg0) {
        String val = wrappedStreamReader.getAttributeValue(arg0);
        return val == null ? null : expressionExpander.expand(val);
    }

    public String getAttributeValue(String arg0, String arg1) {
        String val = wrappedStreamReader.getAttributeValue(arg0, arg1);
        return val == null ? null : expressionExpander.expand(val);
    }

    public String getCharacterEncodingScheme() {
        return wrappedStreamReader.getCharacterEncodingScheme();
    }

    public String getEncoding() {
        return wrappedStreamReader.getEncoding();
    }

    public int getEventType() {
        return wrappedStreamReader.getEventType();
    }

    public String getLocalName() {
        return wrappedStreamReader.getLocalName();
    }

    public Location getLocation() {
        return wrappedStreamReader.getLocation();
    }

    public QName getName() {
        return wrappedStreamReader.getName();
    }

    public NamespaceContext getNamespaceContext() {
        return wrappedStreamReader.getNamespaceContext();
    }

    public int getNamespaceCount() {
        return wrappedStreamReader.getNamespaceCount();
    }

    public String getNamespacePrefix(int arg0) {
        return wrappedStreamReader.getNamespacePrefix(arg0);
    }

    public String getNamespaceURI() {
        return wrappedStreamReader.getNamespaceURI();
    }

    public String getNamespaceURI(int arg0) {
        return wrappedStreamReader.getNamespaceURI(arg0);
    }

    public String getNamespaceURI(String arg0) {
        return wrappedStreamReader.getNamespaceURI(arg0);
    }

    public String getPIData() {
        return wrappedStreamReader.getPIData();
    }

    public String getPITarget() {
        return wrappedStreamReader.getPITarget();
    }

    public String getPrefix() {
        return wrappedStreamReader.getPrefix();
    }

    public Object getProperty(String arg0) throws IllegalArgumentException {
        return wrappedStreamReader.getProperty(arg0);
    }

    public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException {
        return wrappedStreamReader.getTextCharacters(arg0, arg1, arg2, arg3);
    }

    public int getTextLength() {
        return wrappedStreamReader.getTextLength();
    }

    public int getTextStart() {
        return wrappedStreamReader.getTextStart();
    }

    public String getVersion() {
        return wrappedStreamReader.getVersion();
    }

    public boolean hasName() {
        return wrappedStreamReader.hasName();
    }

    public boolean hasNext() throws XMLStreamException {
        return wrappedStreamReader.hasNext();
    }

    public boolean hasText() {
        return wrappedStreamReader.hasText();
    }

    public boolean isAttributeSpecified(int arg0) {
        return wrappedStreamReader.isAttributeSpecified(arg0);
    }

    public boolean isCharacters() {
        return wrappedStreamReader.isCharacters();
    }

    public boolean isEndElement() {
        return wrappedStreamReader.isEndElement();
    }

    public boolean isStandalone() {
        return wrappedStreamReader.isStandalone();
    }

    public boolean isStartElement() {
        return wrappedStreamReader.isStartElement();
    }

    public boolean isWhiteSpace() {
        return wrappedStreamReader.isWhiteSpace();
    }

    public int next() throws XMLStreamException {
        return wrappedStreamReader.next();
    }

    public int nextTag() throws XMLStreamException {
        return wrappedStreamReader.nextTag();
    }

    public void require(int arg0, String arg1, String arg2) throws XMLStreamException {
        wrappedStreamReader.require(arg0, arg1, arg2);
    }

    public boolean standaloneSet() {
        return wrappedStreamReader.standaloneSet();
    }

}
