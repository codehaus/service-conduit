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

import java.util.Map;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.ftp.api.Session;
import org.sca4j.ftp.server.monitor.FtpMonitor;
import org.sca4j.ftp.server.protocol.DefaultRequest;
import org.sca4j.ftp.server.protocol.DefaultResponse;
import org.sca4j.ftp.server.protocol.DefaultFtpSession;
import org.sca4j.ftp.server.protocol.Request;
import org.sca4j.ftp.server.protocol.RequestHandler;
import org.sca4j.ftp.server.protocol.Response;
import org.sca4j.ftp.server.security.User;

/**
 * TODO Use monitor instead of System.err.
 *
 * @version $Revision$ $Date$
 */
public class FtpHandler implements IoHandler {

    private Map<String, RequestHandler> requestHandlers;
    private FtpMonitor ftpMonitor;

    /**
     * Sets the monitor for logging significant events.
     *
     * @param ftpMonitor Monitor for logging significant events.
     */
    @Monitor
    public void setFtpMonitor(FtpMonitor ftpMonitor) {
        this.ftpMonitor = ftpMonitor;
    }

    /**
     * Injects the FTP commands.
     *
     * @param ftpCommands FTP commands.
     */
    @Reference
    public void setRequestHandlers(Map<String, RequestHandler> ftpCommands) {
        this.requestHandlers = ftpCommands;
    }

    public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
        Session ftpSession = new DefaultFtpSession(session);
        ftpMonitor.onException(throwable, ftpSession.getUserName());
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        DefaultFtpSession ftpSession = new DefaultFtpSession(session);

        Request request = new DefaultRequest(message.toString(), ftpSession);
        if (!"PASS".equals(request.getCommand())) {
            ftpMonitor.onCommand(message, ftpSession.getUserName());
        } else {
            ftpMonitor.onCommand("PASS ********", ftpSession.getUserName());
        }

        RequestHandler requestHandler = requestHandlers.get(request.getCommand());
        if (requestHandler == null) {
            session.write(new DefaultResponse(502, "Command " + request.getCommand() + " not implemented"));
        } else {
            requestHandler.service(request);
        }
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        Session ftpSession = new DefaultFtpSession(session);
        ftpMonitor.onResponse(message, ftpSession.getUserName());
    }

    public void sessionCreated(IoSession session) throws Exception {
        Response response = new DefaultResponse(220, "Service ready for new user.");
        session.write(response);
    }

    public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {
        // The session was idle more than the timeout period which is probably a hung client
        // Report an error and wait to close the session
        User user = (User) session.getAttribute(DefaultFtpSession.USER);
        if (user != null) {
            ftpMonitor.connectionTimedOut(user.getName());
        } else {
            ftpMonitor.connectionTimedOut("anonymous");
        }
        session.close(false).awaitUninterruptibly(10000);
    }

    public void sessionOpened(IoSession session) throws Exception {
    }

    public void sessionClosed(IoSession session) throws Exception {
    }

}
