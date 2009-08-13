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

import java.util.Set;

import org.osoa.sca.annotations.Reference;

import org.sca4j.scdl.Scope;
import org.sca4j.spi.command.Command;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;
import org.sca4j.spi.generator.CommandMap;

/**
 * A routing service implementation that routes to the local runtime instance. For example, this service is used to route changesets for runtime
 * extensions.
 *
 * @version $Rev: 5258 $ $Date: 2008-08-24 00:04:47 +0100 (Sun, 24 Aug 2008) $
 */
public class RuntimeRoutingService implements RoutingService {

    private CommandExecutorRegistry registry;
    private ScopeRegistry scopeRegistry;

    public RuntimeRoutingService(@Reference CommandExecutorRegistry registry, @Reference ScopeRegistry scopeRegistry) {
        this.registry = registry;
        this.scopeRegistry = scopeRegistry;
    }

    public void route(CommandMap commandMap) throws RoutingException {

        Set<Command> commands = commandMap.getCommandsForRuntime(null);
        for (Command command : commands) {
            try {
                registry.execute(command);
            } catch (ExecutionException e) {
                throw new RoutingException(e);
            }
        }

        try {
            if (scopeRegistry != null) {
                scopeRegistry.getScopeContainer(Scope.COMPOSITE).reinject();
            }
        } catch (InstanceLifecycleException e) {
            throw new RoutingException(e);
        }

    }

}
