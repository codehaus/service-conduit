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
package org.sca4j.fabric.instantiator;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osoa.sca.annotations.Reference;
import org.sca4j.fabric.instantiator.component.ComponentInstantiator;
import org.sca4j.fabric.instantiator.component.WireInstantiator;
import org.sca4j.fabric.instantiator.normalize.PromotionNormalizer;
import org.sca4j.scdl.Autowire;
import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.scdl.CompositeReference;
import org.sca4j.scdl.CompositeService;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.Include;
import org.sca4j.scdl.Property;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.services.lcm.LogicalComponentManager;
import org.sca4j.spi.util.UriHelper;
import org.w3c.dom.Document;

/**
 * @version $Revision$ $Date$
 */
public class LogicalModelInstantiatorImpl implements LogicalModelInstantiator {

    private final ResolutionService resolutionService;
    private final PromotionNormalizer promotionNormalizer;
    private final LogicalComponentManager logicalComponentManager;
    private final ComponentInstantiator atomicComponentInstantiator;
    private final ComponentInstantiator compositeComponentInstantiator;
    private WireInstantiator wireInstantiator;


    public LogicalModelInstantiatorImpl(@Reference ResolutionService resolutionService,
                                        @Reference PromotionNormalizer promotionNormalizer,
                                        @Reference LogicalComponentManager logicalComponentManager,
                                        @Reference(name = "atomicComponentInstantiator")ComponentInstantiator atomicComponentInstantiator,
                                        @Reference(name = "compositeComponentInstantiator")ComponentInstantiator compositeComponentInstantiator,
                                        @Reference WireInstantiator wireInstantiator) {
        this.resolutionService = resolutionService;
        this.promotionNormalizer = promotionNormalizer;
        this.logicalComponentManager = logicalComponentManager;
        this.atomicComponentInstantiator = atomicComponentInstantiator;
        this.compositeComponentInstantiator = compositeComponentInstantiator;
        this.wireInstantiator = wireInstantiator;
    }

    @SuppressWarnings("unchecked")
    public LogicalChange include(LogicalCompositeComponent targetComposite, Composite composite) {

        LogicalChange change = new LogicalChange(targetComposite);

        // merge the property values into the parent
        Map<String, Document> properties = includeProperties(composite, change);

        // instantiate all the components in the composite and add them to the parent
        List<LogicalComponent<?>> newComponents = instantiateComponents(properties, composite, change);
        List<LogicalService> services = instantiateServices(composite, change);
        List<LogicalReference> references = instantiateReferences(composite, change);

        // explicit wires must be instantiated after the services have been merged
        wireInstantiator.instantiateWires(composite, change.getParent(), change);

        // resolve services and references
        resolve(targetComposite.getComponents(), services, references, change);

        // normalize bindings for each new component
        for (LogicalComponent<?> component : newComponents) {
            normalize(component, change);
        }
        return change;
    }

    public LogicalChange remove(LogicalCompositeComponent targetComposite, Composite composite) {
        LogicalChange change = new LogicalChange(targetComposite);
        // merge the property values into the parent
        excludeProperties(targetComposite, composite, change);
        // merge the component values into the parent
        excludeComponents(targetComposite, composite, change);
        // merge the service values into the parent
        excludeServices(targetComposite, composite, change);
        return change;
    }

    private Map<String, Document> includeProperties(Composite composite, LogicalChange change) {
        LogicalCompositeComponent parent = change.getParent();
        for (Property property : composite.getProperties().values()) {
            String name = property.getName();
            if (parent.getPropertyValues().containsKey(name)) {
                DuplicateProperty error = new DuplicateProperty(parent.getUri(), name);
                change.addError(error);
            } else {
                change.addProperty(name, property.getDefaultValue());
                parent.setPropertyValue(name, property.getDefaultValue());
            }
        }
        return parent.getPropertyValues();
    }

