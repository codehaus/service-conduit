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
package org.sca4j.fabric.services.routing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Set;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.osoa.sca.annotations.Reference;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.host.perf.PerformanceMonitor;
import org.sca4j.scdl.Scope;
import org.sca4j.services.xmlfactory.XMLFactory;
import org.sca4j.services.xmlfactory.XMLFactoryInstantiationException;
import org.sca4j.spi.command.Command;
import org.sca4j.spi.component.InstanceLifecycleException;
import org.sca4j.spi.component.ScopeRegistry;
import org.sca4j.spi.executor.CommandExecutorRegistry;
import org.sca4j.spi.executor.ExecutionException;
import org.sca4j.spi.generator.CommandMap;
import org.sca4j.spi.services.marshaller.MarshalException;
import org.sca4j.spi.services.marshaller.MarshalService;
import org.sca4j.spi.services.messaging.MessagingException;
import org.sca4j.spi.services.messaging.MessagingService;

/**
 * A routing service implementation that routes physical changesets across a domain
 *
 * @version $Rev: 5284 $ $Date: 2008-08-26 17:54:14 +0100 (Tue, 26 Aug 2008) $
 */
public class FederatedRoutingService implements RoutingService {

    private MarshalService marshalService;
    private final MessagingService messagingService;
    private final CommandExecutorRegistry executorRegistry;
    private final XMLFactory xmlFactory;
    private final RoutingMonitor monitor;
    private final ScopeRegistry scopeRegistry;

    public FederatedRoutingService(@Reference MessagingService messagingService,
                                   @Reference CommandExecutorRegistry executorRegistry,
                                   @Reference XMLFactory xmlFactory,
                                   @Monitor RoutingMonitor monitor,
                                   @Reference ScopeRegistry scopeRegistry) {

        this.messagingService = messagingService;
        this.executorRegistry = executorRegistry;
        this.xmlFactory = xmlFactory;
        this.monitor = monitor;
        this.scopeRegistry = scopeRegistry;
    }

    /**
     * Used to lazily inject the MarhsalService since it may be provided by a runtime extension loaded after this component.
     *
     * @param marshalService the MarshalService to inject
     */
    @Reference(required = false)
    public void setMarshalService(MarshalService marshalService) {
        this.marshalService = marshalService;
    }

    public void route(CommandMap commandMap) throws RoutingException {

        for (URI runtimeId : commandMap.getRuntimeIds()) {

            Set<Command> commands = commandMap.getCommandsForRuntime(runtimeId);
            if (runtimeId != null) {
                monitor.routeCommands(runtimeId.toString());
                routeToDestination(runtimeId, commands);
            } else {
                routeLocally(commands);
            }

        }

    }

    private void routeLocally(Set<Command> commands) throws RoutingException {

        for (Command command : commands) {
            try {

                executorRegistry.execute(command);

            } catch (ExecutionException e) {
                throw new RoutingException(e);
            }
        }

        try {
            scopeRegistry.getScopeContainer(Scope.COMPOSITE).reinject();
        } catch (InstanceLifecycleException e) {
            throw new RoutingException(e);
        }

    }

    private void routeToDestination(URI runtimeId, Object commandSet) throws RoutingException {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLOutputFactory factory = xmlFactory.newOutputFactoryInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(out);
            getMarshalService().marshall(commandSet, writer);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            XMLStreamReader pcsReader = xmlFactory.newInputFactoryInstance().createXMLStreamReader(in);
            messagingService.sendMessage(runtimeId, pcsReader);
        } catch (XMLFactoryInstantiationException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            throw new RoutingException("Routing error", e);
        } catch (MarshalException e) {
            throw new RoutingException("Routing error", e);
        } catch (MessagingException e) {
            throw new RoutingException("Routing error", e);
        }

    }

    private MarshalService getMarshalService() {
        if (marshalService == null) {
            throw new IllegalStateException("MarshalService not configured");
        }
        return marshalService;
    }

}
