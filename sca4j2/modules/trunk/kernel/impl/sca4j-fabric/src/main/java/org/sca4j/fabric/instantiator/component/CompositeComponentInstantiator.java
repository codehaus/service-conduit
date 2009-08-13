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
package org.sca4j.fabric.instantiator.component;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Document;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.fabric.services.documentloader.DocumentLoader;
import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ComponentReference;
import org.sca4j.scdl.ComponentService;
import org.sca4j.scdl.Composite;
import org.sca4j.scdl.CompositeImplementation;
import org.sca4j.scdl.CompositeReference;
import org.sca4j.scdl.CompositeService;
import org.sca4j.scdl.Implementation;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalService;
import org.sca4j.spi.model.instance.LogicalWire;

/**
 * Instatiates a composite component in the logical representation of a domain. Child components will be recursively instantiated if they exist.
 *
 * @version $Revision$ $Date$
 */
public class CompositeComponentInstantiator extends AbstractComponentInstantiator {

    private ComponentInstantiator atomicComponentInstantiator;
    private WireInstantiator wireInstantiator;

    public CompositeComponentInstantiator(
            @Reference(name = "atomicComponentInstantiator")ComponentInstantiator atomicComponentInstantiator,
            @Reference WireInstantiator wireInstantiator,
            @Reference(name = "documentLoader")DocumentLoader documentLoader) {
        super(documentLoader);
        this.atomicComponentInstantiator = atomicComponentInstantiator;
        this.wireInstantiator = wireInstantiator;
    }

    @SuppressWarnings("unchecked")
    public <I extends Implementation<?>> LogicalComponent<I> instantiate(LogicalCompositeComponent parent,
                                                                         Map<String, Document> properties,
                                                                         ComponentDefinition<I> definition,
                                                                         LogicalChange change) {
        ComponentDefinition<CompositeImplementation> def = (ComponentDefinition<CompositeImplementation>) definition;
        return LogicalComponent.class.cast(instantiateComposite(parent, properties, def, change));
    }

    private LogicalCompositeComponent instantiateComposite(LogicalCompositeComponent parent,
                                                           Map<String, Document> properties,
                                                           ComponentDefinition<CompositeImplementation> definition,
                                                           LogicalChange change) {

        URI runtimeId = definition.getRuntimeId();
        URI uri = URI.create(parent.getUri() + "/" + definition.getName());
        Composite composite = definition.getImplementation().getComponentType();

        LogicalCompositeComponent component = new LogicalCompositeComponent(uri, runtimeId, definition, parent);
        component.setClassLoaderId(uri);
        initializeProperties(component, definition, change);
        instantiateChildComponents(component, properties, composite, change);
        instantiateCompositeServices(component, composite);
        instantiateCompositeReferences(parent, component, composite, change);
        wireInstantiator.instantiateWires(composite, component, change);
        return component;

    }

    private void instantiateChildComponents(LogicalCompositeComponent parent,
                                            Map<String, Document> properties,
                                            Composite composite,
                                            LogicalChange change) {

        // create the child components
        for (ComponentDefinition<? extends Implementation<?>> child : composite.getDeclaredComponents().values()) {

            LogicalComponent<?> childComponent;
            if (child.getImplementation().isComposite()) {
                childComponent = instantiate(parent, properties, child, change);
            } else {
                childComponent = atomicComponentInstantiator.instantiate(parent, properties, child, change);
                childComponent.setClassLoaderId(parent.getClassLoaderId());
            }
            parent.addComponent(childComponent);
        }

    }

