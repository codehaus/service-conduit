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
