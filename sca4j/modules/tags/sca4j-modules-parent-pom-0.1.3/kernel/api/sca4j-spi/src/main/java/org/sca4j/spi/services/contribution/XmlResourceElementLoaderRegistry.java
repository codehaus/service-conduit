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
package org.sca4j.spi.services.contribution;

import java.net.URI;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;

/**
 * A registry of XmlResourceElementLoaders
 *
 * @version $Rev: 4302 $ $Date: 2008-05-23 10:41:13 +0100 (Fri, 23 May 2008) $
 */
public interface XmlResourceElementLoaderRegistry {
    /**
     * Register a XmlResourceElementLoader using the processor's QName type as the key
     *
     * @param indexer the indexer to register
     */
    void register(XmlResourceElementLoader indexer);

    /**
     * Unregister an XmlResourceElementLoader for a QName
     *
     * @param name the QName
     */
    void unregister(QName name);

    /**
     * Dispatches to an XmlResourceElementLoader to loads an element in an XML resource
     *
     * @param reader          the StAX reader, positioned at the start of the element to laod
     * @param contributionUri the current contribution URI
     * @param resource        the resource
     * @param context         the context to which validation errors and warnings are reported
     * @param loader          the classloader to resolve resources with
     * @throws ContributionException if a fatal error loading the resource occurs
     * @throws XMLStreamException    if an error parsing the XML stream occurs
     */
    void load(XMLStreamReader reader, URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader)
            throws ContributionException, XMLStreamException;

}
