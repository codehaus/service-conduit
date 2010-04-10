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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.osoa.sca.Constants;
import org.osoa.sca.annotations.Reference;
import org.sca4j.host.contribution.ContributionException;
import org.sca4j.scdl.ValidationContext;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.Export;
import org.sca4j.spi.services.contribution.Import;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.spi.services.contribution.ResourceProcessorRegistry;

/**
 * Default IndexStore implementation
 *
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
public class MetaDataStoreImpl implements MetaDataStore {
    
    public static final QName COMPOSITE = new QName(Constants.SCA_NS, "composite");
    
    private Map<URI, Contribution> cache = new ConcurrentHashMap<URI, Contribution>();
    private Map<QName, Map<Export, Contribution>> exportsToContributionCache = new ConcurrentHashMap<QName, Map<Export, Contribution>>();
    
    @Reference public ResourceProcessorRegistry resourceProcessorRegistry;
    

    public void store(Contribution contribution) throws MetaDataStoreException {
        cache.put(contribution.getUri(), contribution);
        addToExports(contribution);
    }

    public Contribution find(URI contributionUri) {
        return cache.get(contributionUri);
    }

    public void remove(URI contributionUri) {
        Contribution contribution = find(contributionUri);
        if (contribution != null) {
            if (contribution.getManifest() == null) {
                return;
            }
            List<Export> exports = contribution.getManifest().getExports();
            if (exports.size() > 0) {
                for (Export export : exports) {
                    exportsToContributionCache.remove(export.getType());
                }
            }
        }
        cache.remove(contributionUri);
    }

    @SuppressWarnings({"unchecked"})
    public <S, RE extends ResourceElement<S, ?>> RE resolve(S symbol, Class<RE> resourceElementType) throws MetaDataStoreException {
        for (Contribution contribution : cache.values()) {
            for (Resource resource : contribution.getResources()) {
                for (ResourceElement<?, ?> element : resource.getResourceElements(resourceElementType)) {
                    if (element.getSymbol().equals(symbol)) {
                        if (!resource.isProcessed()) {
                            // this is a programming error as resolve(Symbol) should only be called after contribution resources have been processed
                            throw new MetaDataStoreException("Attempt to resolve a resource before it is processed");
                        }
                        return resourceElementType.cast(element);
                    }
                }
            }
        }
        return null;
    }

    public <S, RE extends ResourceElement<S, ?>> Resource resolveContainingResource(URI contributionUri, S symbol, Class<RE> resourceElementType) {
        Contribution contribution = cache.get(contributionUri);
        if (contribution != null) {
            for (Resource resource : contribution.getResources()) {
                for (ResourceElement<?, ?> element : resource.getResourceElements(resourceElementType)) {
                    if (element.getSymbol().equals(symbol)) {
                        return resource;
                    }
                }
            }
        }
        return null;
    }

    public <S, RE extends ResourceElement<S, ?>> RE resolve(URI contributionUri, Class<RE> type, S symbol, ValidationContext context) throws MetaDataStoreException {
        Contribution contribution = find(contributionUri);
        if (contribution == null) {
            String identifier = contributionUri.toString();
            throw new ContributionResolutionException("Contribution not found: " + identifier, identifier);
        }
        RE element = resolveInternal(contribution, type, symbol, context);
        if (element != null) {
            return element;
        }
        for (URI uri : contribution.getResolvedImportUris()) {
            Contribution resolved = cache.get(uri);
            if (resolved == null) {
                String identifier = contributionUri.toString();
                throw new ContributionResolutionException("Dependent contibution not found: " + identifier, identifier);
            }
            element = resolveInternal(resolved, type, symbol, context);
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    /**
     * Resolves an import to a Contribution that exports it
     *
     * @param imprt the import to resolve
     * @return the contribution or null
     */
    public Contribution resolve(Import imprt) {
        Map<Export, Contribution> map = exportsToContributionCache.get(imprt.getType());
        if (map == null) {
            return null;
        }
        for (Map.Entry<Export, Contribution> entry : map.entrySet()) {
            int level = entry.getKey().match(imprt);
            if (level == Export.EXACT_MATCH) {
                return entry.getValue();
            }
        }
        return null;
    }

    public List<Contribution> resolveTransitiveImports(Contribution contribution) throws UnresolvableImportException {
        ArrayList<Contribution> contributions = new ArrayList<Contribution>();
        resolveTransitiveImports(contribution, contributions);
        return contributions;
    }

    private void resolveTransitiveImports(Contribution contribution, List<Contribution> dependencies)
            throws UnresolvableImportException {
        for (Import imprt : contribution.getManifest().getImports()) {
            Contribution imported = resolve(imprt);
            if (imported == null) {
                String id = contribution.getUri().toString();
                throw new UnresolvableImportException("Import " + imprt + " in contribution " + id + " cannot be resolved", id, imprt);
            }
            if (!dependencies.contains(imported)) {
                dependencies.add(imported);
            }
            resolveTransitiveImports(imported, dependencies);
        }
    }

    @SuppressWarnings({"unchecked"})
    private <S, RE extends ResourceElement<?, ?>> RE resolveInternal(Contribution contribution, Class<RE> type, S symbol, ValidationContext context) throws MetaDataStoreException {
        URI contributionUri = contribution.getUri();
        ClassLoader loader = getClass().getClassLoader();
        for (Resource resource : contribution.getResources()) {
            for (ResourceElement<?, ?> element : resource.getResourceElements(type)) {
                if (element.getSymbol().equals(symbol)) {
                    if (!resource.isProcessed()) {
                        try {
                            resourceProcessorRegistry.processResource(contributionUri, resource, context, loader);
                        } catch (ContributionException e) {
                            String identifier = resource.getUrl().toString();
                            throw new MetaDataStoreException("Error resolving resource: " + identifier, identifier, e);
                        }
                    }
                    if (!type.isInstance(element)) {
                        throw new IllegalArgumentException("Invalid type for symbol: " + type);
                    }
                    return type.cast(element);
                }
            }
        }
        return null;
    }

    /**
     * Adds the contribution exports to the cached list of exports for the domain
     *
     * @param contribution the contribution containing the exports to add
     */
    private void addToExports(Contribution contribution) {

        if (contribution.getManifest() == null) {
            return;
        }

        List<Export> exports = contribution.getManifest().getExports();
        if (exports.size() > 0) {
            for (Export export : exports) {
                Map<Export, Contribution> map = exportsToContributionCache.get(export.getType());
                if (map == null) {
                    map = new ConcurrentHashMap<Export, Contribution>();
                    exportsToContributionCache.put(export.getType(), map);
                }
                map.put(export, contribution);
            }
        }
    }

}
