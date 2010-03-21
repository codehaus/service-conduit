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
