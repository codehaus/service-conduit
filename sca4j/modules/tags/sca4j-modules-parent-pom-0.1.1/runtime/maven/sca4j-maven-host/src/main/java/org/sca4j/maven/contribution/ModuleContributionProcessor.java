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
package org.sca4j.maven.contribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.util.FileHelper;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.xml.Loader;
import org.sca4j.introspection.xml.LoaderException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contenttype.ContentTypeResolutionException;
import org.sca4j.spi.services.contenttype.ContentTypeResolver;
import org.sca4j.spi.services.contribution.Action;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionManifest;
import org.sca4j.spi.services.contribution.ContributionProcessor;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.Resource;

/**
 * Processes a Maven module directory.
 *
 * @version $Rev: 4877 $ $Date: 2008-06-22 11:44:45 +0100 (Sun, 22 Jun 2008) $
 */
@EagerInit
public class ModuleContributionProcessor implements ContributionProcessor {
    public static final List<String> CONTENT_TYPES = initializeContentTypes();

    private ProcessorRegistry registry;
    private ContentTypeResolver contentTypeResolver;
    private Loader loader;

    public ModuleContributionProcessor(@Reference ProcessorRegistry registry,
                                       @Reference ContentTypeResolver contentTypeResolver,
                                       @Reference Loader loader) {
        this.registry = registry;
        this.contentTypeResolver = contentTypeResolver;
        this.loader = loader;
    }

    public List<String> getContentTypes() {
        return CONTENT_TYPES;
    }

    @Init
    public void init() {
        registry.register(this);
    }

    public void process(Contribution contribution, ValidationContext context, ClassLoader loader) throws ContributionException {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        URI contributionUri = contribution.getUri();
        try {
            Thread.currentThread().setContextClassLoader(loader);
            for (Resource resource : contribution.getResources()) {
                if (!resource.isProcessed()) {
                    registry.processResource(contributionUri, resource, context, loader);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    public void processManifest(Contribution contribution, final ValidationContext context) throws ContributionException {
        ContributionManifest manifest;
        try {
            URL sourceUrl = contribution.getLocation();
            URL manifestURL = new URL(sourceUrl.toExternalForm() + "/classes/META-INF/sca-contribution.xml");
            ClassLoader cl = getClass().getClassLoader();
            URI uri = contribution.getUri();
            IntrospectionContext childContext = new DefaultIntrospectionContext(cl, uri, null);
            manifest = loader.load(manifestURL, ContributionManifest.class, childContext);
            if (childContext.hasErrors()) {
                context.addErrors(childContext.getErrors());
            }
            if (childContext.hasWarnings()) {
                context.addWarnings(childContext.getWarnings());
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

        iterateArtifacts(contribution, context, new Action() {
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

    public void index(Contribution contribution, final ValidationContext context) throws ContributionException {
        iterateArtifacts(contribution, context, new Action() {
            public void process(Contribution contribution, String contentType, URL url)
                    throws ContributionException {
                registry.indexResource(contribution, contentType, url, context);
            }
        });
    }

    private void iterateArtifacts(Contribution contribution, final ValidationContext context, Action action) throws ContributionException {
        File root = FileHelper.toFile(contribution.getLocation());
        assert root.isDirectory();
        iterateArtifactsResursive(contribution, context, action, root);
    }

    private void iterateArtifactsResursive(Contribution contribution, final ValidationContext context, Action action, File dir) throws ContributionException {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                iterateArtifactsResursive(contribution, context, action, file);
            } else {
                try {
                    URL entryUrl = file.toURI().toURL();
                    String contentType = contentTypeResolver.getContentType(entryUrl);
                    action.process(contribution, contentType, entryUrl);
                } catch (MalformedURLException e) {
                    context.addWarning(new ContributionIndexingFailure(file, e));
                } catch (IOException e) {
                    context.addWarning(new ContributionIndexingFailure(file, e));
                } catch (ContentTypeResolutionException e) {
                    context.addWarning(new ContributionIndexingFailure(file, e));
                }
            }
        }

    }

    private static List<String> initializeContentTypes() {
        List<String> list = new ArrayList<String>(1);
        list.add("application/vnd.sca4j.maven-project");
        return list;
    }

}
