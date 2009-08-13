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
