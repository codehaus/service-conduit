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
 * Original Codehaus Header
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
 * Original Apache Header
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.fabric.services.contribution;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Service;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.ManifestProcessor;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceProcessor;

/**
 * Default implementation of ProcessorRegistry
 *
 * @version $Rev: 4358 $ $Date: 2008-05-26 07:31:29 +0100 (Mon, 26 May 2008) $
 */
@EagerInit
@Service(ProcessorRegistry.class)
public class ProcessorRegistryImpl implements ProcessorRegistry {
    
    private Map<String, ResourceProcessor> resourceProcessorCache = new HashMap<String, ResourceProcessor>();
    private Map<String, ManifestProcessor> manifestProcessorCache = new HashMap<String, ManifestProcessor>();

    public ProcessorRegistryImpl() {
    }

    public void register(ResourceProcessor processor) {
        resourceProcessorCache.put(processor.getContentType(), processor);
    }

    public void unregisterResourceProcessor(String contentType) {
        resourceProcessorCache.remove(contentType);
    }

    public void register(ManifestProcessor processor) {
        manifestProcessorCache.put(processor.getContentType(), processor);
    }

    public void unregisterManifestProcessor(String contentType) {
        manifestProcessorCache.remove(contentType);
    }

    public void processManifestArtifact(ContributionManifest manifest, String contentType, InputStream inputStream, ValidationContext context)
            throws ContributionException {
        ManifestProcessor processor = manifestProcessorCache.get(contentType);
        if (processor != null) {
            processor.process(manifest, inputStream, context);
        }
    }

    public void indexResource(Contribution contribution, String contentType, URL url, ValidationContext context) throws ContributionException {
        ResourceProcessor processor = resourceProcessorCache.get(contentType);
        if (processor == null) {
            // unknown type, skip
            return;
        }
        processor.index(contribution, url, context);
    }

    public void processResource(URI contributionUri, Resource resource, ValidationContext context, ClassLoader loader) throws ContributionException {
        ResourceProcessor processor = resourceProcessorCache.get(resource.getContentType());
        if (processor == null) {
            // FIXME for now, return null
            return;
            //throw new UnsupportedContentTypeException(contentType);
        }
        processor.process(contributionUri, resource, context, loader);
    }

}
