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
