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
package org.sca4j.fabric.services.componentmanager;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sca4j.spi.component.Component;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.componentmanager.RegistrationException;

/**
 * Default implementation of the component manager
 *
 * @version $Rev: 4790 $ $Date: 2008-06-08 16:14:42 +0100 (Sun, 08 Jun 2008) $
 */
public class ComponentManagerImpl implements ComponentManager {
    private final Map<URI, Component> components;

    public ComponentManagerImpl() {
        components = new ConcurrentHashMap<URI, Component>();
    }

    public synchronized void register(Component component) throws RegistrationException {
        URI uri = component.getUri();

        assert uri != null;
        assert !uri.toString().endsWith("/");
        if (components.containsKey(uri)) {
            throw new DuplicateComponentException("A component is already registered for: " + uri.toString());
        }
        components.put(uri, component);
    }

    public synchronized void unregister(Component component) throws RegistrationException {
        URI uri = component.getUri();
        components.remove(uri);
    }

    public Component getComponent(URI name) {
        return components.get(name);
    }

    public List<URI> getComponentsInHierarchy(URI uri) {
        String strigified = uri.toString();
        List<URI> uris = new ArrayList<URI>();
        for (Component component : components.values()) {
            URI componentUri = component.getUri();
            if (componentUri.toString().startsWith(strigified)) {
                uris.add(componentUri);
            }
        }
        return uris;
    }
}
