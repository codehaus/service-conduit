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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ContributionNotFoundException;
import org.sca4j.host.contribution.ContributionService;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.Deployable;
import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.introspection.validation.InvalidContributionException;
import org.sca4j.introspection.validation.ValidationUtils;
import org.sca4j.scdl.ArtifactValidationFailure;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contenttype.ContentTypeResolutionException;
import org.sca4j.spi.services.contenttype.ContentTypeResolver;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;

/**
 * Default ContributionService implementation
 *
 * @version $Rev: 4912 $ $Date: 2008-06-26 23:28:37 +0100 (Thu, 26 Jun 2008) $
 */
@Service(ContributionService.class)
@EagerInit
public class ContributionServiceImpl implements ContributionService {
    private ProcessorRegistry processorRegistry;
    private MetaDataStore metaDataStore;
    private ContributionLoader contributionLoader;
    private ContentTypeResolver contentTypeResolver;
    private DependencyService dependencyService;
    private String uriPrefix = "contribution://";
    private ContributionServiceMonitor monitor;


    public ContributionServiceImpl(@Reference ProcessorRegistry processorRegistry,
                                   @Reference MetaDataStore metaDataStore,
                                   @Reference ContributionLoader contributionLoader,
                                   @Reference ContentTypeResolver contentTypeResolver,
                                   @Reference DependencyService dependencyService,
                                   @Monitor ContributionServiceMonitor monitor)
            throws IOException, ClassNotFoundException {
        this.processorRegistry = processorRegistry;
        this.metaDataStore = metaDataStore;
        this.contributionLoader = contributionLoader;
        this.contentTypeResolver = contentTypeResolver;
        this.dependencyService = dependencyService;
        this.monitor = monitor;
    }

    @Property(required = false)
    public void setUriPrefix(String uriPrefix) {
        this.uriPrefix = uriPrefix;
    }

    public List<URI> contribute(List<ContributionSource> sources) throws ContributionException {
        List<Contribution> contributions = new ArrayList<Contribution>(sources.size());
        for (ContributionSource source : sources) {
            // store the contributions
            contributions.add(store(source));
        }
        for (Contribution contribution : contributions) {
            // process any SCA manifest information, including imports and exports
            ValidationContext context = new DefaultValidationContext();
            processorRegistry.processManifest(contribution, context);
            if (context.hasErrors()) {
                ArtifactValidationFailure error = new ArtifactValidationFailure("the contribution manifest (sca-contribution.xml)");
                error.addFailures(context.getErrors());
                List<ValidationFailure<?>> errors = new ArrayList<ValidationFailure<?>>();
                errors.add(error);

                ArtifactValidationFailure warning = new ArtifactValidationFailure("the contribution manifest (sca-contribution.xml)");
                warning.addFailures(context.getWarnings());
                List<ValidationFailure<?>> warnings = new ArrayList<ValidationFailure<?>>();
                warnings.add(warning);

                throw new InvalidContributionException(errors, warnings);
            }

        }
        // order the contributions based on their dependencies
        contributions = dependencyService.order(contributions);
        for (Contribution contribution : contributions) {
            ClassLoader loader = contributionLoader.loadContribution(contribution);
            // continue processing the contributions. As they are ordered, dependencies will resolve correctly
            processContents(contribution, loader);
        }
        List<URI> uris = new ArrayList<URI>(contributions.size());
        for (Contribution contribution : contributions) {
            uris.add(contribution.getUri());
        }
        return uris;
    }

    public URI contribute(ContributionSource source) throws ContributionException {
        Contribution contribution = store(source);
        ValidationContext context = new DefaultValidationContext();
        processorRegistry.processManifest(contribution, context);
        if (context.hasErrors()) {
            ArtifactValidationFailure failure = new ArtifactValidationFailure("the contribution manifest (sca-contribution.xml)");
            failure.addFailures(context.getErrors());
            List<ValidationFailure<?>> failures = new ArrayList<ValidationFailure<?>>();
            failures.add(failure);
            ArtifactValidationFailure warning = new ArtifactValidationFailure("the contribution manifest (sca-contribution.xml)");
            warning.addFailures(context.getWarnings());
            List<ValidationFailure<?>> warnings = new ArrayList<ValidationFailure<?>>();
            warnings.add(warning);
            throw new InvalidContributionException(failures, warnings);
        }
        ClassLoader loader = contributionLoader.loadContribution(contribution);
        processContents(contribution, loader);
        return contribution.getUri();
    }

    public boolean exists(URI uri) {
        return metaDataStore.find(uri) != null;
    }

    public void update(ContributionSource source) throws ContributionException {
        URI uri = source.getUri();
        byte[] checksum = source.getChecksum();
        long timestamp = source.getTimestamp();
        InputStream is = null;
        try {
            is = source.getSource();
            update(uri, checksum, timestamp);
        } catch (IOException e) {
            throw new ContributionException("Contribution error", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                monitor.error("Error closing stream", e);
            }
        }
    }

    public long getContributionTimestamp(URI uri) {
        Contribution contribution = metaDataStore.find(uri);
        if (contribution == null) {
            return -1;
        }
        return contribution.getTimestamp();
    }

    public List<Deployable> getDeployables(URI contributionUri) throws ContributionException {
        Contribution contribution = find(contributionUri);
        List<Deployable> list = new ArrayList<Deployable>();
        if (contribution.getManifest() != null) {
            for (Deployable deployable : contribution.getManifest().getDeployables()) {
                list.add(deployable);
            }
        }
        return list;
    }

