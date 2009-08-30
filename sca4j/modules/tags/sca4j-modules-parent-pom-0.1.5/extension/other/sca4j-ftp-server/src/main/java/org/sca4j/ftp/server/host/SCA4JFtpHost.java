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
package org.sca4j.ftp.server.host;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.host.work.WorkScheduler;

/**
 * F3 implementation of the in-process FTP host.
 *
 * @version $Revision$ $Date$
 */
@EagerInit
@Scope("COMPOSITE")
public class SCA4JFtpHost implements FtpHost {
    
    private FtpHostMonitor monitor;
    private WorkScheduler workScheduler;
    private int commandPort = 21;
    private SocketAcceptor acceptor;
    private IoHandler ftpHandler;
    private ProtocolCodecFactory codecFactory;
    private String listenAddress;
    private int idleTimeout = 60; // 60 seconds default

    /**
     * Starts the FTP server.
     *
     * @throws IOException If unable to start the FTP server.
     */
    @Init
    public void start() throws IOException {
        ExecutorService filterExecutor = new SCA4JExecutorService(workScheduler);
        InetSocketAddress socketAddress;
        if (listenAddress == null) {
            socketAddress = new InetSocketAddress(InetAddress.getLocalHost(), commandPort);
        } else {
            socketAddress = new InetSocketAddress(listenAddress, commandPort);
        }
        acceptor = new NioSocketAcceptor();
        SocketSessionConfig config = acceptor.getSessionConfig();
        config.setIdleTime(IdleStatus.READER_IDLE, idleTimeout);
        acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(filterExecutor));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
        acceptor.setHandler(ftpHandler);
        monitor.extensionStarted();
        acceptor.setCloseOnDeactivation(true);
        acceptor.bind(socketAddress);
        monitor.startFtpListener(commandPort);
    }

    /**
     * Stops the FTP server.
     * @throws InterruptedException 
     */
    @Destroy
    public void stop() throws InterruptedException {
        for (IoSession session : acceptor.getManagedSessions().values()) {
            session.close(false).await();
        }
        acceptor.unbind();
        acceptor.dispose();
        monitor.extensionStopped();
    }

    /**
     * Sets the monitor.
     *
     * @param monitor the monitor.
     */
    @Monitor
    public void setMonitor(FtpHostMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Sets the handler for the FTP commands.
     *
     * @param ftpHandler FTP Handler.
     */
    @Reference
    public void setFtpHandler(IoHandler ftpHandler) {
        this.ftpHandler = ftpHandler;
    }

    /**
     * Sets the protocol codec factory.
     *
     * @param codecFactory Protocol codec.
     */
    @Reference
    public void setCodecFactory(ProtocolCodecFactory codecFactory) {
        this.codecFactory = codecFactory;
    }

    /**
     * Sets the work scheduler for task execution.
     *
     * @param workScheduler the scheduler
     */
    @Reference
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    /**
     * Sets the FTP command port.
     *
     * @param commandPort Command port.
     */
    @Property
    public void setCommandPort(int commandPort) {
        this.commandPort = commandPort;
    }

    /**
     * Sets the optional timeout in seconds for sockets that are idle.
     *
     * @param timeout timeout in seconds.
     */
    @Property
    public void setIdleTimeout(int timeout) {
        this.idleTimeout = timeout;
    }

    /**
     * Sets the address the server should bind to. This is used for multi-homed machines.
     *
     * @param listenAddress the address to bind to
     */
    @Property
    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }
}
