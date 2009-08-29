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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.runtime.webapp.contribution;

import java.io.Serializable;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Property;

import org.sca4j.spi.services.contribution.XmlIndexer;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.XmlIndexerRegistry;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;

/**
 * Adds an index entry for the web.xml descriptor to the symbol space of a WAR contribution.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
public class WebXmlIndexer implements XmlIndexer {
    private static final QName WEB_APP_NO_NAMESPACE = new QName(null, "web-app");
    private static final QName WEB_APP_NAMESPACE = new QName("http://java.sun.com/xml/ns/j2ee", "web-app");

    private XmlIndexerRegistry registry;
    private boolean namespace;

    public WebXmlIndexer(@Reference XmlIndexerRegistry registry, @Property(name = "namespace")boolean namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    @Init
    public void init() {
        registry.register(this);
    }

    public QName getType() {
        if (namespace) {
            return WEB_APP_NAMESPACE;
        } else {
            return WEB_APP_NO_NAMESPACE;
        }
    }

    public void index(Resource resource, XMLStreamReader reader, ValidationContext context) throws ContributionException {
        QNameSymbol symbol;
        if (namespace) {
            symbol = new QNameSymbol(WEB_APP_NAMESPACE);
        } else {
            symbol = new QNameSymbol(WEB_APP_NO_NAMESPACE);
        }
        ResourceElement<QNameSymbol, Serializable> element = new ResourceElement<QNameSymbol, Serializable>(symbol);
        resource.addResourceElement(element);
    }
}
