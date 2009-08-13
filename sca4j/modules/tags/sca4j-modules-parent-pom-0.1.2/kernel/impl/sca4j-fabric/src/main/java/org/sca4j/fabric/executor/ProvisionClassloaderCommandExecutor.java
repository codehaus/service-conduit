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

import org.sca4j.fabric.builder.classloader.ClassLoaderBuilder;
import org.sca4j.fabric.command.ProvisionClassloaderCommand;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.executor.CommandExecutor;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;

/**
 * Creates a classloader on a runtime corresponding to a PhysicalClassLoaderDefinition.
 *
 * @version $Rev: 2878 $ $Date: 2008-02-23 18:42:09 +0000 (Sat, 23 Feb 2008) $
 */
@EagerInit
public class ProvisionClassloaderCommandExecutor implements CommandExecutor<ProvisionClassloaderCommand> {

    private ClassLoaderBuilder classLoaderBuilder;
    private CommandExecutorRegistry commandExecutorRegistry;

    @Constructor
    public ProvisionClassloaderCommandExecutor(@Reference CommandExecutorRegistry commandExecutorRegistry,
                                               @Reference ClassLoaderBuilder classLoaderBuilder) {
        this.classLoaderBuilder = classLoaderBuilder;
        this.commandExecutorRegistry = commandExecutorRegistry;
    }

    public ProvisionClassloaderCommandExecutor(ClassLoaderBuilder classLoaderBuilder) {
        this.classLoaderBuilder = classLoaderBuilder;
    }

    @Init
    public void init() {
        commandExecutorRegistry.register(ProvisionClassloaderCommand.class, this);
    }

    public void execute(ProvisionClassloaderCommand command) throws ExecutionException {
        try {
            classLoaderBuilder.build(command.getClassLoaderDefinition());
        } catch (BuilderException e) {
            throw new ExecutionException(e.getMessage(), e);
        }

    }
}
