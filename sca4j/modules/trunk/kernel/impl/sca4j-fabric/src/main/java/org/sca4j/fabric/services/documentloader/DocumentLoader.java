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
package org.sca4j.fabric.services.documentloader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Service interface for loading XML documents from a file as DOM objects.
 *
 * @version $Rev: 2420 $ $Date: 2008-01-01 21:13:35 +0000 (Tue, 01 Jan 2008) $
 */
public interface DocumentLoader {
    /**
     * Loads a Document from a local file.
     *
     * @param file the file containing the XML document
     * @return the content of the file as a Document
     * @throws IOException  if there was a problem reading the file
     * @throws SAXException if there was a problem with the document
     */
    Document load(File file) throws IOException, SAXException;

    /**
     * Loads a Document from a physical resource.
     *
     * @param url the location of the resource
     * @return the content of the resource as a Document
     * @throws IOException  if there was a problem reading the resource
     * @throws SAXException if there was a problem with the document
     */
    Document load(URL url) throws IOException, SAXException;

    /**
     * Loads a Document from a logical resource.
     * <p/>
     * How the resource is converted to a physical location is implementation defined.
     *
     * @param uri the logical location of the resource
     * @return the content of the resource as a Document
     * @throws IOException  if there was a problem reading the resource
     * @throws SAXException if there was a problem with the document
     */
    Document load(URI uri) throws IOException, SAXException;

    /**
     * Loads a Document from a logical source.
     *
     * @param source the source of the document text
     * @return the content as a Document
     * @throws IOException  if there was a problem reading the content
     * @throws SAXException if there was a problem with the document
     */
    Document load(InputSource source) throws IOException, SAXException;
}
