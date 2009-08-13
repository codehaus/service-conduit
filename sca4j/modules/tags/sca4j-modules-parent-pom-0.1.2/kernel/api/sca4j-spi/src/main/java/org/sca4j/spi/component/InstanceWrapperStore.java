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
