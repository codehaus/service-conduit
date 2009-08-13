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

/**
 * Implementations bootstrap a runtime in two phases. The first phase initializes the runtime domain. The second phase initializes the core runtime
 * services.
 *
 * @version $Rev: 5263 $ $Date: 2008-08-25 07:50:32 +0100 (Mon, 25 Aug 2008) $
 */
public interface Bootstrapper {
    /**
     * Initializes the domain for the given runtime.
     *
     * @param runtime         the runtime to initialize the domain for
     * @param bootClassLoader the bootstrap classloader
     * @param appClassLoader  the top-level application classloader
     * @throws InitializationException if there was a problem bootstrapping the runtime
     */
    public void bootRuntimeDomain(SCA4JRuntime<?> runtime, ClassLoader bootClassLoader, ClassLoader appClassLoader)
            throws InitializationException;

    /**
     * Initialize the core system components for the supplied runtime.
     *
     * @throws InitializationException if there was a problem bootstrapping the runtime
     */
    public void bootSystem() throws InitializationException;

}
