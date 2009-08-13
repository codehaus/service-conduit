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
package org.sca4j.spi.generator;

import org.sca4j.spi.command.Command;
import org.sca4j.spi.model.instance.LogicalComponent;

/**
 * Generates a Command that must be applied to a runtime based on changes to a logical component.
 *
 * @version $Rev: 3678 $ $Date: 2008-04-19 18:05:57 +0100 (Sat, 19 Apr 2008) $
 */
public interface CommandGenerator {

    /**
     * Gets the order the command generator should be called in.
     *
     * @return an ascending  value where 0 is first
     */
    int getOrder();

    /**
     * Generates a command based on the contents of a logical component
     *
     * @param logicalComponent the logical component to generate the command from
     * @return the generated command or null if no changes were detected
     * @throws GenerationException if an error occurs during generation
     */
    Command generate(LogicalComponent<?> logicalComponent) throws GenerationException;

}