    private void instantiateCompositeServices(LogicalCompositeComponent component, Composite composite) {

        ComponentDefinition<CompositeImplementation> definition = component.getDefinition();
        String uriBase = component.getUri().toString() + "/";

        for (CompositeService service : composite.getServices().values()) {

            String name = service.getName();
            URI serviceUri = component.getUri().resolve('#' + name);
            LogicalService logicalService = new LogicalService(serviceUri, service, component);
            logicalService.setPromotedUri(URI.create(uriBase + service.getPromote()));

            for (BindingDefinition binding : service.getBindings()) {
                logicalService.addBinding(new LogicalBinding<BindingDefinition>(binding, logicalService));
            }

            for (BindingDefinition binding : service.getCallbackBindings()) {
                logicalService.addCallbackBinding(new LogicalBinding<BindingDefinition>(binding, logicalService));
            }

            ComponentService componentService = definition.getServices().get(name);
            if (componentService != null) {
                // Merge/override logical reference configuration created above with service configuration on the
                // composite use. For example, when the component is used as an implementation, it may contain
                // service configuration. This information must be merged with or used to override any
                // configuration that was created by service promotions within the composite
                if (!componentService.getBindings().isEmpty()) {
                    List<LogicalBinding<?>> bindings = new ArrayList<LogicalBinding<?>>();
                    for (BindingDefinition binding : componentService.getBindings()) {
                        bindings.add(new LogicalBinding<BindingDefinition>(binding, logicalService));
                    }
                    logicalService.overrideBindings(bindings);
                }
            }

            component.addService(logicalService);

        }

    }

    private void instantiateCompositeReferences(LogicalCompositeComponent parent,
                                                LogicalCompositeComponent component,
                                                Composite composite,
                                                LogicalChange change) {

        ComponentDefinition<CompositeImplementation> definition = component.getDefinition();
        String uriBase = component.getUri().toString() + "/";

        // create logical references based on promoted references in the composite definition
        for (CompositeReference reference : composite.getReferences().values()) {

            String name = reference.getName();
            URI referenceUri = component.getUri().resolve('#' + name);
            LogicalReference logicalReference = new LogicalReference(referenceUri, reference, component);

            for (BindingDefinition binding : reference.getBindings()) {
                logicalReference.addBinding(new LogicalBinding<BindingDefinition>(binding, logicalReference));
            }

            for (BindingDefinition binding : reference.getCallbackBindings()) {
                logicalReference.addCallbackBinding(new LogicalBinding<BindingDefinition>(binding, logicalReference));
            }

            for (URI promotedUri : reference.getPromotedUris()) {
                URI resolvedUri = URI.create(uriBase + promotedUri.toString());
                logicalReference.addPromotedUri(resolvedUri);
            }

            ComponentReference componentReference = definition.getReferences().get(name);

            if (componentReference != null) {

                // Merge/override logical reference configuration created above with reference configuration on the
                // composite use. For example, when the component is used as an implementation, it may contain
                // reference configuration. This information must be merged with or used to override any
                // configuration that was created by reference promotions within the composite
                if (!componentReference.getBindings().isEmpty()) {
                    List<LogicalBinding<?>> bindings = new ArrayList<LogicalBinding<?>>();
                    for (BindingDefinition binding : componentReference.getBindings()) {
                        bindings.add(new LogicalBinding<BindingDefinition>(binding, logicalReference));
                    }
                    logicalReference.overrideBindings(bindings);
                }

                if (!componentReference.getTargets().isEmpty()) {
                    List<URI> targets = new ArrayList<URI>();
                    for (URI targetUri : componentReference.getTargets()) {
                        // the target is relative to the component's parent, not the component
                        targets.add(URI.create(parent.getUri().toString() + "/" + targetUri));
                    }
                    // xcv potentially remove if LogicalWires added to LogicalReference
                    LogicalCompositeComponent grandParent = parent.getParent();
                    Set<LogicalWire> wires = new LinkedHashSet<LogicalWire>();
                    if (null != grandParent) {
                        for (URI targetUri : targets) {
                            LogicalWire wire = new LogicalWire(grandParent, logicalReference, targetUri);
                            change.addWire(wire);
                            wires.add(wire);
                        }
                        grandParent.overrideWires(logicalReference, wires);
                    } else {
                        for (URI targetUri : targets) {
                            LogicalWire wire = new LogicalWire(parent, logicalReference, targetUri);
                            change.addWire(wire);
                            wires.add(wire);
                        }
                        parent.overrideWires(logicalReference, wires);
                    }
                    // end remove
                }

            }

            component.addReference(logicalReference);

        }
    }


}
