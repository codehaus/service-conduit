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
package org.sca4j.host.runtime;

import java.util.concurrent.Future;

/**
 * Implementations manage the lifecycle for a runtime type. This involves transitioning through a series of states:
 * <pre>
 * <ul>
 *      <li>BOOT PRIMORDIAL - the runtime is booted with and its domain containing system components is initialized.
 *      <li>INITIALIZE - extensions are registered and activated in the local runtime domain.
 *      <li>JOIN DOMIAN - the runtime instance discoveres and joins an application domain.
 *      <li>RECOVER - the runtime recovers and synchronizes its state with the application domain.
 *      <li>START - the runtime is prepared to receive and process requests
 *      <li>SHUTDOWN - the runtime has stopped prcessing synnchronous requests and detached from the domain.
 * </ul>
 * </pre>
 * The initialize operation is synchronous while all other operations are performed in non-blocking fashion. For non-blocking transitions, host
 * environments may choose to block on the returned Future or perform additional work prior to querying for completion.
 *
 * @version $Rev: 5264 $ $Date: 2008-08-25 09:20:09 +0100 (Mon, 25 Aug 2008) $
 */
public interface RuntimeLifecycleCoordinator<R extends SCA4JRuntime<?>, B extends Bootstrapper> {

    /**
     * Sets the bootstrap configuration. Thismust be done prior to booting the runtime.
     *
     * @param configuration the bootstrap configuration
     */
    void setConfiguration(BootConfiguration<R, B> configuration);

    /**
     * Boots the runtime with its primordial components.
     *
     * @throws InitializationException if an error occurs booting the runtime
     */
    void bootPrimordial() throws InitializationException;

    /**
     * Initializes the local runtime, including all system components
     *
     * @throws InitializationException if an error occurs initializing the runtime
     */
    void initialize() throws InitializationException;

    /**
     * Join the domain in a non-blocking fashion.
     *
     * @param timeout the timeout in milliseconds or -1 if the operation should wait indefinitely
     * @return a future that can be polled for completion of the operation
     * @throws InitializationException if an error occurs joining the domain
     */
    Future<Void> joinDomain(long timeout) throws InitializationException;

    /**
     * Perform the recovery operation. On controller nodes, this may result in reprovisioning components and resources.
     *
     * @return a future that can be polled for completion of the operation
     */
    Future<Void> recover();

    /**
     * Start the runtime receiving requests.
     *
     * @return a future that can be polled for completion of the operation
     * @throws StartException if an error starting the runtime occurs
     */
    Future<Void> start() throws StartException;

    /**
     * Shuts the runtime down, stopping it from receiving requests and detaching it from the domain. In-flight synchronous operations will be allowed
     * to proceed to completion.
     *
     * @return a future that can be polled for completion of the operation
     * @throws ShutdownException if an error ocurrs shutting down the runtime
     */
    Future<Void> shutdown() throws ShutdownException;
}


