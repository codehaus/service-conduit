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
package org.sca4j.spi.model.instance;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

import org.osoa.sca.Constants;
import org.w3c.dom.Document;

import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Implementation;

/**
 * Represents an instantiated component in the service network.
 *
 * @version $Rev: 5280 $ $Date: 2008-08-26 15:57:57 +0100 (Tue, 26 Aug 2008) $
 */
public class LogicalComponent<I extends Implementation<?>> extends LogicalScaArtifact<LogicalCompositeComponent> {
    private static final long serialVersionUID = -3520150701040845117L;

    private static final QName TYPE = new QName(Constants.SCA_NS, "component");

    private final ComponentDefinition<I> definition;
    private final Map<String, Document> propertyValues = new HashMap<String, Document>();
    private final Map<String, LogicalService> services = new HashMap<String, LogicalService>();
    private final Map<String, LogicalReference> references = new HashMap<String, LogicalReference>();
    private final Map<String, LogicalResource<?>> resources = new HashMap<String, LogicalResource<?>>();
    private URI classLoaderId;
    private URI runtimeId;
    private boolean active;
    private Autowire autowire;
    private boolean provisioned;

    /**
     * @param uri        URI of the component.
     * @param runtimeId  URI of the runtime to which the component has to be provisioned.
     * @param definition Definition of the component.
     * @param parent     Parent of the component.
     */
    public LogicalComponent(URI uri, URI runtimeId, ComponentDefinition<I> definition, LogicalCompositeComponent parent) {
        super(uri, parent, TYPE);
        this.runtimeId = runtimeId;
        this.definition = definition;
    }

    /**
     * Returns the runtime id the component is provisioned to.
     *
     * @return the runtime id the component is provisioned to
     */
    public URI getRuntimeId() {
        return runtimeId;
    }

    /**
     * Sets the runtime id the component is provisioned to.
     *
     * @param runtimeId the runtime id the component is provisioned to
     */
    public void setRuntimeId(URI runtimeId) {
        this.runtimeId = runtimeId;
    }

    /**
     * True if the component is curently active on a node.
     *
     * @return true if the component is curently active on a node
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets if the component is currently active on a node.
     *
     * @param active true if the component is active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the overriden autowire value or null if not overriden
     *
     * @return the overriden autowire value or null if not overriden
     */
    public Autowire getAutowireOverride() {
        return autowire;
    }

    /**
     * Sets the overriden autowire value
     *
     * @param autowire the autowire value
     */
    public void setAutowireOverride(Autowire autowire) {
        this.autowire = autowire;
    }

    /**
     * Returns the services offered by the current component.
     *
     * @return the services offered by the current component
     */
    public Collection<LogicalService> getServices() {
        return services.values();
    }

    /**
     * Returns a service with the given URI.
     *
     * @param name the service name
     * @return the service.
     */
    public LogicalService getService(String name) {
        return services.get(name);
    }

    /**
     * Adds a the resolved service
     *
     * @param service the service to add
     */
    public void addService(LogicalService service) {
        services.put(service.getUri().getFragment(), service);
    }

    /**
     * Returns the resources required by the current component.
     *
     * @return the resources required by the current component
     */
    public Collection<LogicalResource<?>> getResources() {
        return resources.values();
    }

    /**
     * Returns a resource with the given URI.
     *
     * @param name the resource name
     * @return the resource.
     */
    public LogicalResource<?> getResource(String name) {
        return resources.get(name);
    }

    /**
     * Adds a the resolved resource
     *
     * @param resource the resource to add
     */
    public void addResource(LogicalResource<?> resource) {
        resources.put(resource.getUri().getFragment(), resource);
    }

    /**
     * Returns the resolved component references.
     *
     * @return the component references
     */
    public Collection<LogicalReference> getReferences() {
        return references.values();
    }

    /**
     * Returns a the resolved reference with the given URI.
     *
     * @param name the reference name
     * @return the reference.
     */
    public LogicalReference getReference(String name) {
        return references.get(name);
    }

    /**
     * Adds a resolved reference
     *
     * @param reference the reference to add
     */
    public void addReference(LogicalReference reference) {
        references.put(reference.getUri().getFragment(), reference);
    }

    /**
     * Returns the resolved property values for the component.
     *
     * @return the resolved property values for the component
     */
    public Map<String, Document> getPropertyValues() {
        return propertyValues;
    }

    /**
     * Gets the value of a property.
     *
     * @param name Name of the property.
     * @return Property value for the specified property.
     */
    public Document getPropertyValue(String name) {
        return propertyValues.get(name);
    }

    /**
     * Sets a resolved property value
     *
     * @param name  the property name
     * @param value the property value
     */
    public void setPropertyValue(String name, Document value) {
        propertyValues.put(name, value);
    }

    /**
     * Returns the component implementation type.
     *
     * @return the component implementation type
     */
    public ComponentDefinition<I> getDefinition() {
        return definition;
    }

    /**
     * Gets the component type.
     *
     * @return Component type.
     */
    public AbstractComponentType<?, ?, ?, ?> getComponentType() {
        return getDefinition().getComponentType();
    }

    /**
     * Checks whether this component needs to be eager inited.
     *
     * @return True if the component needs to be eager inited.
     */
    public boolean isEagerInit() {

        ComponentDefinition<? extends Implementation<?>> definition = getDefinition();
        AbstractComponentType<?, ?, ?, ?> componentType = definition.getImplementation().getComponentType();

        Integer level = definition.getInitLevel();
        if (level == null) {
            level = componentType.getInitLevel();
        }
        return "COMPOSITE".equals(componentType.getScope()) && level > 0;

    }

    /**
     * @return Intents declared on the SCA artifact.
     */
    public Set<QName> getIntents() {
        return definition.getIntents();
    }

    /**
     * @param intents Intents declared on the SCA artifact.
     */
    public void setIntents(Set<QName> intents) {
        definition.setIntents(intents);
    }

    /**
     * @return Policy sets declared on the SCA artifact.
     */
    public Set<QName> getPolicySets() {
        return definition.getPolicySets();
    }

    /**
     * @param policySets Policy sets declared on the SCA artifact.
     */
    public void setPolicySets(Set<QName> policySets) {
        definition.setPolicySets(policySets);
    }

    /**
     * Checks whether the wire has been provisioned.
     *
     * @return True if the wire has been provisioned.
     */
    public boolean isProvisioned() {
        return provisioned;
    }

    /**
     * Marks thw wire as provisioned/unprovisioned.
     *
     * @param provisioned True if the wire has been provisioned.
     */
    public void setProvisioned(boolean provisioned) {
        this.provisioned = provisioned;
    }

    /**
     * Returns the classloader id the component is associated with.
     *
     * @return the classloader id the component is associated with
     */
    public URI getClassLoaderId() {
        return classLoaderId;
    }

    /**
     * Sets the classloader id the component is associated with.
     *
     * @param id the classloader id the component is associated with
     */
    public void setClassLoaderId(URI id) {
        this.classLoaderId = id;
    }
}
