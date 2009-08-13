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

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.XmlElementManifestProcessor;
import org.sca4j.spi.services.contribution.XmlManifestProcessorRegistry;
import org.sca4j.scdl.ValidationContext;

/**
 * Default implementation of XmlManifestProcessorRegistry.
 *
 * @version $Rev: 4312 $ $Date: 2008-05-23 23:29:23 +0100 (Fri, 23 May 2008) $
 */
public class XmlManifestProcessorRegistryImpl implements XmlManifestProcessorRegistry {

    private Map<QName, XmlElementManifestProcessor> cache = new HashMap<QName, XmlElementManifestProcessor>();

    public void register(XmlElementManifestProcessor processor) {
        cache.put(processor.getType(), processor);
    }

    public void unregisterProcessor(QName name) {
        cache.remove(name);
    }

    public void process(QName name, ContributionManifest manifest, XMLStreamReader reader, ValidationContext context) throws ContributionException {
        XmlElementManifestProcessor processor = cache.get(name);
        if (processor == null) {
            return;
        }
        processor.process(manifest, reader, context);
    }
}
