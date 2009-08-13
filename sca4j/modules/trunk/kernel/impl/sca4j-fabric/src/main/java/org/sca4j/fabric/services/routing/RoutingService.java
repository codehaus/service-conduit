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
package org.sca4j.fabric.services.routing;

import org.sca4j.spi.generator.CommandMap;

/**
 * Implementations route physical commands to a runtime node.
 *
 * @version $Rev: 3644 $ $Date: 2008-04-15 15:07:31 +0100 (Tue, 15 Apr 2008) $
 */
public interface RoutingService {

    /**
     * Routes a command set to a runtime node
     *
     * @param commandMap the command map to route
     * @throws RoutingException if an exception occurs during routing
     */
    void route(CommandMap commandMap) throws RoutingException;

}
