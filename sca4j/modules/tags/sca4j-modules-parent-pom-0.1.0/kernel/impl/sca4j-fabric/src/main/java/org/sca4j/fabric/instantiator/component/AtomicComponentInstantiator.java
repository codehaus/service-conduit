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
import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.w3c.dom.Document;

import org.sca4j.fabric.instantiator.LogicalChange;
import org.sca4j.fabric.services.documentloader.DocumentLoader;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.BindingDefinition;
import org.sca4j.scdl.ComponentDefinition;
import org.sca4j.scdl.ComponentReference;
import org.sca4j.scdl.ComponentService;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ResourceDefinition;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.spi.model.instance.LogicalCompositeComponent;
import org.sca4j.spi.model.instance.LogicalReference;
import org.sca4j.spi.model.instance.LogicalResource;
import org.sca4j.spi.model.instance.LogicalService;

/**
 * @version $Revision$ $Date$
 */
public class AtomicComponentInstantiator extends AbstractComponentInstantiator {

    public AtomicComponentInstantiator(@Reference(name = "documentLoader")DocumentLoader documentLoader) {
        super(documentLoader);
    }

    public <I extends Implementation<?>> LogicalComponent<I> instantiate(LogicalCompositeComponent parent,
                                                                         Map<String, Document> properties,
                                                                         ComponentDefinition<I> definition,
                                                                         LogicalChange change) {

        I impl = definition.getImplementation();
        AbstractComponentType<?, ?, ?, ?> componentType = impl.getComponentType();

        URI runtimeId = definition.getRuntimeId();
        URI uri = URI.create(parent.getUri() + "/" + definition.getName());
        LogicalComponent<I> component = new LogicalComponent<I>(uri, runtimeId, definition, parent);
        initializeProperties(component, definition, change);
        createServices(definition, component, componentType);
        createReferences(definition, component, componentType);
        createResources(component, componentType);
        return component;

    }

    private <I extends Implementation<?>> void createServices(ComponentDefinition<I> definition,
                                                              LogicalComponent<I> component,
                                                              AbstractComponentType<?, ?, ?, ?> componentType) {

        for (ServiceDefinition service : componentType.getServices().values()) {
            String name = service.getName();
            URI serviceUri = component.getUri().resolve('#' + name);
            LogicalService logicalService = new LogicalService(serviceUri, service, component);

            for (BindingDefinition binding : service.getBindings()) {
                logicalService.addBinding(new LogicalBinding<BindingDefinition>(binding, logicalService));
            }

            for (BindingDefinition binding : service.getCallbackBindings()) {
                logicalService.addCallbackBinding(new LogicalBinding<BindingDefinition>(binding, logicalService));
            }

            // service is configured in the component definition
            ComponentService componentService = definition.getServices().get(name);
            if (componentService != null) {
                logicalService.addIntents(componentService.getIntents());
                addOperationLevelIntentsAndPolicies(logicalService, componentService);
                for (BindingDefinition binding : componentService.getBindings()) {
                    logicalService.addBinding(new LogicalBinding<BindingDefinition>(binding, logicalService));
                }
                for (BindingDefinition binding : componentService.getCallbackBindings()) {
                    logicalService.addCallbackBinding(new LogicalBinding<BindingDefinition>(binding, logicalService));
                }
            }
            component.addService(logicalService);
        }

    }

    private <I extends Implementation<?>> void createReferences(ComponentDefinition<I> definition,
                                                                LogicalComponent<I> component,
                                                                AbstractComponentType<?, ?, ?, ?> componentType) {

        for (ReferenceDefinition reference : componentType.getReferences().values()) {
            String name = reference.getName();
            URI referenceUri = component.getUri().resolve('#' + name);
            LogicalReference logicalReference = new LogicalReference(referenceUri, reference, component);

            // reference is configured in the component definition
            ComponentReference componentReference = definition.getReferences().get(name);
            if (componentReference != null) {
                logicalReference.addIntents(componentReference.getIntents());
                addOperationLevelIntentsAndPolicies(logicalReference, componentReference);
                for (BindingDefinition binding : componentReference.getBindings()) {
                    logicalReference.addBinding(new LogicalBinding<BindingDefinition>(binding, logicalReference));
                }
                for (BindingDefinition binding : componentReference.getCallbackBindings()) {
                    logicalReference.addCallbackBinding(new LogicalBinding<BindingDefinition>(binding, logicalReference));
                }
            }
            component.addReference(logicalReference);
        }

    }

    private void createResources(LogicalComponent<?> component, AbstractComponentType<?, ?, ?, ?> componentType) {

        for (ResourceDefinition resource : componentType.getResources().values()) {
            URI resourceUri = component.getUri().resolve('#' + resource.getName());
            LogicalResource<ResourceDefinition> logicalResource = new LogicalResource<ResourceDefinition>(resourceUri, resource, component);
            component.addResource(logicalResource);
        }

    }

}
