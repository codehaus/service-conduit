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
package org.sca4j.fabric.services.contribution.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osoa.sca.annotations.Reference;

import org.sca4j.host.contribution.Constants;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contenttype.ContentTypeResolutionException;
import org.sca4j.spi.services.contenttype.ContentTypeResolver;
import org.sca4j.spi.services.contribution.Action;
import org.sca4j.spi.services.contribution.ArchiveContributionHandler;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.ProcessorRegistry;

/**
 * Introspects a Zip-based contribution, delegating to ResourceProcessors for handling leaf-level children.
 */
public class ZipContributionHandler implements ArchiveContributionHandler {

    private final Loader loader;
    private final ContentTypeResolver contentTypeResolver;
    private ProcessorRegistry registry;

    public ZipContributionHandler(@Reference ProcessorRegistry processorRegistry,
                                  @Reference Loader loader,
                                  @Reference ContentTypeResolver contentTypeResolver) {

        this.registry = processorRegistry;
        this.loader = loader;
        this.contentTypeResolver = contentTypeResolver;
    }

    public String getContentType() {
        return Constants.ZIP_CONTENT_TYPE;
    }

    public boolean canProcess(Contribution contribution) {
        String sourceUrl = contribution.getLocation().toString();
        return sourceUrl.endsWith(".jar") || sourceUrl.endsWith(".zip");
    }

    public void processManifest(Contribution contribution, final ValidationContext context) throws ContributionException {
        ContributionManifest manifest;
        try {
            URL sourceUrl = contribution.getLocation();
            URL manifestURL = new URL("jar:" + sourceUrl.toExternalForm() + "!/META-INF/sca-contribution.xml");
            ClassLoader cl = getClass().getClassLoader();
            URI uri = contribution.getUri();
            IntrospectionContext childContext = new DefaultIntrospectionContext(cl, uri, null);
            manifest = loader.load(manifestURL, ContributionManifest.class, childContext);
            
            boolean existOnErrorWarnings = onErrorAndWarnings(context, childContext);
            if (existOnErrorWarnings) {
            	/* Return out on error and warning existing */
             	return;
            }
            
        } catch (LoaderException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                manifest = new ContributionManifest();
            } else {
                throw new ContributionException(e);
            }
        } catch (MalformedURLException e) {
            manifest = new ContributionManifest();
        }
        contribution.setManifest(manifest);

        iterateArtifacts(contribution, new Action() {
            public void process(Contribution contribution, String contentType, URL url)
                    throws ContributionException {
                InputStream stream = null;
                try {
                    stream = url.openStream();
                    registry.processManifestArtifact(contribution.getManifest(), contentType, stream, context);
                } catch (IOException e) {
                    throw new ContributionException(e);
                } finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

	public void iterateArtifacts(Contribution contribution, Action action) throws ContributionException {
        URL location = contribution.getLocation();
        ZipInputStream zipStream = null;
        try {
            zipStream = new ZipInputStream(location.openStream());
            while (true) {
                ZipEntry entry = zipStream.getNextEntry();
                if (entry == null) {
                    // EOF
                    break;
                }
                if (entry.isDirectory()) {
                    continue;
                }

                URL entryUrl = new URL("jar:" + location.toExternalForm() + "!/" + entry.getName());
                // hack to return the correct content type
                String contentType = contentTypeResolver.getContentType(new URL(location, entry.getName()));

                // String contentType = contentTypeResolver.getContentType(entryUrl);
                // skip entry if we don't recognize the content type
                if (contentType == null) {
                    continue;
                }
                action.process(contribution, contentType, entryUrl);
            }
        } catch (ContentTypeResolutionException e) {
            throw new ContributionException(e);
        } catch (MalformedURLException e) {
            throw new ContributionException(e);
        } catch (IOException e) {
            throw new ContributionException(e);
        } finally {
            try {
                if (zipStream != null) {
                    zipStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
	
	/*
	 * Adds to the validation context the error and warnings, and returns boolean outcome accordingly
	 */
	private boolean onErrorAndWarnings(ValidationContext validationContext , IntrospectionContext childContext) {
		boolean exist = false;
		
		if (childContext.hasErrors()) {
            validationContext.addErrors(childContext.getErrors());
            exist = true;
        }
        if (childContext.hasWarnings()) {
            validationContext.addWarnings(childContext.getWarnings());
            exist = true;
        }
        return exist;
	}
}
