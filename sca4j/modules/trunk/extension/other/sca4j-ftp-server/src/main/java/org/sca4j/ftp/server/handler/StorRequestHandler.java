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
import java.io.InputStream;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.ftp.api.FtpLet;
import org.sca4j.ftp.server.data.DataConnection;
import org.sca4j.ftp.server.monitor.FtpMonitor;
import org.sca4j.ftp.server.passive.PassiveConnectionService;
import org.sca4j.ftp.server.protocol.DefaultFtpSession;
import org.sca4j.ftp.server.protocol.DefaultResponse;
import org.sca4j.ftp.server.protocol.Request;
import org.sca4j.ftp.server.protocol.RequestHandler;
import org.sca4j.ftp.spi.FtpLetContainer;

/**
 * Handles the <code>STOR</code> command.
 * <p/>
 * TODO Add mechanism to register the FTPlet.
 *
 * @version $Revision$ $Date$
 */
public class StorRequestHandler implements RequestHandler {

    private PassiveConnectionService passiveConnectionService;
    private FtpLetContainer ftpLetContainer;
    private FtpMonitor ftpMonitor;
    private int idleTimeout = 60000;

    /**
     * Services the <code>STOR</code> request. Currently only supports passive connections. This means <code>STOR</STOR> command should be preceded by
     * a <code>PASV</code> command.
     *
     * @param request Object the encapsuates the current FTP command.
     */
    public void service(Request request) {

        DefaultFtpSession session = request.getSession();
        if (!session.isAuthenticated()) {
            session.write(new DefaultResponse(530, "Access Denied"));
            return;
        }
        int passivePort = session.getPassivePort();

        if (0 == passivePort) {
            session.write(new DefaultResponse(503, "PASV must be issued first"));
            return;
        }

        String fileName = request.getArgument();
        if (null == fileName) {
            closeDataConnection(session, passivePort);
            session.write(new DefaultResponse(501, "Syntax error in parameters or arguments"));
            return;
        }

        session.write(new DefaultResponse(150, "File status okay; about to open data connection"));

        DataConnection dataConnection = session.getDataConnection();

        try {
            dataConnection.open();
        } catch (IOException ex) {
            closeDataConnection(session, passivePort);
            session.write(new DefaultResponse(425, "Can't open data connection"));
            return;
        }

        transfer(session, passivePort, dataConnection, fileName);

    }

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
     * Injects the FtpLet container.
     *
     * @param ftpLetContainer Ftplet container.
     */
    @Reference
    public void setFtpLetContainer(FtpLetContainer ftpLetContainer) {
        this.ftpLetContainer = ftpLetContainer;
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

    /**
     * Sets the optional timeout in seconds for sockets that are idle.
     *
     * @param timeout timeout in seconds.
     */
    @Property
    public void setIdleTimeout(int timeout) {
        this.idleTimeout = timeout * 1000;
    }

    /*
     * Transfers the file by calling the mapped FtpLet.
     */
    private void transfer(DefaultFtpSession session, int passivePort, DataConnection dataConnection, String fileName) {
        
        session.setMaxIdle(0);

        try {

            InputStream uploadData = dataConnection.getInputStream();

            FtpLet ftpLet = ftpLetContainer.getFtpLet(session.getCurrentDirectory());
            if (ftpLet == null) {
                ftpMonitor.noFtpLetRegistered(fileName);
                session.write(new DefaultResponse(426, "Data connection error"));
                return;
            }
            if (!ftpLet.onUpload(fileName, session, uploadData)) {
                ftpMonitor.uploadError(session.getUserName());
                session.write(new DefaultResponse(426, "Data connection error"));
                return;
            }
            session.write(new DefaultResponse(226, "Transfer complete"));

        } catch (Exception ex) {
            ftpMonitor.onException(ex, session.getUserName());
            session.write(new DefaultResponse(426, "Data connection error"));
        } finally {
            closeDataConnection(session, passivePort);
            session.setMaxIdle(idleTimeout);
            session.clearQuoteCommands();
        }

    }

    /*
     * Closes the data connection.
     */
    private void closeDataConnection(DefaultFtpSession session, int passivePort) {
        session.closeDataConnection();
        passiveConnectionService.release(passivePort);
    }

}
