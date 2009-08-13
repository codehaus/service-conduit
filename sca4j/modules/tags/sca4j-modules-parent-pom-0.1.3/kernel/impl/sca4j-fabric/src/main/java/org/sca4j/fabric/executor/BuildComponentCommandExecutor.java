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

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Reference;

import org.sca4j.fabric.command.BuildComponentCommand;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.ComponentBuilderRegistry;
import org.sca4j.spi.component.Component;
import org.sca4j.spi.executor.CommandExecutor;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;
import org.sca4j.spi.model.physical.PhysicalComponentDefinition;
import org.sca4j.spi.services.componentmanager.ComponentManager;
import org.sca4j.spi.services.componentmanager.RegistrationException;

/**
 * Eagerly initializes a component on a service node.
 *
 * @version $Rev: 2878 $ $Date: 2008-02-23 18:42:09 +0000 (Sat, 23 Feb 2008) $
 */
@EagerInit
public class BuildComponentCommandExecutor implements CommandExecutor<BuildComponentCommand> {

    private final ComponentBuilderRegistry componentBuilderRegistry;
    private final ComponentManager componentManager;
    private CommandExecutorRegistry commandExecutorRegistry;

    @Constructor
    public BuildComponentCommandExecutor(@Reference ComponentBuilderRegistry componentBuilderRegistry,
                                         @Reference ComponentManager componentManager,
                                         @Reference CommandExecutorRegistry commandExecutorRegistry) {
        this.componentBuilderRegistry = componentBuilderRegistry;
        this.componentManager = componentManager;
        this.commandExecutorRegistry = commandExecutorRegistry;
    }

    public BuildComponentCommandExecutor(ComponentBuilderRegistry componentBuilderRegistry, ComponentManager componentManager) {
        this.componentBuilderRegistry = componentBuilderRegistry;
        this.componentManager = componentManager;
    }

    @Init
    public void init() {
        commandExecutorRegistry.register(BuildComponentCommand.class, this);
    }

    public void execute(BuildComponentCommand command) throws ExecutionException {

        try {
            PhysicalComponentDefinition physicalComponentDefinition = command.getDefinition();
            Component component = componentBuilderRegistry.build(physicalComponentDefinition);
            componentManager.register(component);

        } catch (BuilderException e) {
            throw new ExecutionException(e.getMessage(), e);
        } catch (RegistrationException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
    }
}
