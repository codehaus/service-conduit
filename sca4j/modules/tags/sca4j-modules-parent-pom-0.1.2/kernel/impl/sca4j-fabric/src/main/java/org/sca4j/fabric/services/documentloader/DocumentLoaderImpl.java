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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.sca4j.fabric.util.IOHelper;

/**
 * Default implementation that creates a new DocumentBuilder for every invocation.
 * <p/>
 * URI resolution is handled by the underlying JAXP implementation.
 *
 * @version $Rev: 2960 $ $Date: 2008-02-29 17:26:32 +0000 (Fri, 29 Feb 2008) $
 */
public class DocumentLoaderImpl implements DocumentLoader {
    private static final DocumentBuilderFactory DOCUMENT_FACTORY;

    static {
        DOCUMENT_FACTORY = DocumentBuilderFactory.newInstance();
        DOCUMENT_FACTORY.setNamespaceAware(true);
    }

    public Document load(File file) throws IOException, SAXException {
        DocumentBuilder builder = getBuilder();
        return builder.parse(file);
    }

    public Document load(URL url) throws IOException, SAXException {
        InputStream stream = url.openStream();
        try {
            stream = new BufferedInputStream(stream);
            DocumentBuilder builder = getBuilder();
            return builder.parse(stream);
        } finally {
            IOHelper.closeQueitly(stream);
        }
    }

    public Document load(URI uri) throws IOException, SAXException {
        DocumentBuilder builder = getBuilder();
        return builder.parse(uri.toString());
    }

    public Document load(InputSource source) throws IOException, SAXException {
        DocumentBuilder builder = getBuilder();
        return builder.parse(source);
    }

    private DocumentBuilder getBuilder() {
        try {
            return DOCUMENT_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }
}
