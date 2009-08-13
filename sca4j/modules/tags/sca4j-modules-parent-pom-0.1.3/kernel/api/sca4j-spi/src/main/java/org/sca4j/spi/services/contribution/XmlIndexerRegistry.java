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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;

/**
 * A registry of XmlIndexers
 *
 * @version $Rev: 4313 $ $Date: 2008-05-24 00:06:47 +0100 (Sat, 24 May 2008) $
 */
public interface XmlIndexerRegistry {
    /**
     * Register a XmlIndexer using the processor's QName type as the key
     *
     * @param indexer the indexer to register
     */
    void register(XmlIndexer indexer);

    /**
     * Unregister an XmlIndexer for a QName
     *
     * @param name the QName
     */
    void unregister(QName name);

    /**
     * Dispatch to an XMLIndexer based on the element type of the resource document tag.
     *
     * @param resource the resource being indexed
     * @param reader   the reader positioned on the start element of the first tag
     * @param context  the context to which validation errors and warnings are reported
     * @throws ContributionException if an error occurs during indexing
     */
    void index(Resource resource, XMLStreamReader reader, ValidationContext context) throws ContributionException;

}
