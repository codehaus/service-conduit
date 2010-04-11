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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.host.contribution.ContributionService;
import org.sca4j.host.contribution.ContributionSource;
import org.sca4j.host.contribution.Deployable;
import org.sca4j.host.contribution.ValidationFailure;
import org.sca4j.introspection.validation.InvalidContributionException;
import org.sca4j.introspection.validation.ValidationUtils;
import org.sca4j.scdl.ArtifactValidationFailure;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contenttype.ContentTypeResolver;
import org.sca4j.spi.services.contribution.CompositeResourceElement;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.ContributionProcessor;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.Resource;

/**
 * Default ContributionService implementation
 *
 * @version $Rev: 4912 $ $Date: 2008-06-26 23:28:37 +0100 (Thu, 26 Jun 2008) $
 */
@EagerInit
public class ContributionServiceImpl implements ContributionService {
    
    @Reference public Map<String, ContributionProcessor> contributionProcessors;
    @Reference public MetaDataStore metaDataStore;
    @Reference public ContributionLoader contributionLoader;
    @Reference public ContentTypeResolver contentTypeResolver;
    @Reference public DependencyService dependencyService;
    @Monitor public ContributionServiceMonitor monitor;

    /**
     * {@inheritDoc}
     * @see org.sca4j.host.contribution.ContributionService#contribute(org.sca4j.host.contribution.ContributionSource[])
     */
    public List<URI> contribute(ContributionSource ... sources) throws ContributionException {
        
        List<Contribution> contributions = store(sources);
        
        processManifests(contributions);
        
        contributions = dependencyService.order(contributions);
        
        for (Contribution contribution : contributions) {
            ClassLoader loader = contributionLoader.loadContribution(contribution);
            processContents(contribution, loader);
        }
        
        List<URI> uris = new ArrayList<URI>(contributions.size());
        for (Contribution contribution : contributions) {
            uris.add(contribution.getUri());
        }
        
        return uris;
        
    }

    /*
     * Process the manifest.
     */
    private void processManifests(List<Contribution> contributions) throws ContributionException, InvalidContributionException {
        
        for (Contribution contribution : contributions) {
            ValidationContext context = new DefaultValidationContext();
            getContributionProcessor(contribution.getType()).processManifest(contribution, context);
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
        
    }

    /*
     * Store the contribution.
     */
    private List<Contribution> store(ContributionSource ... sources) throws ContributionException {
        List<Contribution> contributions = new ArrayList<Contribution>(sources.length);
        for (ContributionSource source : sources) {
            contributions.add(new Contribution(source.getLocation(), source.getTimestamp(), source.getType()));
        }
        return contributions;
    }

    /*
     * Process the contributions.
     */
    private void processContents(Contribution contribution, ClassLoader loader) throws ContributionException {
        
        try {
            
            ValidationContext context = new DefaultValidationContext();
            getContributionProcessor(contribution.getType()).index(contribution, context);
            if (context.hasErrors()) {
                throw new InvalidContributionException(context.getErrors(), context.getWarnings());
            }
            metaDataStore.store(contribution);
            
            context = new DefaultValidationContext();
            getContributionProcessor(contribution.getType()).process(contribution, context, loader);
            validateContrbitution(contribution, context);
            if (context.hasErrors()) {
                throw new InvalidContributionException(context.getErrors(), context.getWarnings());
            } else if (context.hasWarnings()) {
                monitor.contributionWarnings(ValidationUtils.outputWarnings(context.getWarnings()));
            }
            
        } catch (MetaDataStoreException e) {
            throw new ContributionException(e);
        }
        
    }

    /*
     * Validates the contribution.
     */
    private void validateContrbitution(Contribution contribution, ValidationContext context) {
        for (Deployable deployable : contribution.getManifest().getDeployables()) {
            QName name = deployable.getName();
            boolean found = false;
            for (Resource resource : contribution.getResources()) {
                for (CompositeResourceElement element : resource.getResourceElements(CompositeResourceElement.class)) {
                    if (element.getSymbol().equals(name)) {
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
    
    private ContributionProcessor getContributionProcessor(String type) throws ContributionException {
        if (contributionProcessors.containsKey(type)) {
            return contributionProcessors.get(type);
        }
        throw new ContributionException("Invalid contribution type " + type);
    }

}
