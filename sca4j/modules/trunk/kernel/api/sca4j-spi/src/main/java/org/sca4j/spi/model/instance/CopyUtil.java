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
