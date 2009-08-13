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
package org.sca4j.fabric.services.contribution.manifest;

import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.ManifestProcessor;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;

/**
 * Processes XML artifacts in a contribution that contain manifest information. Dispatches to {@link org.sca4j.spi.services.contribution.XmlElementManifestProcessor}
 * based on the document element type for further processing.
 *
 * @version $Rev: 4312 $ $Date: 2008-05-23 23:29:23 +0100 (Fri, 23 May 2008) $
 */
@EagerInit
public class XmlManifestProcessor implements ManifestProcessor {
    private ProcessorRegistry processorRegistry;
    private XmlManifestProcessorRegistry manifestProcessorRegistry;
    private XMLInputFactory xmlFactory;

    public XmlManifestProcessor(@Reference ProcessorRegistry registry,
                                @Reference XmlManifestProcessorRegistry manifestProcessorRegistry,
                                @Reference XMLFactory xmlFactory) {
        this.processorRegistry = registry;
        this.manifestProcessorRegistry = manifestProcessorRegistry;
        this.xmlFactory = xmlFactory.newInputFactoryInstance();
    }

    @Init
    public void init() {
        processorRegistry.register(this);
    }

    public String getContentType() {
        return "application/xml";
    }

    public void process(ContributionManifest manifest, InputStream stream, ValidationContext context) throws ContributionException {
        XMLStreamReader reader = null;
        try {
            reader = xmlFactory.createXMLStreamReader(stream);
            while (reader.hasNext() && XMLStreamConstants.START_ELEMENT != reader.getEventType()) {
                reader.next();
            }
            if (XMLStreamConstants.END_DOCUMENT == reader.getEventType()) {
                return;
            }
            manifestProcessorRegistry.process(reader.getName(), manifest, reader, context);
        } catch (XMLStreamException e) {
            throw new ContributionException(e);
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
