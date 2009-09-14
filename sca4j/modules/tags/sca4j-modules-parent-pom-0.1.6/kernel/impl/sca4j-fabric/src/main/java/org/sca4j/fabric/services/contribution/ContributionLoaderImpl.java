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
package org.sca4j.fabric.services.contribution;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.osoa.sca.annotations.Reference;

import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.classloader.MultiParentClassLoader;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.Import;
import org.sca4j.spi.services.contribution.MatchingExportNotFoundException;
import org.sca4j.spi.services.contribution.MetaDataStore;

/**
 * Default implementation of the ContributionLoader. Classloaders corresponding to loaded contributions are registered by name with the system
 * ClassLoaderRegistry.
 *
 * @version $Rev: 5273 $ $Date: 2008-08-26 05:12:07 +0100 (Tue, 26 Aug 2008) $
 */
public class ContributionLoaderImpl implements ContributionLoader {
    private static final URI APP_CLASSLOADER = URI.create("sca4j://runtime/ApplicationClassLoader");
    private final ClassLoaderRegistry classLoaderRegistry;
    private final MetaDataStore store;
    private final ClasspathProcessorRegistry classpathProcessorRegistry;
    private boolean classloaderIsolation;

    public ContributionLoaderImpl(@Reference ClassLoaderRegistry classLoaderRegistry,
                                  @Reference MetaDataStore store,
                                  @Reference ClasspathProcessorRegistry classpathProcessorRegistry,
                                  @Reference HostInfo info) {
        this.classLoaderRegistry = classLoaderRegistry;
        this.store = store;
        this.classpathProcessorRegistry = classpathProcessorRegistry;
        classloaderIsolation = info.supportsClassLoaderIsolation();
    }

    public ClassLoader loadContribution(Contribution contribution) throws ContributionLoadException, MatchingExportNotFoundException {
        URI contributionUri = contribution.getUri();
        ClassLoader cl = classLoaderRegistry.getClassLoader(APP_CLASSLOADER);
        if (!classloaderIsolation) {
            // the host environment does not support classloader isolation so only verify extensions are present
            verifyImports(contribution);
            return cl;
        }
        MultiParentClassLoader loader = new MultiParentClassLoader(contributionUri, cl);
        List<URL> classpath;
        try {
            // construct the classpath for contained resources in the contribution
            classpath = classpathProcessorRegistry.process(contribution.getLocation());
        } catch (IOException e) {
            throw new ContributionLoadException(e);
        }

        for (URL library : classpath) {
            loader.addURL(library);
        }
        resolveImports(contribution, loader);
        // register the classloader
        classLoaderRegistry.register(contributionUri, loader);
        return loader;
    }

    private void resolveImports(Contribution contribution, MultiParentClassLoader loader)
            throws MatchingExportNotFoundException, ContributionLoadException {
        ContributionManifest manifest = contribution.getManifest();
        for (Import imprt : manifest.getImports()) {
            Contribution imported = store.resolve(imprt);
            if (imported == null) {
                String id = imprt.toString();
                throw new MatchingExportNotFoundException("No matching export found for: " + id, id);
            }
            // add the resolved URI to the contribution
            URI importedUri = imported.getUri();
            contribution.addResolvedImportUri(importedUri);
            // add the imported classloader
            ClassLoader importedLoader = classLoaderRegistry.getClassLoader(importedUri);
            if (importedLoader == null) {
                // TODO load in a transient classloader
                String uri = importedUri.toString();
                throw new ContributionLoadException("Imported classloader could not be found: " + uri, uri);
            }
            loader.addParent(importedLoader);
        }
    }

    private void verifyImports(Contribution contribution)
            throws MatchingExportNotFoundException, ContributionLoadException {
        ContributionManifest manifest = contribution.getManifest();
        for (Import imprt : manifest.getImports()) {
            Contribution imported = store.resolve(imprt);
            if (imported == null) {
                String id = imprt.toString();
                throw new MatchingExportNotFoundException("No matching export found for: " + id, id);
            }
            // add the resolved URI to the contribution
            URI importedUri = imported.getUri();
            contribution.addResolvedImportUri(importedUri);
        }
    }

}
