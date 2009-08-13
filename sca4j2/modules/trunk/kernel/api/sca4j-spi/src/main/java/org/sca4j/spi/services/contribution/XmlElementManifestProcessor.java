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
 * Processes an XML-based entry that contains manifest information. Invoked when a contribution is introspected and an XML content type is encountered
 * with a document element type corresponding to the one handled by the processor implementation.
 *
 * @version $Rev: 4312 $ $Date: 2008-05-23 23:29:23 +0100 (Fri, 23 May 2008) $
 */
public interface XmlElementManifestProcessor {

    /**
     * Returns the QName for the type of XML entry handled by this processor
     *
     * @return the QName
     */
    QName getType();

    /**
     * Processes the XML contribution
     *
     * @param manifest the contribution manifest to update
     * @param reader   the reader positioned at the first element of the document
     * @param context  the context to which validation errors and warnings are reported
     * @throws ContributionException if an error occurs processing
     */
    void process(ContributionManifest manifest, XMLStreamReader reader, ValidationContext context) throws ContributionException;
}
