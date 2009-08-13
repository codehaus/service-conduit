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
package org.sca4j.introspection.xml;

import java.net.URL;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.introspection.IntrospectionContext;

/**
 * System service for loading configuration artifacts from an XML source.
 *
 * @version $Rev: 4301 $ $Date: 2008-05-23 06:33:58 +0100 (Fri, 23 May 2008) $
 */
public interface Loader {
    /**
     * Parse the supplied XML stream, dispatching to the appropriate registered loader for each element encountered in the stream.
     * <p/>
     * This method must be called with the XML cursor positioned on a START_ELEMENT event. When this method returns, the stream will be positioned on
     * the corresponding END_ELEMENT event.
     *
     * @param reader  the XML stream to parse
     * @param type    the type of Java object that should be returned
     * @param context the current loader context
     * @return the model object obtained by parsing the current element on the stream
     * @throws UnrecognizedElementException if no loader was found for the element
     * @throws XMLStreamException           if there was a problem reading the stream
     * @throws ClassCastException           if the XML type cannot be cast to the expected output type
     */
    <OUTPUT> OUTPUT load(XMLStreamReader reader, Class<OUTPUT> type, IntrospectionContext context)
            throws XMLStreamException, UnrecognizedElementException;

    /**
     * Load a model object from a specified location.
     *
     * @param url     the location of an XML document to be loaded
     * @param type    the type of Java Object that should be returned
     * @param context the current loader context
     * @return the model ojbect loaded from the document
     * @throws LoaderException    if there was a problem loading the document
     * @throws ClassCastException if the XML type cannot be cast to the expected output type
     */
    <OUTPUT> OUTPUT load(URL url, Class<OUTPUT> type, IntrospectionContext context) throws LoaderException;
}
