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
package org.sca4j.fabric.executor;

import java.net.URI;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.command.StartCompositeContextCommand;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.executor.CommandExecutor;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;

/**
 * Executes a {@link org.sca4j.fabric.command.StartCompositeContextCommand}.
 *
 * @version $Rev: 3681 $ $Date: 2008-04-19 19:00:58 +0100 (Sat, 19 Apr 2008) $
 */
@EagerInit
public class StartCompositeContextCommandExecutor implements CommandExecutor<StartCompositeContextCommand> {
    private ScopeContainer<URI> container;
    private CommandExecutorRegistry commandExecutorRegistry;

    @Constructor
    public StartCompositeContextCommandExecutor(@Reference CommandExecutorRegistry commandExecutorRegistry,
                                                @Reference ScopeRegistry scopeRegistry) {
        this.commandExecutorRegistry = commandExecutorRegistry;
        this.container = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
    }

    public StartCompositeContextCommandExecutor(ScopeRegistry scopeRegistry) {
        this.container = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
    }

    @Init
    public void init() {
        commandExecutorRegistry.register(StartCompositeContextCommand.class, this);
    }

    public void execute(StartCompositeContextCommand command) throws ExecutionException {
        URI groupId = command.getGroupId();
        WorkContext workContext = new WorkContext();
        CallFrame frame = new CallFrame(groupId);
        workContext.addCallFrame(frame);
        try {
            container.startContext(workContext);
        } catch (GroupInitializationException e) {
            throw new ExecutionException("Error executing command", e);
        }
    }

}
