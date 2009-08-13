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
package org.sca4j.ftp.server.handler;

import java.io.IOException;
import java.io.OutputStream;

import org.osoa.sca.annotations.Reference;

import org.sca4j.ftp.server.data.DataConnection;
import org.sca4j.ftp.server.passive.PassiveConnectionService;
import org.sca4j.ftp.server.protocol.DefaultResponse;
import org.sca4j.ftp.server.protocol.FtpSession;
import org.sca4j.ftp.server.protocol.Request;
import org.sca4j.ftp.server.protocol.RequestHandler;

/**
 * Handles the <code>LIST</code> command.
 * <p/>
 *
 * @version $Revision$ $Date$
 */
public class ListRequestHandler implements RequestHandler {
    private static final byte[] BYTES = "".getBytes();
    private PassiveConnectionService passiveConnectionService;

    /**
     * Injects the passive connection service.
     *
     * @param passiveConnectionService Passive connection service.
     */
    @Reference
    public void setPassivePortService(PassiveConnectionService passiveConnectionService) {
        this.passiveConnectionService = passiveConnectionService;
    }

    public void service(Request request) {

        FtpSession session = request.getSession();
        int passivePort = session.getPassivePort();

        if (0 == passivePort) {
            session.write(new DefaultResponse(503, "PASV must be issued first"));
            return;
        }

        session.write(new DefaultResponse(150, "File status okay; about to open data connection"));


        try {
            DataConnection dataConnection = session.getDataConnection();
            dataConnection.open();
            OutputStream stream = dataConnection.getOutputStream();
            stream.write(BYTES);
            stream.close();
            session.write(new DefaultResponse(226, "Transfer complete"));
        } catch (IOException ex) {
            session.write(new DefaultResponse(425, "Can't open data connection"));
        } finally{
            closeAndRelease(session, passivePort);
        }

    }


    /*
     * Closes the data connection and releases the passive port
     */
    private void closeAndRelease(FtpSession session, int passivePort) {
        session.closeDataConnection();
        passiveConnectionService.release(passivePort);
    }

}
