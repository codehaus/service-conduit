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
package org.sca4j.spi.component;

/**
 * Interface implemented by services that are able to store InstanceWrappers between invocations.
 * Instances are grouped together into collections identified by the context id. Each collection may contain
 * instances from several components.
 *
 * @version $Rev: 1 $ $Date: 2007-05-14 18:40:37 +0100 (Mon, 14 May 2007) $
 * @param <KEY> the type of key this store uses to identify contexts
 */
public interface InstanceWrapperStore<KEY> {
    /**
     * Notification to the store that a scope context is being started.
     * This must be called before any instances are associated with the context
     *
     * @param contextId the id of the context
     * @throws StoreException if there was a problem initializing the context
     */
    void startContext(KEY contextId) throws StoreException;

    /**
     * Notification to the store that a scope context is ending.
     * 
     * @param contextId the id of the context
     * @throws StoreException if there was a problem shutting the context down
     */
    void stopContext(KEY contextId) throws StoreException;

    /**
     * Get the instance of the supplied component that is associated with the supplied context.
     * Returns null if there is no instance currently associated.
     *
     * @param component the component whose instance should be returned
     * @param contextId the context whose instance should be returned
     * @return the wrapped instance associated with the context or null
     * @throws StoreException if there was problem returning the instance
     */
    <T> InstanceWrapper<T> getWrapper(AtomicComponent<T> component, KEY contextId) throws StoreException;

    /**
     * Associated an instance of the supplied component with the supplied context.
     *
     * @param component the component whose instance is being stored
     * @param contextId the context with which the instance is associated
     * @param wrapper the wrapped instance
     * @throws StoreException if there was a problem storing the instance
     */
    <T> void putWrapper(AtomicComponent<T> component, KEY contextId, InstanceWrapper<T> wrapper) throws StoreException;
}
