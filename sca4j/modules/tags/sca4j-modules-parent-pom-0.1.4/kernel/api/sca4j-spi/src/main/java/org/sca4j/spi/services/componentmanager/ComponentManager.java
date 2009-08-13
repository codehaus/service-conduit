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
package org.sca4j.spi.services.componentmanager;

import java.net.URI;
import java.util.List;

import org.sca4j.spi.component.Component;

/**
 * Responsible for tracking and managing the component tree for a runtime instance. The tree corresponds to components
 * deployed to the current runtime and hence may be sparse in comparison to the assembly component hierarchy for the SCA
 * domain as parents and children may be distributed to different runtimes.
 *
 * @version $Rev: 4790 $ $Date: 2008-06-08 16:14:42 +0100 (Sun, 08 Jun 2008) $
 */
public interface ComponentManager {

    /**
     * Registers a component which will be managed by the runtime
     *
     * @param component the component
     * @throws RegistrationException when an error ocurrs registering the component
     */
    void register(Component component) throws RegistrationException;

    /**
     * Deregisters a component
     *
     * @param component the component to deregister
     * @throws RegistrationException when an error ocurrs registering the component
     */
    void unregister(Component component) throws RegistrationException;

    /**
     * Returns the component with the given URI
     *
     * @param uri the component URI
     * @return the component or null if not found
     */
    Component getComponent(URI uri);

    /**
     * Returns a list of component URIs in the given hierarchy, e.g a domain or composite within a domain.
     *
     * @param uri a URI representing the hierarchy
     * @return the list of component URIs
     */
    List<URI> getComponentsInHierarchy(URI uri);
}
