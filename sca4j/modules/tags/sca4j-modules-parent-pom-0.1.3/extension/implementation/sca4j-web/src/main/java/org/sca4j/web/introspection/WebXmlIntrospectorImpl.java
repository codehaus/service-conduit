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
package org.sca4j.web.introspection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;

import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.scdl.validation.MissingResource;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.Resource;

/**
 * Default implementation of WebXmlIntrospector.
 *
 * @version $Revision$ $Date$
 */
public class WebXmlIntrospectorImpl implements WebXmlIntrospector {
    private static final QNameSymbol WEB_APP_NO_NAMESPACE = new QNameSymbol(new QName(null, "web-app"));
    private static final QNameSymbol WEB_APP_NAMESPACE = new QNameSymbol(new QName("http://java.sun.com/xml/ns/j2ee", "web-app"));

    private MetaDataStore store;
    private XMLInputFactory xmlFactory;

    public WebXmlIntrospectorImpl(@Reference MetaDataStore store, @Reference XMLFactory factory) {
        this.store = store;
        this.xmlFactory = factory.newInputFactoryInstance();
    }

    public List<Class<?>> introspectArtifactClasses(IntrospectionContext context) {
        List<Class<?>> artifacts = new ArrayList<Class<?>>();
        ClassLoader cl = context.getTargetClassLoader();
        Resource resource = store.resolveContainingResource(context.getContributionUri(), WEB_APP_NAMESPACE);
        if (resource == null) {
            resource = store.resolveContainingResource(context.getContributionUri(), WEB_APP_NO_NAMESPACE);
            if (resource == null) {
                // tolerate no web.xml
                return artifacts;
            }
        }
        InputStream xmlStream = null;
        try {
            xmlStream = resource.getUrl().openStream();
            XMLStreamReader reader = xmlFactory.createXMLStreamReader(xmlStream);
            while (true) {
                // only match on local part since namespaces may be omitted
                int event = reader.next();
                switch (event) {
                case START_ELEMENT:
                    String name = reader.getName().getLocalPart();
                    if (name.equals("servlet-class")) {
                        String className = reader.getElementText();
                        try {
                            artifacts.add(cl.loadClass(className.trim()));
                        } catch (ClassNotFoundException e) {
                            MissingResource failure = new MissingResource("Servlet class not found: " + className, className);
                            context.addError(failure);
                        }
                    } else if (name.equals("filter-class")) {
                        String className = reader.getElementText();
                        try {
                            artifacts.add(cl.loadClass(className.trim()));
                        } catch (ClassNotFoundException e) {
                            MissingResource failure = new MissingResource("Filter class not found: " + className, className);
                            context.addError(failure);
                        }
                    }
                    break;
                case END_ELEMENT:
                    if (reader.getName().getLocalPart().equals("web-app")) {
                        return artifacts;
                    }
                    break;
                case END_DOCUMENT:
                    return artifacts;
                }
            }
        } catch (XMLStreamException e) {
            InvalidWebManifest failure = new InvalidWebManifest("Error reading web.xml", e);
            context.addError(failure);
        } catch (IOException e) {
            InvalidWebManifest failure = new InvalidWebManifest("Error reading web.xml", e);
            context.addError(failure);
        } finally {
            try {
                if (xmlStream != null) {
                    xmlStream.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        return artifacts;

    }
}
