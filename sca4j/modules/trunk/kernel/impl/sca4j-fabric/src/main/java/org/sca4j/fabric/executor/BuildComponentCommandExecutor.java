/**
 * SCA4J
 * Copyright (c) 2009 - 2099 Service Symphony Ltd
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
 *
 * Licensed to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the license
 * is included in this distrubtion or you may obtain a copy at
 *
 *    http://www.opensource.org/licenses/apache2.0.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * This project contains code licensed from the Apache Software Foundation under
 * the Apache License, Version 2.0 and original code from project contributors.
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
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
