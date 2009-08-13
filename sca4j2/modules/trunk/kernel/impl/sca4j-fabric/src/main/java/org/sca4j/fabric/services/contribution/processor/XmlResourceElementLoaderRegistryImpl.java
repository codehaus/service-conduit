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
package org.sca4j.fabric.services.contribution.processor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.XmlResourceElementLoader;
import org.sca4j.spi.services.contribution.XmlResourceElementLoaderRegistry;

/**
 * Default impelmentation of an XmlIndexerRegistry.
 *
 * @version $Rev: 4302 $ $Date: 2008-05-23 10:41:13 +0100 (Fri, 23 May 2008) $
 */
public class XmlResourceElementLoaderRegistryImpl implements XmlResourceElementLoaderRegistry {
    private Map<QName, XmlResourceElementLoader> cache = new HashMap<QName, XmlResourceElementLoader>();

    public void register(XmlResourceElementLoader loader) {
        cache.put(loader.getType(), loader);
    }

    public void unregister(QName name) {
        cache.remove(name);
    }

    @SuppressWarnings({"unchecked"})
    public void load(XMLStreamReader reader, URI contributionUri, Resource resource, ValidationContext context,  ClassLoader loader)
            throws ContributionException, XMLStreamException {
        try {
            QName name = reader.getName();
            XmlResourceElementLoader elementLoader = cache.get(name);
            if (elementLoader == null) {
                return;
            }
            elementLoader.load(reader, contributionUri, resource, context, loader);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }

    }
}
