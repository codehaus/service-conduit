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
package org.sca4j.fabric.generator.classloader;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sca4j.spi.command.Command;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * Generates classloader definitions for a set of logical components that are to be provisioned to a runtime.
 * <p/>
 * On the runtime, a builder is responsible for matching the PhysicalClassLoaderDefinition to an existing classloader or creating a new one. During
 * this process, the contribution archive and required extensions may need to be provisioned to the runtime.
 * <p/>
 *
 * @version $Revision$ $Date$
 */
public interface ClassLoaderCommandGenerator {

    /**
     * Generates the classloader definitons for a set of logical components.
     *
     * @param components the logical components
     * @return the classloader provisioning commands grouped by runtime id where they are to be provisioned
     * @throws GenerationException if an error occurs during generation
     */
    Map<URI, Set<Command>> generate(List<LogicalComponent<?>> components) throws GenerationException;

}
