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
package org.sca4j.fabric.generator;

import java.util.Collection;

import org.sca4j.spi.generator.CommandMap;
import org.sca4j.spi.generator.GenerationException;
import org.sca4j.spi.model.instance.LogicalComponent;
import org.sca4j.fabric.instantiator.LogicalChange;

/**
 * Interface that abstracts the concerns of a generating commands to provision a set of componets to runtimes in a domain.
 *
 * @version $Revision$ $Date$
 */
public interface PhysicalModelGenerator {

    /**
     * Generate commands to provision a set of components and their wires to runtimes in the domain based on the given set of logical components.
     *
     * @param components the logical component set.
     * @return the command map containing the generated commands
     * @throws GenerationException If unable to generate the command map.
     */
    CommandMap generate(Collection<LogicalComponent<?>> components) throws GenerationException;


    /**
     * Generate commands to provision a set of components and their wires to runtimes in the domain based on the given set of logical components.     *
     * @param change LogicalChange that encapsulates changes to components, properties & wires
     * @return the command map containing the generated commands
     * @throws GenerationException
     */
    CommandMap generate(LogicalChange change) throws GenerationException;

}
