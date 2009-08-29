/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
