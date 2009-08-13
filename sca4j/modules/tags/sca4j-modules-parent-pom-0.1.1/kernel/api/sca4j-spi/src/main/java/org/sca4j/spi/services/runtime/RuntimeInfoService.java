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
package org.sca4j.spi.services.runtime;

import java.net.URI;

import org.sca4j.spi.model.topology.RuntimeInfo;

/**
 * Provides the abstraction to the component that provides information about the local runtime.
 * <p/>
 * Information about the runtime includes,
 * <p/>
 * <li>Runtime Id</li> <li>Components that are running in the runtime</li> <li>Features that are supported by the runtime</li>
 *
 * @version $Revsion$ $Date$
 */
public interface RuntimeInfoService {

    /**
     * Returns the current rumtime id.
     *
     * @return the current rumtime id
     */
    URI getCurrentRuntimeId();

    /**
     * Returns the information on the current runtime.
     *
     * @return Information on the current runtime.
     */
    RuntimeInfo getRuntimeInfo();

    /**
     * Register the opaque destination identifier to contact this runtime on. The identifier is registered by the participant messaging
     * infrastructure.
     *
     * @param id the opaque identifier
     */
    void registerMessageDestination(String id);
}
