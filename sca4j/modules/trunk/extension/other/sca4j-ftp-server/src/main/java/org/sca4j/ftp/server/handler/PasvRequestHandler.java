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
package org.sca4j.ftp.server.handler;

import java.io.IOException;
import java.net.InetAddress;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.sca4j.ftp.server.data.DataConnection;
import org.sca4j.ftp.server.data.PassiveDataConnection;
import org.sca4j.ftp.server.passive.PassiveConnectionService;
import org.sca4j.ftp.server.protocol.DefaultFtpSession;
import org.sca4j.ftp.server.protocol.DefaultResponse;
import org.sca4j.ftp.server.protocol.Request;
import org.sca4j.ftp.server.protocol.RequestHandler;

/**
 * Handles the <code>PASV</code> command.
 *
 * @version $Revision$ $Date$
 */
public class PasvRequestHandler implements RequestHandler {

    private PassiveConnectionService passiveConnectionService;
    private String listenAddress;
    private int idleTimeout = 60000;  // 60 seconds default

    /**
     * Initializes the passive data connection on request of <code>PASV</code> command from an authenticated user.
     *
     * @param request Object the encapsuates the current FTP command.
     */
    public void service(Request request) {

        DefaultFtpSession session = request.getSession();

        if (!session.isAuthenticated()) {
            session.write(new DefaultResponse(530, "Access denied"));
            return;
        }

        int passivePort = 0;

        try {

            passivePort = passiveConnectionService.acquire();

            InetAddress localAddress;
            if (listenAddress == null) {
                localAddress = InetAddress.getLocalHost();
            } else {
                localAddress = InetAddress.getByName(listenAddress);
            }

            String socketAddress = localAddress.getHostAddress().replace('.', ',') + ',' + (passivePort >> 8) + ',' + (passivePort & 0xFF);
            session.setPassivePort(passivePort);

            DataConnection dataConnection = new PassiveDataConnection(localAddress, passivePort, idleTimeout);
            dataConnection.initialize();
            session.setDataConnection(dataConnection);

            session.write(new DefaultResponse(227, "Entering Passive Mode (" + socketAddress + ")"));

        } catch (InterruptedException e) {
            session.write(new DefaultResponse(427, "Can't open passive connection"));
        } catch (IOException e) {
            passiveConnectionService.release(passivePort);
            session.write(new DefaultResponse(427, "Can't open passive connection"));
        }

    }

    @Property
    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    /**
     * Sets the optional timeout in seconds for sockets that are idle.
     *
     * @param timeout timeout in seconds.
     */
    @Property
    public void setIdleTimeout(int timeout) {
        this.idleTimeout = timeout * 1000;
    }


    /**
     * Injects the passive connection service.
     *
     * @param passiveConnectionService Passive connection service.
     */
    @Reference
    public void setPassivePortService(PassiveConnectionService passiveConnectionService) {
        this.passiveConnectionService = passiveConnectionService;
    }

}
