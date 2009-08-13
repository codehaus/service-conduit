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
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import org.sca4j.scdl.validation.MissingImplementation;
import org.sca4j.scdl.validation.NoPropertyInComponentType;
import org.sca4j.scdl.validation.NoReferenceInComponentType;
import org.sca4j.scdl.validation.NoServiceInComponentType;

/**
 * Represents a component. <p>A component is a configured instance of an implementation. The provided and consumed services, as well as the available
 * configuration properties are defined by the implementation (represented by its componentType).</p> <p>Every component has a name which uniquely
 * identifies it within the scope of the composite that contains it; the name must be different from the names of all other components, services and
 * references immediately contained in the composite (directly or through an &lt;include&gt; element).</p> <p>A component may define a {@link
 * PropertyValue} that overrides the default value of a {@link org.sca4j.scdl.Property} specified in the componentType.</p> <p>It may also define a
 * {@link ComponentReference} for a {@link org.sca4j.scdl.ReferenceDefinition} defined in the componentType. The ComponentReference must resolve to
 * another component or a reference in the enclosing composite.</p> <p>A component may define a {@link ComponentService} for a {@link
 * org.sca4j.scdl.ServiceDefinition} specified in the componentType.</p> <p>Components may specify an initialization level that will determine the
 * order in which it will be eagerly initialized relative to other components from the enclosing composite that are in the same scope. This can be
 * used to define a startup sequence for components that are otherwise independent. Any initialization required to resolve references between
 * components will override this initialization order.</p>
 *
 * @version $Rev: 4284 $ $Date: 2008-05-21 03:12:49 +0100 (Wed, 21 May 2008) $
 */
public class ComponentDefinition<I extends Implementation<?>> extends AbstractPolicyAware {
    private static final long serialVersionUID = 4909969579651563484L;

    private final String name;
    private URI runtimeId;
    private Autowire autowire = Autowire.INHERITED;
    private Integer initLevel;
    private I implementation;
    private final Map<String, ComponentService> services = new HashMap<String, ComponentService>();
    private final Map<String, ComponentReference> references = new HashMap<String, ComponentReference>();
    private final Map<String, PropertyValue> propertyValues = new HashMap<String, PropertyValue>();
    private Document key;
    private URI contributionUri;

    /**
     * Constructor specifying the component's name.
     *
     * @param name the name of this component
     */
    public ComponentDefinition(String name) {
        this.name = name;
    }

    /**
     * Constructor specifying the component's name and implementation.
     *
     * @param name           the name of this component
     * @param implementation the implementation of this component
     */
    public ComponentDefinition(String name, I implementation) {
        this.name = name;
        this.implementation = implementation;
    }

    /**
     * Sets the {@link Implementation} of this component.
     *
     * @param implementation the implementation of this component
     */
    public void setImplementation(I implementation) {
        this.implementation = implementation;
    }

    /**
     * Returns the {@link Implementation} of this component.
     *
     * @return the implementation of this component
     */
    public I getImplementation() {
        return implementation;
    }

    /**
     * Returns the name of this component.
     *
     * @return the name of this component
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the id of the node the component is to be provisioned to.
     *
     * @return the id of the node the component is to be provisioned to
     */
    public URI getRuntimeId() {
        return runtimeId;
    }

    /**
     * Sets the id of the node the component is to be provisioned to.
     *
     * @param id the id of the node the component is to be provisioned to
     */
    public void setRuntimeId(URI id) {
        this.runtimeId = id;
    }

    /**
     * Returns the autowire status for the component.
     *
     * @return the autowire status for the component.
     */
    public Autowire getAutowire() {
        return autowire;
    }

    /**
     * Sets the autowire status for the component.
     *
     * @param autowire the autowire status.
     */
    public void setAutowire(Autowire autowire) {
        this.autowire = autowire;
    }

    /**
     * Returns the initialization level of this component.
     *
     * @return the initialization level of this component
     */
    public Integer getInitLevel() {
        return initLevel;
    }

    /**
     * Sets the initialization level of this component. If set to null then the level from the componentType is used. If set to zero or a negative
     * value then the component will not be eagerly initialized.
     *
     * @param initLevel the initialization level of this component
     */
    public void setInitLevel(Integer initLevel) {
        this.initLevel = initLevel;
    }

    /**
     * Returns a live Map of the {@link ComponentReference targets} configured by this component definition.
     *
     * @return the reference targets configured by this component
     */
    public Map<String, ComponentReference> getReferences() {
        return references;
    }

    /**
     * Add a reference target configuration to this component. Any existing configuration for the reference named in the target is replaced.
     *
     * @param target the target to add
     */
    public void add(ComponentReference target) {
        references.put(target.getName(), target);
    }

    /**
     * Returns a live Map of the {@link ComponentService}s configured by this component definition.
     *
     * @return the services configured by this component
     */
    public Map<String, ComponentService> getServices() {
        return services;
    }

    /**
     * Add a service configuration to this component. Any existing configuration for the service is replaced.
     *
     * @param service the service to add
     */
    public void add(ComponentService service) {
        services.put(service.getName(), service);
    }

    /**
     * Returns a live Map of {@link PropertyValue property values} configured by this component definition.
     *
     * @return the property values configured by this component
     */
    public Map<String, PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    /**
     * Add a property value configuration to this component. Any existing configuration for the property names in the property value is replaced.
     *
     * @param value the property value to add
     */
    public void add(PropertyValue value) {
        propertyValues.put(value.getName(), value);
    }

    /**
     * Returns the key to be used if this component is wired to a map of references.
     *
     * @return The value of the key.
     */
    public Document getKey() {
        return key;
    }

    /**
     * Returns the key to be used if this component is wired to a map of references.
     *
     * @param key The value of the key.
     */
    public void setKey(Document key) {
        this.key = key;
    }

    /**
     * Gets the component type.
     *
     * @return Component type.
     */
    public AbstractComponentType<?, ?, ?, ?> getComponentType() {
        return getImplementation().getComponentType();
    }

    /**
     * Returns the URI of the contribution the component definition is contained in.
     *
     * @return the URI of the contribution the component definition is contained in.
     */
    public URI getContributionUri() {
        return contributionUri;
    }

    /**
     * Sets the URI of the contribution the component definition is contained in.
     *
     * @param contributionUri the URI of the contribution the component definition is contained in.
     */
    public void setContributionUri(URI contributionUri) {
        this.contributionUri = contributionUri;
    }

    @Override
    public void validate(ValidationContext context) {
        super.validate(context);

        AbstractComponentType componentType;
        if (implementation == null) {
            context.addError(new MissingImplementation(this));
            componentType = null;
        } else {
            implementation.validate(context);
            componentType = implementation.getComponentType();
        }

        for (ComponentService service : services.values()) {
            service.validate(context);
            if (componentType != null && !componentType.getServices().containsKey(service.getName())) {
                context.addError(new NoServiceInComponentType(service));
            }
        }
        for (ComponentReference reference : references.values()) {
            reference.validate(context);
            if (componentType != null && !componentType.getReferences().containsKey(reference.getName())) {
                context.addError(new NoReferenceInComponentType(reference));
            }
        }
        for (PropertyValue propertyValue : propertyValues.values()) {
            propertyValue.validate(context);
            if (componentType != null && !componentType.getProperties().containsKey(propertyValue.getName())) {
                context.addError(new NoPropertyInComponentType(propertyValue));
            }
        }
    }
}