    public void remove(URI contributionUri) throws ContributionException {
        //Work in progress
        metaDataStore.remove(contributionUri);
    }

    public <T> T resolve(URI contributionUri, Class<T> definitionType, QName name) {
        throw new UnsupportedOperationException();
    }

    public URL resolve(URI contribution, String namespace, URI uri, URI baseURI) {
        throw new UnsupportedOperationException();
    }

    private Contribution find(URI contributionUri) throws ContributionNotFoundException {
        Contribution contribution = metaDataStore.find(contributionUri);
        if (contribution == null) {
            String uri = contributionUri.toString();
            throw new ContributionNotFoundException("No contribution found for: " + uri, uri);
        }
        return contribution;
    }

    private void update(URI uri, byte[] checksum, long timestamp) throws ContributionException, IOException {
        Contribution contribution = metaDataStore.find(uri);
        if (contribution == null) {
            String identifier = uri.toString();
            throw new ContributionNotFoundException("Contribution not found for: " + identifier, identifier);
        }
        long archivedTimestamp = contribution.getTimestamp();
        if (timestamp > archivedTimestamp) {
            // TODO update
        } else if (timestamp == archivedTimestamp && Arrays.equals(checksum, contribution.getChecksum())) {
            // TODO update
        }
    }

    /**
     * Stores the contents of a contribution in the archive store if it is not local
     *
     * @param source the contribution source
     * @return the contribution
     * @throws ContributionException if an error occurs during the store operation
     */
    private Contribution store(ContributionSource source) throws ContributionException {
        URI contributionUri = source.getUri();
        if (contributionUri == null) {
            contributionUri = URI.create(uriPrefix + "/" + UUID.randomUUID());
        }
        try {
            URL locationUrl = source.getLocation();
            String type = source.getContentType();
            if (type == null) {
                type = contentTypeResolver.getContentType(source.getLocation());
            }
            byte[] checksum = source.getChecksum();
            long timestamp = source.getTimestamp();
            return new Contribution(contributionUri, locationUrl, checksum, timestamp, type);
        } catch (ContentTypeResolutionException e) {
            throw new ContributionException(e);
        }
    }

    /**
     * Processes contribution contents. This assumes all dependencies are installed and can be resolved.
     *
     * @param contribution the contribution to process
     * @param loader       the classloader to load resources in
     * @throws ContributionException if an error occurs during processing
     */
    private void processContents(Contribution contribution, ClassLoader loader) throws ContributionException {
        try {
            ValidationContext context = new DefaultValidationContext();
            processorRegistry.indexContribution(contribution, context);
            if (context.hasErrors()) {
                throw new InvalidContributionException(context.getErrors(), context.getWarnings());
            }
            metaDataStore.store(contribution);
            context = new DefaultValidationContext();
            processorRegistry.processContribution(contribution, context, loader);
            validateContrbitution(contribution, context);
            if (context.hasErrors()) {
                throw new InvalidContributionException(context.getErrors(), context.getWarnings());
            } else if (context.hasWarnings()) {
                // there were just warnings, report them
                monitor.contributionWarnings(ValidationUtils.outputWarnings(context.getWarnings()));
            }
            addContributionUri(contribution);
        } catch (MetaDataStoreException e) {
            throw new ContributionException(e);
        }
    }

    /**
     * Performs final validation on a contribution.
     *
     * @param contribution the contribution to validate
     * @param context      the validation context
     */
    private void validateContrbitution(Contribution contribution, ValidationContext context) {
        for (Deployable deployable : contribution.getManifest().getDeployables()) {
            QName name = deployable.getName();
            QNameSymbol symbol = new QNameSymbol(name);
            boolean found = false;
            for (Resource resource : contribution.getResources()) {
                for (ResourceElement<?, ?> element : resource.getResourceElements()) {
                    if (element.getSymbol().equals(symbol)) {
                        found = true;
                    }
                }
            }
            if (!found) {
                URI uri = contribution.getUri();
                InvalidDeployable failure = new InvalidDeployable("Deployable composite " + name + " not found in " + uri, uri, name);
                context.addError(failure);
            }

        }
    }

    /**
     * Recursively adds the contribution URI to all components.
     *
     * @param contribution the contribution the component is defined in
     * @throws ContributionNotFoundException if a required imported contribution is not found
     */
    private void addContributionUri(Contribution contribution) throws ContributionException {
        for (Resource resource : contribution.getResources()) {
            for (ResourceElement<?, ?> element : resource.getResourceElements()) {
                Object value = element.getValue();
                if (value instanceof Composite) {
                    addContributionUri(contribution, (Composite) value);
                }
            }
        }
    }

    /**
     * Adds the contibution URI to a component and its children if it is a composite.
     *
     * @param contribution the contribution
     * @param composite    the composite
     */
    private void addContributionUri(Contribution contribution, Composite composite) {
        for (ComponentDefinition<?> definition : composite.getComponents().values()) {
            Implementation<?> implementation = definition.getImplementation();
            if (CompositeImplementation.class.isInstance(implementation)) {
                CompositeImplementation compositeImplementation = CompositeImplementation.class.cast(implementation);
                Composite componentType = compositeImplementation.getComponentType();
                addContributionUri(contribution, componentType);
            } else {
                definition.setContributionUri(contribution.getUri());
            }
        }
    }


}
