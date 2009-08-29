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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.fabric.instantiator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import org.sca4j.host.domain.AssemblyFailure;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.instance.LogicalWire;

/**
 * @version $Rev: 5095 $ $Date: 2008-07-28 18:49:36 +0100 (Mon, 28 Jul 2008) $
 */
public class LogicalChange {

    private final LogicalCompositeComponent parent;

    private final List<AssemblyFailure> errors = new ArrayList<AssemblyFailure>();
    private final List<AssemblyFailure> warnings = new ArrayList<AssemblyFailure>();

    private final Map<String, Document> addedProperties = new HashMap<String, Document>();
    private final List<String> deletedProperties = new ArrayList<String>();

    private final List<LogicalComponent<?>> addedComponents = new ArrayList<LogicalComponent<?>>();
    private final List<LogicalComponent<?>> deletedComponents = new ArrayList<LogicalComponent<?>>();

    private final List<LogicalService> addedServices = new ArrayList<LogicalService>();
    private final List<URI> deletedServices = new ArrayList<URI>();

    private final List<LogicalReference> addedReferences = new ArrayList<LogicalReference>();
    private final Map<URI, List<LogicalWire>> addedWires = new HashMap<URI, List<LogicalWire>>();

    /**
     * Construct a logical change specifiying the context to which it applies.
     *
     * @param parent the context to which this change applies
     */
    public LogicalChange(LogicalCompositeComponent parent) {
        this.parent = parent;
    }

    /**
     * Returns the component the change is targeted at.
     *
     * @return the component the change is targeted at.
     */
    public LogicalCompositeComponent getParent() {
        return parent;
    }

    /**
     * Returns true if the change generation has detected any fatal errors.
     *
     * @return true if the change generation has detected any fatal errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Returns the list of fatal errors detected during change generation.
     *
     * @return the list of fatal errors detected during change generation
     */
    public List<AssemblyFailure> getErrors() {
        return errors;
    }

    /**
     * Add a fatal error to the chnage.
     *
     * @param error the fatal error that has been found
     */
    public void addError(AssemblyFailure error) {
        errors.add(error);
    }

    /**
     * Add a collection of fatal errors to the change.
     *
     * @param errors the fatal errors that have been found
     */
    public void addErrors(List<AssemblyFailure> errors) {
        this.errors.addAll(errors);
    }

    /**
     * Returns true if the change generation has detected any non-fatal warnings.
     *
     * @return true if the change generation has detected any non-fatal warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Returns the list of non-fatal warnings detected during change generation.
     *
     * @return the list of non-fatal warnings detected during change generation
     */
    public List<AssemblyFailure> getWarnings() {
        return warnings;
    }

    /**
     * Add a non-fatal warning to the change.
     *
     * @param warning the non-fatal warning that has been found
     */
    public void addWarning(AssemblyFailure warning) {
        warnings.add(warning);
    }

    /**
     * Add a collection of non-fatal warnings to the change.
     *
     * @param warnings the non-fatal warnings that have been found
     */
    public void addWarnings(List<AssemblyFailure> warnings) {
        this.warnings.addAll(warnings);
    }


    /**
     * Record a property being added.
     *
     * @param name  the name of the property to add
     * @param value the actual value of the property
     */
    public void addProperty(String name, Document value) {
        addedProperties.put(name, value);
    }

    /**
     * Record a property being removed.
     *
     * @param name the name of the property
     */
    public void removeProperty(String name) {
        deletedProperties.add(name);
    }

    /**
     * Record a component being added.
     *
     * @param component the component to add
     */
    public void addComponent(LogicalComponent<?> component) {
        addedComponents.add(component);
    }

    /**
     * Record a component being removed.
     *
     * @param component the component
     */
    public void removeComponent(LogicalComponent<?> component) {
        deletedComponents.add(component);
    }


    public void removeService(URI uri) {
        deletedServices.add(uri);
    }

    /**
     * Return the list of new components.
     *
     * @return the list of added components
     */
    public List<LogicalComponent<?>> getAddedComponents() {
        return addedComponents;
    }

    /**
     * Return the list of deleted components
     *
     * @return the list of deleted components
     */

    public List<LogicalComponent<?>> getDeletedComponents() {
        return deletedComponents;
    }

    /**
     * Record a wire being added.
     *
     * @param wire the wire
     */
    public void addWire(LogicalWire wire) {
        URI uri = wire.getSource().getUri();
        List<LogicalWire> wires = addedWires.get(uri);
        if (wires == null) {
            wires = new ArrayList<LogicalWire>();
            addedWires.put(uri, wires);
        }
        wires.add(wire);
    }

    /**
     * Returns added wires for a source (reference) URI.
     *
     * @param uri the source URI
     * @return the added wires
     */
    public List<LogicalWire> getAddedWires(URI uri) {
        return addedWires.get(uri);
    }

    /**
     * Returns the wires that were added to the change.
     *
     * @return the wires
     */
    public List<LogicalWire> getAddedWires() {
        List<LogicalWire> wires = new ArrayList<LogicalWire>();
        for (List<LogicalWire> list : addedWires.values()) {
            wires.addAll(list);
        }
        return wires;
    }


    /**
     * Records a service being added.
     *
     * @param service the service
     */
    public void addService(LogicalService service) {
        addedServices.add(service);
    }

    /**
     * Returns the collection of added services.
     *
     * @return the added services
     */
    public List<LogicalService> getAddedServices() {
        return addedServices;
    }

    /**
     * Records a reference being added.
     *
     * @param reference the service
     */
    public void addReference(LogicalReference reference) {
        addedReferences.add(reference);
    }

    /**
     * Returns the collection of added references.
     *
     * @return the added references
     */
    public List<LogicalReference> getAddedReferences() {
        return addedReferences;
    }

    /**
     * Returns the collection of deleted service URIs.
     *
     * @return the added references
     */
    public List<URI> getDeletedReferences() {
        return deletedServices;
    }


    /**
     * Returns the map of added properties keyed by name.
     *
     * @return the added properties
     */
    public Map<String, Document> getAddedProperties() {
        return addedProperties;
    }

    /**
     * Returns the names of deleted properties.
     *
     * @return the names of the deleted properties
     */
    public List<String> getDeletedProperties() {
        return deletedProperties;
    }

}
