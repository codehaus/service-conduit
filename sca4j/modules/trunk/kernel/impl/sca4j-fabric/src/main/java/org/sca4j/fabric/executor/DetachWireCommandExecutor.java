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
import org.sca4j.fabric.command.DetachWireCommand;
import org.sca4j.spi.executor.CommandExecutor;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;
import org.sca4j.spi.model.physical.PhysicalWireDefinition;
import org.sca4j.spi.builder.BuilderException;

/*
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
@EagerInit
public class DetachWireCommandExecutor implements CommandExecutor<DetachWireCommand> {


    private CommandExecutorRegistry commandExecutorRegistry;
    private final Connector connector;

    @Constructor
    public DetachWireCommandExecutor(@Reference CommandExecutorRegistry registry,
                                     @Reference Connector connector) {
        this.commandExecutorRegistry = registry;
        this.connector = connector;
    }

    @Init
    public void init() {
        commandExecutorRegistry.register(DetachWireCommand.class, this);
    }

    public void execute(DetachWireCommand command) throws ExecutionException {
        for (PhysicalWireDefinition physicalWireDefinition : command.getPhysicalWireDefinitions()) {
          try {
              connector.disconnect(physicalWireDefinition);
          } catch (BuilderException be) {
            throw new AssertionError(be);
          }
        }

    }
}
