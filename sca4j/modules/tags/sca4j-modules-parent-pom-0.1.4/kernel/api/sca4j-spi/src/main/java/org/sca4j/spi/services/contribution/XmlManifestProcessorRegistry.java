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
 * A registry of XmlElementManifestProcessors.
 *
 * @version $Rev: 4312 $ $Date: 2008-05-23 23:29:23 +0100 (Fri, 23 May 2008) $
 */
public interface XmlManifestProcessorRegistry {
    /**
     * Register a XmlElementManifestProcessor using the processor's QName type as the key
     *
     * @param processor the processor to register
     */
    void register(XmlElementManifestProcessor processor);

    /**
     * Unregister an XmlElementManifestProcessor for a QName
     *
     * @param name the QName
     */
    void unregisterProcessor(QName name);

    /**
     * Dispatches to an XmlElementManifestProcessor based on the given Qname.
     *
     * @param name     the document element type to dispatch on
     * @param manifest the manifest being processed
     * @param reader   the reader position on the document element start tag
     * @param context  the context to which validation errors and warnings are reported
     * @throws ContributionException if an error occurs during processing
     */
    void process(QName name, ContributionManifest manifest, XMLStreamReader reader, ValidationContext context) throws ContributionException;

}
