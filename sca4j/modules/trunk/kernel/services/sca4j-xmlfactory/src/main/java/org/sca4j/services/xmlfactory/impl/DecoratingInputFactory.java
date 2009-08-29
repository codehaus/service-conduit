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
package org.sca4j.services.xmlfactory.impl;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.EventFilter;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;

import org.sca4j.host.expression.ExpressionExpander;

/**
 * @author meerajk
 *
 */
public class DecoratingInputFactory extends XMLInputFactory {
    
    private final XMLInputFactory wrappedFactory;
    private final ExpressionExpander expressionExpander;

    public DecoratingInputFactory(XMLInputFactory wrappedFactory, ExpressionExpander expressionExpander) {
        this.wrappedFactory = wrappedFactory;
        this.expressionExpander = expressionExpander;
    }
    
    public XMLEventReader createFilteredReader(XMLEventReader arg0, EventFilter arg1) throws XMLStreamException {
        return wrappedFactory.createFilteredReader(arg0, arg1);
    }

    public XMLStreamReader createFilteredReader(XMLStreamReader arg0, StreamFilter arg1) throws XMLStreamException {
        return wrappedFactory.createFilteredReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(InputStream arg0, String arg1) throws XMLStreamException {
        return wrappedFactory.createXMLEventReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(InputStream arg0) throws XMLStreamException {
        return wrappedFactory.createXMLEventReader(arg0);
    }

    public XMLEventReader createXMLEventReader(Reader arg0) throws XMLStreamException {
        return wrappedFactory.createXMLEventReader(arg0);
    }

    public XMLEventReader createXMLEventReader(Source arg0) throws XMLStreamException {
        return wrappedFactory.createXMLEventReader(arg0);
    }

    public XMLEventReader createXMLEventReader(String arg0, InputStream arg1) throws XMLStreamException {
        return wrappedFactory.createXMLEventReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(String arg0, Reader arg1) throws XMLStreamException {
        return wrappedFactory.createXMLEventReader(arg0, arg1);
    }

    public XMLEventReader createXMLEventReader(XMLStreamReader arg0) throws XMLStreamException {
        return wrappedFactory.createXMLEventReader(arg0);
    }

    public XMLStreamReader createXMLStreamReader(InputStream arg0, String arg1) throws XMLStreamException {
        return new DecoratingStreamReader(wrappedFactory.createXMLStreamReader(arg0, arg1), expressionExpander);
    }

    public XMLStreamReader createXMLStreamReader(InputStream arg0) throws XMLStreamException {
        return new DecoratingStreamReader(wrappedFactory.createXMLStreamReader(arg0), expressionExpander);
    }

    public XMLStreamReader createXMLStreamReader(Reader arg0) throws XMLStreamException {
        return new DecoratingStreamReader(wrappedFactory.createXMLStreamReader(arg0), expressionExpander);
    }

    public XMLStreamReader createXMLStreamReader(Source arg0) throws XMLStreamException {
        return new DecoratingStreamReader(wrappedFactory.createXMLStreamReader(arg0), expressionExpander);
    }

    public XMLStreamReader createXMLStreamReader(String arg0, InputStream arg1) throws XMLStreamException {
        return new DecoratingStreamReader(wrappedFactory.createXMLStreamReader(arg0, arg1), expressionExpander);
    }

    public XMLStreamReader createXMLStreamReader(String arg0, Reader arg1) throws XMLStreamException {
        return new DecoratingStreamReader(wrappedFactory.createXMLStreamReader(arg0, arg1), expressionExpander);
    }

    public boolean equals(Object arg0) {
        return wrappedFactory.equals(arg0);
    }

    public XMLEventAllocator getEventAllocator() {
        return wrappedFactory.getEventAllocator();
    }

    public Object getProperty(String arg0) throws IllegalArgumentException {
        return wrappedFactory.getProperty(arg0);
    }

    public XMLReporter getXMLReporter() {
        return wrappedFactory.getXMLReporter();
    }

    public XMLResolver getXMLResolver() {
        return wrappedFactory.getXMLResolver();
    }

    public int hashCode() {
        return wrappedFactory.hashCode();
    }

    public boolean isPropertySupported(String arg0) {
        return wrappedFactory.isPropertySupported(arg0);
    }

    public void setEventAllocator(XMLEventAllocator arg0) {
        wrappedFactory.setEventAllocator(arg0);
    }

    public void setProperty(String arg0, Object arg1) throws IllegalArgumentException {
        wrappedFactory.setProperty(arg0, arg1);
    }

    public void setXMLReporter(XMLReporter arg0) {
        wrappedFactory.setXMLReporter(arg0);
    }

    public void setXMLResolver(XMLResolver arg0) {
        wrappedFactory.setXMLResolver(arg0);
    }

    public String toString() {
        return wrappedFactory.toString();
    }

}
