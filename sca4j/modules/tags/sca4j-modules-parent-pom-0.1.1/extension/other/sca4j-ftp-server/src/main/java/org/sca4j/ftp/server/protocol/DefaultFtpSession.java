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
package org.sca4j.ftp.server.protocol;

import java.util.LinkedList;
import java.util.List;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.sca4j.ftp.api.FtpConstants;
import org.sca4j.ftp.api.Session;
import org.sca4j.ftp.server.data.DataConnection;
import org.sca4j.ftp.server.security.User;


/**
 * Represents an FTP session between a client and a server.
 *
 * @version $Revision$ $Date$
 */
public class DefaultFtpSession implements Session {

    public static final String USER = "org.sca4j.ftp.server.user";
    public static final String PASSIVE_PORT = "org.sca4j.ftp.server.passive.port";
    public static final String DATA_CONNECTION = "org.sca4j.ftp.server.data.connection";
    public static final String CURRENT_DIRECTORY = "org.sca4j.ftp.server.directory";
    public static final String CONTENT_TYPE = "org.sca4j.ftp.server.content.type";
    public static final String QUOTE_COMMANDS = "org.sca4j.ftp.server.quote.commands";

    private IoSession ioSession;

    /**
     * Initializes the wrapped IO session.
     *
     * @param ioSession Wrapped IO session.
     */
    public DefaultFtpSession(IoSession ioSession) {
        this.ioSession = ioSession;
        if (ioSession.getAttribute(QUOTE_COMMANDS) == null) {
            List<String> quoteCommands = new LinkedList<String>();
            ioSession.setAttribute(QUOTE_COMMANDS, quoteCommands);
        }
    }
    
    /* (non-Javadoc)
     * @see org.sca4j.ftp.server.protocol.Session#getQuoteCommands()
     */
    @SuppressWarnings("unchecked")
    public List<String> getQuoteCommands() {
        return (List<String>) ioSession.getAttribute(QUOTE_COMMANDS);
    }
    
    /**
     * Adds a quote command.
     * 
     * @param command Quote command to be added.
     */
    public void addQuoteCommand(String command) {
        getQuoteCommands().add(command);
    }
    
    /**
     * Clears all the quote command.
     */
    public void clearQuoteCommands() {
        getQuoteCommands().clear();
    }

    /**
     * Sets the current user.
     *
     * @param user Current user.
     */
    public void setUser(User user) {
        ioSession.setAttribute(USER, user);
    }

    /* (non-Javadoc)
     * @see org.sca4j.ftp.server.protocol.Session#getUserName()
     */
    public String getUserName() {
        User user = getUser();
        return user != null ? user.getName() : "";
    }

    /**
     * Gets the current user.
     *
     * @return Current user.
     */
    public User getUser() {
        return (User) ioSession.getAttribute(USER);
    }

    /**
     * Sets the session as authenticate.
     */
    public void setAuthenticated() {
        getUser().setAuthenticated();
    }

    /* (non-Javadoc)
     * @see org.sca4j.ftp.server.protocol.Session#isAuthenticated()
     */
    public boolean isAuthenticated() {
        User user = getUser();
        return user != null && user.isAuthenticated();
    }

    /**
     * Gets the passive port.
     *
     * @return Passiv port.
     */
    public int getPassivePort() {
        return (Integer) ioSession.getAttribute(PASSIVE_PORT);
    }

    /**
     * Set the passive port.
     *
     * @param passivePort Passive port.
     */
    public void setPassivePort(int passivePort) {
        ioSession.setAttribute(PASSIVE_PORT, passivePort);
    }

    /* (non-Javadoc)
     * @see org.sca4j.ftp.server.protocol.Session#getCurrentDirectory()
     */
    public String getCurrentDirectory() {
        String dir = (String) ioSession.getAttribute(CURRENT_DIRECTORY);
        if (dir == null) {
            return "/";
        }
        return dir;
    }

    /**
     * Sets the current working directory.
     *
     * @param dir the working directory.
     */
    public void setCurrentDirectory(String dir) {
        ioSession.setAttribute(CURRENT_DIRECTORY, dir);
    }

    /**
     * Writes a message out.
     *
     * @param object Message to be written.
     * @return Write future for the async operation.
     */
    public WriteFuture write(Object object) {
        return ioSession.write(object);
    }

    /**
     * Sets the initialized data connection.
     *
     * @param dataConnection Initialized data connection.
     */
    public void setDataConnection(DataConnection dataConnection) {
        ioSession.setAttribute(DATA_CONNECTION, dataConnection);
    }

    /**
     * Gets the initialized data connection.
     *
     * @return Initialized data connection.
     */
    public DataConnection getDataConnection() {
        return (DataConnection) ioSession.getAttribute(DATA_CONNECTION);
    }

    /**
     * Closes the data connection.
     */
    public void closeDataConnection() {

        DataConnection dataConnection = getDataConnection();
        if (null != dataConnection) {
            dataConnection.close();
        }

        setDataConnection(null);
        setPassivePort(0);

    }

    /**
     * Sets the data content type
     *
     * @param type the file transfer type
     */
    public void setContentType(String type) {
        if ("I".equals(type)) {
            ioSession.setAttribute(CONTENT_TYPE, FtpConstants.BINARY_TYPE);
        } else {
            ioSession.setAttribute(CONTENT_TYPE, FtpConstants.TEXT_TYPE);
        }
    }

    /* (non-Javadoc)
     * @see org.sca4j.ftp.server.protocol.Session#getContentType()
     */
    public String getContentType() {
        String type = (String) ioSession.getAttribute(CONTENT_TYPE);
        if (type == null) {
            // default to ASCII/text
            return FtpConstants.TEXT_TYPE;
        }
        return type;
    }

    /**
     * Closes the FTP session.
     */
    public void close() {
        ioSession.close(false);
    }
    
    /**
     * Touches the session during large transfer.
     */
    public void setMaxIdle(int idle) {
        ioSession.getConfig().setReaderIdleTime(idle);
    }

}