    private List<LogicalComponent<?>> instantiateComponents(Map<String, Document> properties, Composite composite, LogicalChange change) {
        LogicalCompositeComponent parent = change.getParent();
        Collection<ComponentDefinition<? extends Implementation<?>>> definitions = composite.getDeclaredComponents().values();
        List<LogicalComponent<?>> newComponents = new ArrayList<LogicalComponent<?>>(definitions.size());
        for (ComponentDefinition<? extends Implementation<?>> definition : definitions) {
            LogicalComponent<?> logicalComponent = instantiate(parent, properties, null, definition, change);
            setAutowire(composite, definition, logicalComponent);
            newComponents.add(logicalComponent);
            parent.addComponent(logicalComponent);
            change.addComponent(logicalComponent);
        }
        for (Include include : composite.getIncludes().values()) {
            instantiateIncludes(include, parent, properties, change, composite, newComponents);
        }
        return newComponents;
    }
    
    private void instantiateIncludes(Include include, 
    		                         LogicalCompositeComponent parent, 
    		                         Map<String, Document> properties, 
    		                         LogicalChange change, 
    		                         Composite composite, 
    		                         List<LogicalComponent<?>> newComponents) {
        
    	for (ComponentDefinition<? extends Implementation<?>> definition : include.getIncluded().getComponents().values()) {
            URI classLaoderId = URI.create(parent.getUri().toString() + "/" + include.getName().getLocalPart());
            LogicalComponent<?> logicalComponent = instantiate(parent, properties, classLaoderId, definition, change);
            setAutowire(composite, definition, logicalComponent);
            newComponents.add(logicalComponent);
            parent.addComponent(logicalComponent);
            change.addComponent(logicalComponent);
        }
    	
    	for (Include childInclude : include.getIncluded().getIncludes().values()) {
            instantiateIncludes(childInclude, parent, properties, change, composite, newComponents);
    	}
    	
    }

    private void setAutowire(Composite composite, ComponentDefinition<? extends Implementation<?>> definition, LogicalComponent<?> logicalComponent) {
        // use autowire settings on the original composite as an override if they are not specified on the component
        Autowire autowire;
        if (definition.getAutowire() == Autowire.INHERITED) {
            autowire = composite.getAutowire();
        } else {
            autowire = definition.getAutowire();
        }
        if (autowire == Autowire.ON || autowire == Autowire.OFF) {
            logicalComponent.setAutowireOverride(autowire);
        }
    }

    @SuppressWarnings("unchecked")
    private LogicalComponent<?> instantiate(LogicalCompositeComponent parent,
                                            Map<String, Document> properties,
                                            URI classLoaderId,
                                            ComponentDefinition<?> definition,
                                            LogicalChange change) {

        if (definition.getImplementation().isComposite()) {
            ComponentDefinition<CompositeImplementation> componentDefinition = (ComponentDefinition<CompositeImplementation>) definition;
            LogicalComponent<?> component = compositeComponentInstantiator.instantiate(parent, properties, componentDefinition, change);
            component.setClassLoaderId(component.getUri());
            return component;
        } else {
            ComponentDefinition<Implementation<?>> componentDefinition = (ComponentDefinition<Implementation<?>>) definition;
            LogicalComponent<?> component = atomicComponentInstantiator.instantiate(parent, properties, componentDefinition, change);
            if (classLoaderId != null) {
                component.setClassLoaderId(classLoaderId);
            } else {
                component.setClassLoaderId(parent.getUri());
            }
            return component;
        }

    }

    private List<LogicalService> instantiateServices(Composite composite, LogicalChange change) {
        LogicalCompositeComponent parent = change.getParent();
        String base = parent.getUri().toString();
        List<LogicalService> services = new ArrayList<LogicalService>();
        // merge the composite service declarations into the parent
        for (CompositeService compositeService : composite.getServices().values()) {
            URI serviceURI = URI.create(base + '#' + compositeService.getName());
            URI promotedURI = compositeService.getPromote();
            LogicalService logicalService = new LogicalService(serviceURI, compositeService, parent);
            logicalService.setPromotedUri(URI.create(base + "/" + promotedURI));
            for (BindingDefinition binding : compositeService.getBindings()) {
                LogicalBinding<BindingDefinition> logicalBinding = new LogicalBinding<BindingDefinition>(binding, logicalService);
                logicalService.addBinding(logicalBinding);
            }
            for (BindingDefinition binding : compositeService.getCallbackBindings()) {
                LogicalBinding<BindingDefinition> logicalBinding = new LogicalBinding<BindingDefinition>(binding, logicalService);
                logicalService.addCallbackBinding(logicalBinding);
            }
            services.add(logicalService);
            parent.addService(logicalService);
            change.addService(logicalService);
        }
        return services;

    }

