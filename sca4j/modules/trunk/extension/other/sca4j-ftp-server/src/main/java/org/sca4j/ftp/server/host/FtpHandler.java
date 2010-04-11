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

import java.util.Map;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.ftp.api.Session;
import org.sca4j.ftp.server.monitor.FtpMonitor;
import org.sca4j.ftp.server.protocol.DefaultFtpSession;
import org.sca4j.ftp.server.protocol.DefaultRequest;
import org.sca4j.ftp.server.protocol.DefaultResponse;
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
