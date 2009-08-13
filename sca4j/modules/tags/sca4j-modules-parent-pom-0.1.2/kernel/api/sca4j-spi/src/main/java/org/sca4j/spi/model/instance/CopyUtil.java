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
package org.sca4j.spi.model.instance;

import java.net.URI;
import java.util.Map;

import org.w3c.dom.Document;

/**
 * Utilities for copying a logical model graph.
 *
 * @version $Revision$ $Date$
 */
public class CopyUtil {
    private CopyUtil() {
    }

    /**
     * Copies the instance graph, making a complete replica, including preservation of parent-child relationships.
     *
     * @param composite the composite to copy
     * @return the copy
     */
    public static LogicalCompositeComponent copy(LogicalCompositeComponent composite) {
        return copy(composite, composite.getParent());
    }


    /**
     * Recursively performs the actual copy.
     *
     * @param composite the composite to copy
     * @param parent    the parent of the copy
     * @return the copy
     */
    private static LogicalCompositeComponent copy(LogicalCompositeComponent composite, LogicalCompositeComponent parent) {
        LogicalCompositeComponent copy =
                new LogicalCompositeComponent(composite.getUri(), composite.getRuntimeId(), composite.getDefinition(), parent);
        copy.setActive(composite.isActive());
        copy.setAutowireOverride(composite.getAutowireOverride());
        copy.setClassLoaderId(composite.getClassLoaderId());
        copy.setProvisioned(composite.isProvisioned());
        copy.setRuntimeId(composite.getRuntimeId());
        for (Map.Entry<String, Document> entry : composite.getPropertyValues().entrySet()) {
            copy.setPropertyValue(entry.getKey(), entry.getValue());
        }
        for (LogicalComponent<?> component : composite.getComponents()) {
            copy(component, copy);
        }
        for (LogicalReference reference : composite.getReferences()) {
            copy(reference, copy);
        }
        for (LogicalResource<?> resource : composite.getResources()) {
            copy(resource, copy);
        }
        for (LogicalService service : composite.getServices()) {
            copy(service, copy);
        }
        for (LogicalReference reference : copy.getReferences()) {
            copyWires(composite, reference, copy);
        }
        return copy;
    }

    @SuppressWarnings({"unchecked"})
    private static void copy(LogicalComponent<?> component, LogicalCompositeComponent parent) {
        LogicalComponent<?> copy;
        if (component instanceof LogicalCompositeComponent) {
            copy = copy((LogicalCompositeComponent) component, parent);
        } else {
            copy = new LogicalComponent(component.getUri(), component.getRuntimeId(), component.getDefinition(), parent);
        }
        parent.addComponent(copy);
    }

    private static void copy(LogicalReference reference, LogicalCompositeComponent parent) {
        LogicalReference copy = new LogicalReference(reference.getUri(), reference.getDefinition(), parent);
        for (URI uri : reference.getPromotedUris()) {
            copy.addPromotedUri(uri);
        }
        copy(reference, copy);
        parent.addReference(copy);
    }

    @SuppressWarnings({"unchecked"})
    private static void copy(LogicalResource<?> resource, LogicalCompositeComponent parent) {
        LogicalResource copy = new LogicalResource(resource.getUri(), resource.getResourceDefinition(), parent);
        copy.setTarget(resource.getTarget());
        parent.addResource(copy);
    }


    private static void copy(LogicalService service, LogicalCompositeComponent parent) {
        LogicalService copy = new LogicalService(service.getUri(), service.getDefinition(), parent);
        copy.setPromotedUri(service.getPromotedUri());
        copy(service, copy);
        parent.addService(copy);
    }

    @SuppressWarnings({"unchecked"})
    private static void copy(Bindable from, Bindable to) {
        for (LogicalBinding<?> binding : from.getBindings()) {
            LogicalBinding<?> copy = new LogicalBinding(binding.getBinding(), to);
            copy.setProvisioned(binding.isProvisioned());
            to.addBinding(copy);
        }
        for (LogicalBinding<?> binding : from.getCallbackBindings()) {
            LogicalBinding<?> copy = new LogicalBinding(binding.getBinding(), to);
            copy.setProvisioned(binding.isProvisioned());
            to.addCallbackBinding(copy);
        }
    }

    private static void copyWires(LogicalCompositeComponent composite, LogicalReference reference, LogicalCompositeComponent parent) {
        for (LogicalWire wire : composite.getWires(reference)) {
            LogicalWire wireCopy = new LogicalWire(parent, reference, wire.getTargetUri());
            parent.addWire(reference, wireCopy);
        }
    }

}
