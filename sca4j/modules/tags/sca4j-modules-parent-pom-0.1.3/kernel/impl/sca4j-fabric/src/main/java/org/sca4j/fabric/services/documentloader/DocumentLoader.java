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
