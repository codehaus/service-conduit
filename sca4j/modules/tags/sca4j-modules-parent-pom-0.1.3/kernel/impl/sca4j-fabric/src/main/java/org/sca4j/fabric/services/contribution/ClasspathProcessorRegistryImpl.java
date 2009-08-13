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
package org.sca4j.fabric.services.contribution;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.spi.services.contribution.ClasspathProcessor;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;

/**
 * @version $Rev: 5262 $ $Date: 2008-08-25 07:43:39 +0100 (Mon, 25 Aug 2008) $
 */
@EagerInit
public class ClasspathProcessorRegistryImpl implements ClasspathProcessorRegistry {
    private List<ClasspathProcessor> processors = new ArrayList<ClasspathProcessor>();
    // cache of previously processed artifact URLs
    private Map<URL, List<URL>> cache = new HashMap<URL, List<URL>>();

    public void register(ClasspathProcessor processor) {
        processors.add(processor);
    }

    public void unregister(ClasspathProcessor processor) {
        processors.remove(processor);
    }

    public List<URL> process(URL url) throws IOException {
        List<URL> cached = cache.get(url);
        if (cached != null) {
            // artifact has already been processed, reuse it
            return cached;
        }
        for (ClasspathProcessor processor : processors) {
            if (processor.canProcess(url)) {
                List<URL> urls = processor.process(url);
                cache.put(url, urls);
                return urls;

            }
        }
        // artifact does not need to be expanded, just return its base url
        List<URL> urls = new ArrayList<URL>();
        urls.add(url);
        cache.put(url, urls);
        return urls;
    }
}