    private List<LogicalReference> instantiateReferences(Composite composite, LogicalChange change) {
        LogicalCompositeComponent parent = change.getParent();
        String base = parent.getUri().toString();
        // merge the composite reference definitions into the parent
        List<LogicalReference> references = new ArrayList<LogicalReference>(composite.getReferences().size());
        for (CompositeReference compositeReference : composite.getReferences().values()) {
            URI referenceURi = URI.create(base + '#' + compositeReference.getName());
            LogicalReference logicalReference = new LogicalReference(referenceURi, compositeReference, parent);
            for (URI promotedUri : compositeReference.getPromotedUris()) {
                URI resolvedUri = URI.create(base + "/" + promotedUri.toString());
                logicalReference.addPromotedUri(resolvedUri);
            }
            for (BindingDefinition binding : compositeReference.getBindings()) {
                logicalReference.addBinding(new LogicalBinding<BindingDefinition>(binding, logicalReference));
            }
            for (BindingDefinition binding : compositeReference.getCallbackBindings()) {
                logicalReference.addCallbackBinding(new LogicalBinding<BindingDefinition>(binding, logicalReference));
            }
            references.add(logicalReference);
            parent.addReference(logicalReference);
            change.addReference(logicalReference);
        }
        return references;

    }


    private void resolve(Collection<LogicalComponent<?>> components,
                         List<LogicalService> services,
                         List<LogicalReference> references,
                         LogicalChange change) {

        // resolve composite services merged into the domain
        for (LogicalService service : services) {
            resolutionService.resolve(service, change);
        }

        // resove composite references merged into the domain
        for (LogicalReference reference : references) {
            resolutionService.resolve(reference, logicalComponentManager.getRootComponent(), change);
        }

        // resolve wires for each new component
        for (LogicalComponent<?> component : components) {
            resolutionService.resolve(component, change);
        }

    }

    /**
     * Normalizes the component and any children
     *
     * @param component the component to normalize
     * @param change    the logical change associated with the normalize operation
     */
    private void normalize(LogicalComponent<?> component, LogicalChange change) {
        if (component instanceof LogicalCompositeComponent) {
            LogicalCompositeComponent composite = (LogicalCompositeComponent) component;
            for (LogicalComponent<?> child : composite.getComponents()) {
                normalize(child, change);
            }
        } else {
            promotionNormalizer.normalize(component, change);
        }

    }

    private void excludeServices(LogicalComponent<CompositeImplementation> parent, Composite composite, LogicalChange change) {
        String base = parent.getUri().toString();
        // merge the composite service declarations into the parent
        for (CompositeService compositeService : composite.getServices().values()) {
            URI serviceURI = URI.create(base + '#' + compositeService.getName());
            change.removeService(serviceURI);

        }

    }

    private Map<String, Document> excludeProperties(LogicalCompositeComponent parent, Composite composite, LogicalChange change) {
        Map<String, Document> map = parent.getPropertyValues();
        for (Property property : composite.getProperties().values()) {
            String name = property.getName();
            change.removeProperty(name);
        }
        return map;
    }

    private void excludeComponents(LogicalCompositeComponent parent, Composite composite, LogicalChange change) {
        Set<String> keys = composite.getComponents().keySet();
        for (String key : keys) {
            for (LogicalComponent<?> component : parent.getComponents()) {
                URI uri = component.getUri();
                if (UriHelper.getBaseName(uri).equals(key)) {
                    change.removeComponent(component);
                    parent.removeComponent(uri);
                }
            }
        }
    }

}
