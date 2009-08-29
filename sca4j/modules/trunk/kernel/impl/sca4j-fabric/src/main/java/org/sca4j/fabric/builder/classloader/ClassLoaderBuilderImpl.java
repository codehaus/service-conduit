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
package org.sca4j.fabric.builder.classloader;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.runtime.ComponentNames;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.classloader.MultiParentClassLoader;
import org.sca4j.spi.model.physical.PhysicalClassLoaderDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.contribution.ClasspathProcessorRegistry;
import org.sca4j.spi.services.contribution.ContributionUriResolver;
import org.sca4j.spi.services.contribution.ResolutionException;

/**
 * Default implementation of ClassLoaderBuilder.
 *
 * @version $Rev: 5321 $ $Date: 2008-09-02 09:54:02 +0100 (Tue, 02 Sep 2008) $
 */
@EagerInit
public class ClassLoaderBuilderImpl implements ClassLoaderBuilder {

    private ClassLoaderRegistry classLoaderRegistry;
    private ContributionUriResolver contributionUriResolver;
    private ClasspathProcessorRegistry classpathProcessorRegistry;
    private boolean classLoaderIsolation;

    public ClassLoaderBuilderImpl(@Reference ClassLoaderRegistry classLoaderRegistry,
                                  @Reference ContributionUriResolver contributionUriResolver,
                                  @Reference ClasspathProcessorRegistry classpathProcessorRegistry,
                                  @Reference HostInfo info) {
        this.classLoaderRegistry = classLoaderRegistry;
        this.contributionUriResolver = contributionUriResolver;
        this.classpathProcessorRegistry = classpathProcessorRegistry;
        classLoaderIsolation = info.supportsClassLoaderIsolation();
    }

    public void build(PhysicalClassLoaderDefinition definition) throws ClassLoaderBuilderException {

        if (classLoaderRegistry.getClassLoader(definition.getUri()) != null) {
            updateClassLoader(definition);
        } else {
            createClassLoader(definition);
        }

    }

    public void destroy(URI uri) {
        classLoaderRegistry.unregister(uri);
    }

    /**
     * Creates a new classloader from a PhysicalClassLoaderDefinition.
     *
     * @param definition the PhysicalClassLoaderDefinition to create the classloader from
     * @throws ClassLoaderBuilderException if an error occurs creating the classloader
     */
    private void createClassLoader(PhysicalClassLoaderDefinition definition) throws ClassLoaderBuilderException {

        URI name = definition.getUri();
        URL[] classpath = resolveClasspath(definition.getContributionUris());

        // build the classloader using the locally cached resources
        MultiParentClassLoader loader = new MultiParentClassLoader(name, classpath, null);
        // add the host classloader
        ClassLoader cl = classLoaderRegistry.getClassLoader(ComponentNames.APPLICATION_CLASSLOADER_ID);
        loader.addParent(cl);
        if (classLoaderIsolation) {
            // if the host supports isolated classloaders, add any parents
            for (URI uri : definition.getParentClassLoaders()) {
                ClassLoader parent = classLoaderRegistry.getClassLoader(uri);
                if (parent == null) {
                    String identifier = uri.toString();
                    throw new ClassLoaderNotFoundException("Parent classloader not found: " + identifier);
                }
                loader.addParent(parent);
            }
        }
        classLoaderRegistry.register(name, loader);
    }

    /**
     * Updates the given classloader with additional artifacts from the PhysicalClassLoaderDefinition. Classloader updates are typically performed
     * during an include operation where the included component requires additional libraries or classes not currently on the composite classpath.
     *
     * @param definition the definition to update the classloader with
     * @throws ClassLoaderBuilderException if an error occurs updating the classloader
     */
    private void updateClassLoader(PhysicalClassLoaderDefinition definition) throws ClassLoaderBuilderException {

        ClassLoader cl = classLoaderRegistry.getClassLoader(definition.getUri());
        assert cl instanceof MultiParentClassLoader;
        MultiParentClassLoader loader = (MultiParentClassLoader) cl;
        Set<URI> uris = definition.getContributionUris();

        for (URI uri : uris) {
            try {
                // resolve the remote artifact URL and add it to the classloader
                URL resolvedUrl = contributionUriResolver.resolve(uri);
                // introspect and expand if necessary
                List<URL> processedUrls = classpathProcessorRegistry.process(resolvedUrl);
                URL[] loaderUrls = loader.getURLs();
                // check if URLs are already on the classpath, and if so do not add them
                for (URL processedUrl : processedUrls) {
                    boolean found = false;
                    for (URL loaderUrl : loaderUrls) {
                        if (loaderUrl.equals(processedUrl)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // the URL is not on the classpath, update the classloader
                        loader.addURL(processedUrl);
                    }

                }
            } catch (ResolutionException e) {
                throw new ClassLoaderBuilderException("Error resolving artifact: " + uri.toString(), e);
            } catch (IOException e) {
                throw new ClassLoaderBuilderException("Error processing: " + uri.toString(), e);
            }
        }

        if (!definition.getExtensionUris().isEmpty()) {
            // since all extensions are merged into the system classloader, just add it
            ClassLoader systemCL = classLoaderRegistry.getClassLoader(URI.create("sca4j://runtime"));
            if (!loader.getParents().contains(systemCL)) {
                loader.addParent(systemCL);
            }
        }
        for (URI uri : definition.getParentClassLoaders()) {
            ClassLoader parent = classLoaderRegistry.getClassLoader(uri);
            if (!loader.getParents().contains(parent)) {
                loader.addParent(parent);
            }
        }

    }

    /**
     * Resolves classpath urls
     *
     * @param uris urls to resolve
     * @return the resolved classpath urls
     * @throws ClassLoaderBuilderException if an error occurs resolving a url
     */
    private URL[] resolveClasspath(Set<URI> uris) throws ClassLoaderBuilderException {

        List<URL> classpath = new ArrayList<URL>();

        for (URI uri : uris) {
            try {
                // resolve the remote contributions and cache them locally
                URL resolvedUrl = contributionUriResolver.resolve(uri);
                // introspect and expand if necessary
                classpath.addAll(classpathProcessorRegistry.process(resolvedUrl));
            } catch (ResolutionException e) {
                throw new ClassLoaderBuilderException("Error resolving artifact: " + uri.toString(), e);
            } catch (IOException e) {
                throw new ClassLoaderBuilderException("Error processing: " + uri.toString(), e);
            }
        }
        return classpath.toArray(new URL[classpath.size()]);

    }

}
