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
package org.sca4j.fabric.domain;

import static org.oasisopen.sca.Constants.SCA_NS;

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
import org.sca4j.spi.services.contribution.CompositeResourceElement;
import org.sca4j.spi.services.contribution.MetaDataStore;
import org.sca4j.spi.services.contribution.MetaDataStoreException;
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
    protected final LogicalComponentManager logicalComponentManager;
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

        CompositeResourceElement element;
        try {
            element = metadataStore.resolve(deployable, CompositeResourceElement.class);
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

        CompositeResourceElement element;
        try {
            element = metadataStore.resolve(deployable, CompositeResourceElement.class);
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
