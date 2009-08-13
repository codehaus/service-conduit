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
