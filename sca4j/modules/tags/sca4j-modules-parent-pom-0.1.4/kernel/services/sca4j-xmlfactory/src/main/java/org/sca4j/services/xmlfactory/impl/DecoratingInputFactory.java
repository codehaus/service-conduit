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
