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

import java.util.HashMap;
import java.util.Map;

import org.osoa.sca.annotations.EagerInit;

import org.sca4j.spi.command.Command;
import org.sca4j.spi.executor.CommandExecutor;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;

/**
 * Default implementation of the CommandExecutorRegistry
 *
 * @version $Rev: 3681 $ $Date: 2008-04-19 19:00:58 +0100 (Sat, 19 Apr 2008) $
 */
@EagerInit
public class CommandExecutorRegistryImpl implements CommandExecutorRegistry {
    private Map<Class<? extends Command>, CommandExecutor<?>> executors =
            new HashMap<Class<? extends Command>, CommandExecutor<?>>();

    public <T extends Command> void register(Class<T> type, CommandExecutor<T> executor) {
        executors.put(type, executor);
    }

    @SuppressWarnings({"unchecked"})
    public <T extends Command> void execute(T command) throws ExecutionException {
        Class<? extends Command> clazz = command.getClass();
        CommandExecutor<T> executor = (CommandExecutor<T>) executors.get(clazz);
        if (executor == null) {
            throw new ExecutorNotFoundException("No registered executor for command: " + clazz.getName(), clazz.getName());
        }
        executor.execute(command);
    }
}
