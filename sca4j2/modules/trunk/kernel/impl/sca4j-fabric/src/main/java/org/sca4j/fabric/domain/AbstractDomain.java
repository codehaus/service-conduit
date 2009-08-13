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
package org.sca4j.fabric.domain;

import static org.osoa.sca.Constants.SCA_NS;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.sca4j.fabric.allocator.AllocationException;
import org.sca4j.fabric.allocator.Allocator;
import org.sca4j.fabric.generator.PhysicalModelGenerator;
import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.fabric.instantiator.LogicalModelInstantiator;
import org.sca4j.fabric.services.routing.RoutingException;
import org.sca4j.fabric.services.routing.RoutingService;
import org.sca4j.host.domain.AssemblyException;
import org.sca4j.host.domain.DeploymentException;
import org.sca4j.host.domain.DomainException;
import org.sca4j.scdl.Composite;
import org.sca4j.spi.domain.Domain;
import org.sca4j.spi.generator.CommandMap;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
import org.sca4j.spi.services.contribution.QNameSymbol;
import org.sca4j.spi.services.contribution.ResourceElement;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.services.lcm.StoreException;

/**
 * Base class for a domain.
 *
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
public abstract class AbstractDomain implements Domain {
    public static final QName COMPOSITE = new QName(SCA_NS, "composite");

    private final MetaDataStore metadataStore;
    private final LogicalComponentManager logicalComponentManager;
    protected LogicalModelInstantiator logicalModelInstantiator;
    protected Allocator allocator;
    protected RoutingService routingService;
    protected PhysicalModelGenerator physicalModelGenerator;

    public AbstractDomain(MetaDataStore metadataStore,
                          LogicalComponentManager logicalComponentManager,
                          Allocator allocator,
                          PhysicalModelGenerator physicalModelGenerator,
                          LogicalModelInstantiator logicalModelInstantiator,
                          RoutingService routingService) {
        this.allocator = allocator;
        this.metadataStore = metadataStore;
        this.physicalModelGenerator = physicalModelGenerator;
        this.logicalModelInstantiator = logicalModelInstantiator;
        this.logicalComponentManager = logicalComponentManager;
        this.routingService = routingService;
    }

    public void initialize() throws DomainException {
    }

    public void include(QName deployable) throws DeploymentException {

        ResourceElement<QNameSymbol, ?> element;
        try {
            element = metadataStore.resolve(new QNameSymbol(deployable));
        } catch (MetaDataStoreException e) {
            throw new DeploymentException("Error deploying: " + deployable, e);
        }
        if (element == null) {
            String id = deployable.toString();
            throw new DeployableNotFoundException("Deployable not found: " + id, id);
        }

        Object object = element.getValue();
        if (!(object instanceof Composite)) {
            String id = deployable.toString();
            throw new IllegalDeployableTypeException("Deployable must be a composite:" + id, id);
        }

        Composite composite = (Composite) object;
        include(composite);

    }

    public void include(Composite composite) throws DeploymentException {

        LogicalCompositeComponent domain = logicalComponentManager.getRootComponent();

        LogicalChange change = logicalModelInstantiator.include(domain, composite);
        if (change.hasErrors()) {
            throw new AssemblyException(change.getErrors(), change.getWarnings());
        } else if (change.hasWarnings()) {
            // TOOD log warnings 
        }
        Collection<LogicalComponent<?>> components = domain.getComponents();

        // Allocate the components to runtime nodes
        try {
            allocate(components);
        } catch (AllocationException e) {
            throw new DeploymentException("Error deploying composite: " + composite.getName());
        }

        try {
            // generate and provision any new components and new wires
            CommandMap commandMap = physicalModelGenerator.generate(components);
            routingService.route(commandMap);
        } catch (GenerationException e) {
            throw new DeploymentException("Error deploying: " + composite.getName(), e);
        } catch (RoutingException e) {
            throw new DeploymentException("Error deploying: " + composite.getName(), e);
        }

        try {
            // TODO this should happen after nodes have deployed the components and wires
            logicalComponentManager.replaceRootComponent(domain);
        } catch (StoreException e) {
            String id = composite.getName().toString();
            throw new DeploymentException("Error activating deployable: " + id, id, e);
        }

    }

    public void remove(QName deployable) throws DeploymentException {

        ResourceElement<QNameSymbol, ?> element;
        try {
            element = metadataStore.resolve(new QNameSymbol(deployable));
        } catch (MetaDataStoreException e) {
            throw new DeploymentException(e);
        }
        if (element == null) {
            String id = deployable.toString();
            throw new DeployableNotFoundException("Deployable not found " + id, id);
        }

        Object object = element.getValue();
        if (!(object instanceof Composite)) {
            String id = deployable.toString();
            throw new IllegalDeployableTypeException("Deployable must be a composite: " + id, id);
        }

        Composite composite = (Composite) object;
        remove(composite);

    }

    public void remove(Composite composite) throws DeploymentException {

        LogicalCompositeComponent domain = logicalComponentManager.getRootComponent();

        LogicalChange change;
        change = logicalModelInstantiator.remove(domain, composite);
        if (change.hasErrors()) {
            throw new AssemblyException(change.getErrors(), change.getWarnings());
        } else if (change.hasWarnings()) {
            // TOOD log warnings
        }

        Collection<LogicalComponent<?>> components = change.getAddedComponents();

        // Allocate the components to runtime nodes
        try {
            allocate(components);
        } catch (AllocationException e) {
            throw new DeploymentException("Error deploying composite: " + composite.getName());
        }

        try {
            // generate and provision any new components and new wires
            CommandMap commandMap = physicalModelGenerator.generate(change);
            routingService.route(commandMap);
        } catch (GenerationException e) {
            throw new DeploymentException("Error deploying: " + composite.getName(), e);
        } catch (RoutingException e) {
            throw new DeploymentException("Error deploying: " + composite.getName(), e);
        }

        try {
            // TODO this should happen after nodes have undeployed the components and wires
            logicalComponentManager.replaceRootComponent(domain);
        } catch (StoreException e) {
            String id = composite.getName().toString();
            throw new DeploymentException("Error activating deployable: " + id, id, e);
        }

    }

    /**
     * Delegates to the Allocator to determine which runtimes to deploy the given collection of components to.
     *
     * @param components the components to allocate
     * @throws AllocationException if an allocation error occurs
     */
    private void allocate(Collection<LogicalComponent<?>> components) throws AllocationException {
        for (LogicalComponent<?> component : components) {
            if (!component.isProvisioned()) {
                allocator.allocate(component, false);
            }
        }
    }


}
