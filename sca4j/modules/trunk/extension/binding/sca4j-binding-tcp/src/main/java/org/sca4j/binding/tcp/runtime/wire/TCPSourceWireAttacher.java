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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.sca4j.binding.tcp.runtime.wire;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.tcp.provision.TCPWireSourceDefinition;
import org.sca4j.binding.tcp.runtime.concurrent.SCA4JExecutorService;
import org.sca4j.binding.tcp.runtime.handler.TCPHandler;
import org.sca4j.binding.tcp.runtime.monitor.TCPBindingMonitor;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Revision$ $Date$
 */
public class TCPSourceWireAttacher implements SourceWireAttacher<TCPWireSourceDefinition> {

    private final WorkScheduler workScheduler;

    private final TCPBindingMonitor monitor;

    private SocketAcceptor acceptor;

    /**
     * Inject dependencies
     * 
     * @param workScheduler
     *            F3 Work Scheduler
     */
    public TCPSourceWireAttacher(@Reference WorkScheduler workScheduler, 
                                 @Monitor TCPBindingMonitor monitor) {
        this.workScheduler = workScheduler;
        this.monitor = monitor;
    }

    /**
     * {@inheritDoc}
     */
    public void attachToSource(TCPWireSourceDefinition source, PhysicalWireTargetDefinition target, final Wire wire) throws WiringException {
        URI uri = source.getUri();
        String hostname = uri.getHost();
        int port = uri.getPort();

        ExecutorService filterExecutor = new SCA4JExecutorService(workScheduler);
        InetSocketAddress socketAddress;
        try {
            // TODO: Move below to separate component configurable with Codec
            // and IoHandler based on protocol like TCP/HTTP etc.
            socketAddress = new InetSocketAddress(hostname, port);
            acceptor = new NioSocketAcceptor();
            acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(filterExecutor));
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new CodecFactory()));

            acceptor.setHandler(new TCPHandler(wire, monitor));
            acceptor.bind(socketAddress);

            monitor.onTcpExtensionStarted(uri.toString());

        } catch (UnknownHostException exception) {
            throw new WiringException("Unable to bind to:" + uri.toString(), "binding.tcp", exception);
        } catch (IOException exception) {
            throw new WiringException("Unable to bind to:" + uri.toString(), "binding.tcp", exception);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void detachFromSource(TCPWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void attachObjectFactory(TCPWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition definition) throws WiringException {
        throw new UnsupportedOperationException();
    }

    /**
     * Stops the processing.
     */
    @Destroy
    public void destroy() {
        acceptor.unbind();
        acceptor.dispose();
    }
}
