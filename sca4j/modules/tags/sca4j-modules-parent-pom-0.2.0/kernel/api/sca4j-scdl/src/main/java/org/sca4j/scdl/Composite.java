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
package org.sca4j.scdl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * A specialization of component type for composite components.
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class Composite extends AbstractComponentType<CompositeService, CompositeReference, Property, ResourceDefinition> implements PolicyAware {
    private static final long serialVersionUID = -3126069884608566611L;

    private final QName name;
    private URI contributionUri;
    private boolean local;
    private Autowire autowire;
    private final Map<String, ComponentDefinition<? extends Implementation<?>>> components =
            new HashMap<String, ComponentDefinition<? extends Implementation<?>>>();
    private final Map<QName, Include> includes = new HashMap<QName, Include>();
    private final List<WireDefinition> wires = new ArrayList<WireDefinition>();

    // views are caches of all properties, references, wires, or components contained in the composite and its included composites
    private final Map<String, Property> propertiesView = new HashMap<String, Property>();
    private final Map<String, CompositeReference> referencesView = new HashMap<String, CompositeReference>();
    private final Map<String, CompositeService> servicesView = new HashMap<String, CompositeService>();
    private final Map<String, ComponentDefinition<? extends Implementation<?>>> componentsView =
            new HashMap<String, ComponentDefinition<? extends Implementation<?>>>();
    private final List<WireDefinition> wiresView = new ArrayList<WireDefinition>();

    private QName constrainingType;
    private List<QName> intents;
    private List<QName> policySets;

    /**
     * Constructor defining the composite name.
     *
     * @param name the qualified name of this composite
     */
    public Composite(QName name) {
        this.name = name;
        setScope("COMPOSITE");
    }

    /**
     * Returns the qualified name of this composite. The namespace portion of this name is the targetNamespace for other qualified names used in the
     * composite.
     *
     * @return the qualified name of this composite
     */
    public QName getName() {
        return name;
    }

    /**
     * Returns the URI of the contribution this componentType is associated with.
     *
     * @return the URI of the contribution this componentType is associated with
     */
    public URI getContributionUri() {
        return contributionUri;
    }

    /**
     * Sets the URI of the contribution this componentType is associated with.
     *
     * @param contributionUri tcontribution URI
     */
    public void setContributionUri(URI contributionUri) {
        this.contributionUri = contributionUri;
    }

    /**
     * Indicates that components in this composite should be co-located.
     *
     * @return true if components in this composite should be co-located
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * Sets whether components in this composite should be co-located.
     *
     * @param local true if components in this composite should be co-located
     */
    public void setLocal(boolean local) {
        this.local = local;
    }

    /**
     * Returns if the autowire status for composite
     *
     * @return the autowire status for the composite
     */
    public Autowire getAutowire() {
        return autowire;
    }

    /**
     * Sets the autowire status for the composite
     *
     * @param autowire the autowire status for the composite
     */
    public void setAutowire(Autowire autowire) {
        this.autowire = autowire;
    }

    /**
     * Returns the name of the constraining type for this composite.
     *
     * @return the name of the constraining type for this composite
     */
    public QName getConstrainingType() {
        return constrainingType;
    }

    /**
     * Sets the name of the constraining type for this composite.
     *
     * @param constrainingType the name of the constraining type for this composite
     */
    public void setConstrainingType(QName constrainingType) {
        this.constrainingType = constrainingType;
    }

    @Override
    /**
     * Get all properties including the ones are from included composites
     * @return
     */
    public Map<String, Property> getProperties() {
        return propertiesView;
    }

    public void add(Property property) {
        super.add(property);
        propertiesView.put(property.getName(), property);
    }

    @Override
    /**
     * Get all references including the ones are from included composites
     * @return
     */
    public Map<String, CompositeReference> getReferences() {
        return referencesView;
    }

    public void add(CompositeReference reference) {
        super.add(reference);
        referencesView.put(reference.getName(), reference);
    }

    @Override
    /**
     * Get all services including the ones are from included composites
     * @return
     */
    public Map<String, CompositeService> getServices() {
        return servicesView;
    }

    public void add(CompositeService service) {
        super.add(service);
        servicesView.put(service.getName(), service);
    }

    /**
     * Get all components including the ones are from included composites
     */
    public Map<String, ComponentDefinition<? extends Implementation<?>>> getComponents() {
        return componentsView;
    }

    public void add(ComponentDefinition<? extends Implementation<?>> componentDefinition) {
        componentsView.put(componentDefinition.getName(), componentDefinition);
        components.put(componentDefinition.getName(), componentDefinition);
    }



    /**
     * Returns a collection of potential target services that match the supplied contract.
     * <p/>
     * This can be used to determine potential autowire targets for the service, based on compatibility of the contract.
     *
     * @param contract the candidate contract
     * @return a collection of potential targets
     */
    public Collection<URI> getTargets(ServiceContract<?> contract) {
        Collection<URI> targets = new ArrayList<URI>();
        for (ComponentDefinition<? extends Implementation<?>> component : getComponents().values()) {
            AbstractComponentType<?, ?, ?, ?> componentType = component.getComponentType();
            for (ServiceDefinition service : componentType.getServices().values()) {
                if (contract.isAssignableFrom(service.getServiceContract())) {
                    URI uri = URI.create(component.getName() + '#' + service.getName());
                    targets.add(uri);
                }
            }
        }
        return targets;
    }


    /**
     * Get all wires including the ones are from included composites
     */
    public List<WireDefinition> getWires() {
        return wiresView;
    }

    /**
     * Get declared properties in this composite type, included doesn't count
     */
    public Map<String, Property> getDeclaredProperties() {
        return super.getProperties();
    }

    /**
     * Get declared references in this composite type, included doesn't count
     */
    public Map<String, CompositeReference> getDeclaredReferences() {
        return super.getReferences();
    }

    /**
     * Get declared services in this composite type, included doesn't count
     */
    public Map<String, CompositeService> getDeclaredServices() {
        return super.getServices();
    }

    /**
     * Get declared components in this composite type, included doesn't count
     */
    public Map<String, ComponentDefinition<? extends Implementation<?>>> getDeclaredComponents() {
        return components;
    }

    /**
     * Get declared wires in this composite type, included doesn't count
     */
    public List<WireDefinition> getDeclaredWires() {
        return wires;
    }

    public void add(WireDefinition wireDefn) {
        wires.add(wireDefn);
        wiresView.add(wireDefn);
    }


    public Map<QName, Include> getIncludes() {
        return includes;
    }

    public void add(Include include) {
        includes.put(include.getName(), include);
        componentsView.putAll(include.getIncluded().getComponents());
        referencesView.putAll(include.getIncluded().getReferences());
        propertiesView.putAll(include.getIncluded().getProperties());
        servicesView.putAll(include.getIncluded().getServices());
        wiresView.addAll(include.getIncluded().getWires());
    }


    public List<QName> getIntents() {
        return intents;
    }

    public void setIntents(List<QName> intents) {
        this.intents = intents;
    }

    public List<QName> getPolicySets() {
        return policySets;
    }

    public void setPolicySets(List<QName> policySets) {
        this.policySets = policySets;
    }

    public int hashCode() {
        return name.hashCode();
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Composite that = (Composite) o;
        return name.equals(that.name);
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);
        for (Include include : includes.values()) {
            include.validate(context);
        }
        for (ComponentDefinition<? extends Implementation<?>> component : components.values()) {
            component.validate(context);
        }
        for (WireDefinition wire : wires) {
            wire.validate(context);
        }
    }
}
