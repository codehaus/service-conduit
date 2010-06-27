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
 */
package org.sca4j.bpel.lightweight.introspection;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.sca4j.bpel.lightweight.model.BpelProcessDefinition;
import org.sca4j.introspection.xml.LoaderException;

/**
 * TODO Add stricter structural checks
 */
public class BpelProcessIntrospector {

    private BpelProcessLoader bpelProcessLoader = new BpelProcessLoader();
    
    public BpelProcessDefinition introspect(InputStream inputStream) throws LoaderException {

        XMLStreamReader xmlStreamReader = null;
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
            while (xmlStreamReader.next() != START_ELEMENT) {
            }
            return bpelProcessLoader.load(xmlStreamReader, null);
        } catch (XMLStreamException e) {
            throw new LoaderException(e.getMessage(), xmlStreamReader, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

    }

}
