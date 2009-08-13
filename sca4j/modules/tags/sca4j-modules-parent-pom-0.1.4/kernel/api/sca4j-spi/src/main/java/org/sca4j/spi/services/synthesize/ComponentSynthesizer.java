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

package org.sca4j.spi.services.synthesize;

/**
 * Synthesizes and registers a component from an existing object instance in the runtime domain.
 *
 * @version $Revision$ $Date$
 */
public interface ComponentSynthesizer {

    /**
     * Synthesizes and registers a component from an existing object instance.
     *
     * @param name       the component name
     * @param type       the service contract type
     * @param instance   the implementation instance
     * @param introspect true if the SCA componentType should be introspected from the instance
     * @throws ComponentRegistrationException if an error occurs synthesizing the component
     */
    public <S, I extends S> void registerComponent(String name, Class<S> type, I instance, boolean introspect) throws ComponentRegistrationException;


}
