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
 * Original Codehaus Header
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
 * Original Apache Header
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

import org.oasisopen.sca.annotation.Constructor;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Reference;
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
