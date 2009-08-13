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
package org.sca4j.binding.ftp.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.net.SocketFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.osoa.sca.ServiceUnavailableException;
import org.sca4j.binding.ftp.provision.FtpSecurity;
import org.sca4j.ftp.api.FtpConstants;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;

/**
 * Intercepts calls to the FTP Server and translates into either a STOR or RETR based on the
 * service contract, preceeded by a number of optional commands 
 * @version $Revision$ $Date$
 */
public class FtpTargetInterceptor implements Interceptor {

    private Interceptor next;
    private final int port;
    private final InetAddress hostAddress;
    private String remotePath;
    private String tmpFileSuffix;
    private SocketFactory factory;
    private FtpInterceptorMonitor monitor;

    private final FtpSecurity security;
    private final boolean active;

    /**
     * public constructor
     */
    public FtpTargetInterceptor(InetAddress hostAddress, 
                                int port, FtpSecurity 
                                security, 
                                boolean active,
                                SocketFactory factory, 
                                FtpInterceptorMonitor monitor) throws UnknownHostException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.security = security;
        this.active = active;
        this.factory = factory;
        this.monitor = monitor;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Message invoke(Message msg) {

        FTPClient ftpClient = new FTPClient();
        ftpClient.setSocketFactory(factory);
        List<String> dynamicCommands = msg.getWorkContext().getHeader(List.class, FtpConstants.COMMANDS);

        try {
            connectToServer(ftpClient);
            setTransferType(msg, ftpClient);
            login(ftpClient);
            setTransferMode(ftpClient);
            executeCommandList(dynamicCommands, ftpClient);
            Message message = transfer(msg, ftpClient);
            
            ftpClient.logout();
            ftpClient.disconnect();
            return message;
            
        } catch (IOException e) {
            throw new ServiceUnavailableException(e);
        }

    }

    /**
     * Sets the next interceptor
     */
    public void setNext(Interceptor next) {
        this.next = next;
    }

    /**
     * Sets remote path for the STOR operation.
     * @param remotePath remote path for the STOR operation
     */
    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    /**
     * Sets temporary file suffix to be used while file being transmitted.
     * @param tmpFileSuffix temporary file suffix to be used for file in transmission
     */
    public void setTmpFileSuffix(String tmpFileSuffix) {
        this.tmpFileSuffix = tmpFileSuffix;
    }

    /**
     * Gets the next interceptor
     */
    public Interceptor getNext() {
        return next;
    }

    /* Initiates a transfer of data either as a STORE or as a RETRIEVE depending on the number of args supplied */
    private Message transfer(Message msg, FTPClient ftpClient) throws IOException{

        Object[] args = (Object[]) msg.getBody();
        Message response = new MessageImpl();
        switch (args.length) {
            case 1:
                final InputStream responseStream = retrieveFile(ftpClient, args);
                response.setBody(responseStream);
                ftpClient.completePendingCommand();
                break;
            case 2:
                storeFile(ftpClient, args);
                break;
            default:
                throw new AssertionError(
                        "Unable to execute ftp transfer. Service contract must contain at least one input parameter");
        }

        monitor.onFtpResponse(ftpClient.getReplyString());
        return response;
    }

    /* Retrieves the file from the remote server */
    private InputStream retrieveFile(FTPClient client, Object[] args) throws IOException {
        String fileName = (String) args[0];
        monitor.onFtpCommand("RETR " + fileName);
        return client.retrieveFileStream(fileName);
    }

    /* Stores the file on the remote server */
    private void storeFile(FTPClient ftpClient, Object[] args) throws IOException {
        String fileName = (String) args[0];
        InputStream data = (InputStream) args[1];
        String remoteFileLocation = fileName;
        if (remotePath != null && remotePath.length() > 0) {
            remoteFileLocation = remotePath.endsWith("/") ? remotePath + fileName : remotePath + "/" + fileName;
        }

        String remoteTmpFileLocation = remoteFileLocation;
        if (tmpFileSuffix != null && tmpFileSuffix.length() > 0) {
            remoteTmpFileLocation += tmpFileSuffix;
        }

        monitor.onFtpCommand("STOR " + remoteFileLocation);
        if (!ftpClient.storeFile(remoteTmpFileLocation, data)) {
            throw new ServiceUnavailableException("Unable to upload data. Response sent from server: "
                    + ftpClient.getReplyString() + " ,remoteFileLocation:" + remoteFileLocation);
        }
        // Rename file back to original name if temporary file suffix was used while transmission.
        if (!remoteTmpFileLocation.equals(remoteFileLocation)) {
            ftpClient.rename(remoteTmpFileLocation, remoteFileLocation);
        }
    }

    /* logs in to the remote server */
    private void login(FTPClient ftpClient) throws IOException {
        /*
         * if (!ftpClient.login(security.getUser(), security.getPassword())) { throw new
         * ServiceUnavailableException("Invalid credentials"); }
         */
        // TODO Fix above
        monitor.onAuthentication(security.getUser());
        ftpClient.login(security.getUser(), security.getPassword());
        monitor.onFtpResponse(ftpClient.getReplyString());
    }

    /* Uses ftp client to connect to the remote server */
    private void connectToServer(FTPClient ftpClient) throws SocketException, IOException {
        monitor.onServerConnection(hostAddress, port);
        ftpClient.connect(hostAddress, port);
        monitor.onFtpResponse(ftpClient.getReplyString());
    }

    /* Sets the transfer mode depending on active or passive transfer */
    private void setTransferMode(FTPClient ftpClient) {
        if (active) {
            monitor.onFtpCommand("ACTV");
            ftpClient.enterLocalActiveMode();
            monitor.onFtpResponse(ftpClient.getReplyString());
        } else {
            monitor.onFtpCommand("PASV");
            ftpClient.enterLocalPassiveMode();
            monitor.onFtpResponse(ftpClient.getReplyString());
        }
    }

    /* Sets the transfer type to binary or ascii */
    private void setTransferType(Message msg, FTPClient ftpClient) throws IOException {
        String type = msg.getWorkContext().getHeader(String.class, FtpConstants.HEADER_CONTENT_TYPE);
        if (type != null && type.equalsIgnoreCase(FtpConstants.BINARY_TYPE)) {
            monitor.onFtpCommand("TYPE I");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            monitor.onFtpResponse(ftpClient.getReplyString());
        } else if (type != null && type.equalsIgnoreCase(FtpConstants.TEXT_TYPE)) {
            monitor.onFtpCommand("TYPE A");
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            monitor.onFtpResponse(ftpClient.getReplyString());
        }
    }

    /* executes a list of commands against the remote server */
    private void executeCommandList(List<String> commands, FTPClient ftpClient) throws IOException {
        if (commands != null) {
            for (String command : commands) {
                monitor.onFtpCommand(command);
                ftpClient.sendCommand("QUOTE " + command);
                monitor.onFtpResponse(ftpClient.getReplyString());
            }
        }
    }
}
