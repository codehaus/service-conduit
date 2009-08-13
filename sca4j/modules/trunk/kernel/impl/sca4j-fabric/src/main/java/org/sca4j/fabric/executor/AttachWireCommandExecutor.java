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

import org.sca4j.fabric.builder.Connector;
import org.sca4j.fabric.command.AttachWireCommand;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.executor.CommandExecutor;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;
import org.sca4j.spi.model.physical.PhysicalWireDefinition;

/**
 * Eagerly initializes a component on a service node.
 *
 * @version $Rev: 2878 $ $Date: 2008-02-23 18:42:09 +0000 (Sat, 23 Feb 2008) $
 */
@EagerInit
public class AttachWireCommandExecutor implements CommandExecutor<AttachWireCommand> {

    private CommandExecutorRegistry commandExecutorRegistry;
    private final Connector connector;

    @Constructor
    public AttachWireCommandExecutor(@Reference CommandExecutorRegistry commandExecutorRegistry,
                                     @Reference Connector connector) {
        this.commandExecutorRegistry = commandExecutorRegistry;
        this.connector = connector;
    }

    public AttachWireCommandExecutor(Connector connector) {
        this.connector = connector;
    }

    @Init
    public void init() {
        commandExecutorRegistry.register(AttachWireCommand.class, this);
    }

    public void execute(AttachWireCommand command) throws ExecutionException {

        for (PhysicalWireDefinition physicalWireDefinition : command.getPhysicalWireDefinitions()) {
            try {
                connector.connect(physicalWireDefinition);
            } catch (BuilderException e) {
                throw new ExecutionException(e.getMessage(), e);
            }
        }

    }
}
