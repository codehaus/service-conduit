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

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.sca4j.host.contribution.ContributionException;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.XmlProcessor;
import org.sca4j.spi.services.contribution.XmlProcessorRegistry;
import org.sca4j.scdl.ValidationContext;

/**
 * Default impelmentation of an XmlProcessorRegistry.
 *
 * @version $Rev: 4302 $ $Date: 2008-05-23 10:41:13 +0100 (Fri, 23 May 2008) $
 */
public class XmlProcessorRegistryImpl implements XmlProcessorRegistry {
    private Map<QName, XmlProcessor> cache = new HashMap<QName, XmlProcessor>();

    public void register(XmlProcessor processor) {
        cache.put(processor.getType(), processor);
    }

    public void unregister(QName name) {
        cache.remove(name);
    }

    public void process(Contribution contribution, XMLStreamReader reader, ValidationContext context, ClassLoader loader)
            throws ContributionException {
        QName name = reader.getName();
        XmlProcessor processor = cache.get(name);
        if (processor == null) {
            String id = name.toString();
            throw new XmlProcessorTypeNotFoundException("XML processor not found for: " + id, id);
        }
        processor.processContent(contribution, context, reader, loader);
    }
}
