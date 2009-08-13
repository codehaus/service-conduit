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
import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.command.ComponentInitializationUri;
import org.sca4j.fabric.command.InitializeComponentCommand;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.component.AtomicComponent;
import org.sca4j.spi.component.Component;
import org.sca4j.spi.component.GroupInitializationException;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.executor.CommandExecutor;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.services.componentmanager.ComponentManager;

/**
 * Eagerly initializes a component on a service node.
 *
 * @version $Rev: 4790 $ $Date: 2008-06-08 16:14:42 +0100 (Sun, 08 Jun 2008) $
 */
@EagerInit
public class InitializeComponentCommandExecutor implements CommandExecutor<InitializeComponentCommand> {
    private CommandExecutorRegistry commandExecutorRegistry;
    private ComponentManager manager;
    private ScopeContainer<?> scopeContainer;

    public InitializeComponentCommandExecutor(ScopeRegistry scopeRegistry, ComponentManager manager) {
        this(null, scopeRegistry, manager);
    }

    @Constructor
    public InitializeComponentCommandExecutor(@Reference CommandExecutorRegistry commandExecutorRegistry,
                                              @Reference ScopeRegistry scopeRegistry,
                                              @Reference ComponentManager manager) {
        this.commandExecutorRegistry = commandExecutorRegistry;
        this.manager = manager;
        this.scopeContainer = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
    }

    @Init
    public void init() {
        commandExecutorRegistry.register(InitializeComponentCommand.class, this);
    }

    public void execute(InitializeComponentCommand command) throws ExecutionException {

        ComponentInitializationUri componentInitializationUri = command.getUri();
        URI groupId = componentInitializationUri.getGroupId();
        URI uri = componentInitializationUri.getUri();
        Component component = manager.getComponent(uri);
        if (!(component instanceof AtomicComponent)) {
            throw new ComponentNotRegisteredException("Component not registered: " + uri.toString(), uri.toString());
        }
        WorkContext workContext = new WorkContext();
        CallFrame frame = new CallFrame(groupId);
        workContext.addCallFrame(frame);
        List<AtomicComponent<?>> atomicComponents = new ArrayList<AtomicComponent<?>>();
        atomicComponents.add((AtomicComponent<?>) component);
        try {
            scopeContainer.initializeComponents(atomicComponents, workContext);
        } catch (GroupInitializationException e) {
            throw new ExecutionException("Error starting components", e);
        }


    }
}
