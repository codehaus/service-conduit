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
