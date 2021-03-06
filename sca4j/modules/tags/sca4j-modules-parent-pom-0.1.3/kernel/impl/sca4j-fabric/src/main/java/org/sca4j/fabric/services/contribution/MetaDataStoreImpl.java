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

import java.io.Serializable;
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
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.contribution.Contribution;
import org.sca4j.spi.services.contribution.Export;
import org.sca4j.spi.services.contribution.Import;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.ProcessorRegistry;
import org.sca4j.spi.services.contribution.Resource;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.spi.services.contribution.Symbol;

/**
 * Default IndexStore implementation
 *
 * @version $Rev: 5299 $ $Date: 2008-08-29 23:02:05 +0100 (Fri, 29 Aug 2008) $
 */
public class MetaDataStoreImpl implements MetaDataStore {
    public static final QName COMPOSITE = new QName(Constants.SCA_NS, "composite");
    private Map<URI, Contribution> cache = new ConcurrentHashMap<URI, Contribution>();
    private Map<QName, Map<Export, Contribution>> exportsToContributionCache =
            new ConcurrentHashMap<QName, Map<Export, Contribution>>();
    private ProcessorRegistry processorRegistry;
    private ClassLoaderRegistry classLoaderRegistry;

    public MetaDataStoreImpl(ClassLoaderRegistry classLoaderRegistry, ProcessorRegistry processorRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
        this.processorRegistry = processorRegistry;
    }

    public void store(Contribution contribution) throws MetaDataStoreException {
        cache.put(contribution.getUri(), contribution);
        addToExports(contribution);
    }

    /**
     * Used to reinject the processor registry after runtime bootstrap
     *
     * @param processorRegistry the configured processor registry
     */
    @Reference
    public void setProcessorRegistry(ProcessorRegistry processorRegistry) {
        this.processorRegistry = processorRegistry;
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
    public <S extends Symbol> ResourceElement<S, ?> resolve(S symbol) throws MetaDataStoreException {
        for (Contribution contribution : cache.values()) {
            for (Resource resource : contribution.getResources()) {
                for (ResourceElement<?, ?> element : resource.getResourceElements()) {
                    if (element.getSymbol().equals(symbol)) {
                        if (!resource.isProcessed()) {
                            // this is a programming error as resolve(Symbol) should only be called after contribution resources have been processed
                            throw new MetaDataStoreException("Attempt to resolve a resource before it is processed");
                        }
                        return (ResourceElement<S, ?>) element;
                    }
                }
            }
        }
        return null;
    }

    public Resource resolveContainingResource(URI contributionUri, Symbol symbol) {
        Contribution contribution = cache.get(contributionUri);
        if (contribution != null) {
            for (Resource resource : contribution.getResources()) {
                for (ResourceElement<?, ?> element : resource.getResourceElements()) {
                    if (element.getSymbol().equals(symbol)) {
                        return resource;
                    }
                }
            }
        }
        return null;
    }

    public <S extends Symbol, V extends Serializable> ResourceElement<S, V> resolve(URI contributionUri,
                                                                                    Class<V> type,
                                                                                    S symbol,
                                                                                    ValidationContext context)
            throws MetaDataStoreException {
        Contribution contribution = find(contributionUri);
        if (contribution == null) {
            String identifier = contributionUri.toString();
            throw new ContributionResolutionException("Contribution not found: " + identifier, identifier);
        }
        ResourceElement<S, V> element = resolveInternal(contribution, type, symbol, context);
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
    private <S extends Symbol, V extends Serializable> ResourceElement<S, V> resolveInternal(Contribution contribution,
                                                                                             Class<V> type,
                                                                                             S symbol,
                                                                                             ValidationContext context)
            throws MetaDataStoreException {
        URI contributionUri = contribution.getUri();
        ClassLoader loader = classLoaderRegistry.getClassLoader(contributionUri);
        assert loader != null;
        for (Resource resource : contribution.getResources()) {
            for (ResourceElement<?, ?> element : resource.getResourceElements()) {
                if (element.getSymbol().equals(symbol)) {
                    if (!resource.isProcessed()) {
                        try {
                            processorRegistry.processResource(contributionUri, resource, context, loader);
                        } catch (ContributionException e) {
                            String identifier = resource.getUrl().toString();
                            throw new MetaDataStoreException("Error resolving resource: " + identifier, identifier, e);
                        }
                    }
                    if (!type.isInstance(element.getValue())) {
                        throw new IllegalArgumentException("Invalid type for symbol: " + type);
                    }
                    return (ResourceElement<S, V>) element;
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
