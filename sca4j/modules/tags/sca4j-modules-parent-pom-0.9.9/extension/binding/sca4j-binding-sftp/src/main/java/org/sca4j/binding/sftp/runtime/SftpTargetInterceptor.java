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
 */
package org.sca4j.binding.sftp.runtime;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.oasisopen.sca.ServiceRuntimeException;
import org.sca4j.binding.sftp.common.SecurityPolicy;
import org.sca4j.binding.sftp.common.SecurityPolicy.PkiSecurity;
import org.sca4j.binding.sftp.common.SecurityPolicy.UsernamePasswordSecurity;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.wire.Interceptor;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Intercepts calls to the SFTP server from the reference end of the wire.
 */
public class SftpTargetInterceptor implements Interceptor {
    public static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
    public static final String CHANNEL_SFTP = "sftp";
    private Interceptor next;

    private final String host;
    private final int port;    
    private final SecurityPolicy securityPolicy;
    private String tmpFileSuffix;
    private String remotePath;

    public SftpTargetInterceptor(String host, int port, SecurityPolicy securityPolicy) {
        this.host = host;
        this.port = port;
        this.securityPolicy = securityPolicy;
    }

    /**
     * {@inheritDoc}
     */
    public Message invoke(Message msg) {
        Session session = null;
        try {
            JSch sftpClient = new JSch();
            session = login(sftpClient);
            transfer(session, msg);
            session.disconnect();

            return new MessageImpl();
        } catch (JSchException e) {
            throw new ServiceRuntimeException(e);
        } catch (SftpException e) {
            throw new ServiceRuntimeException(e);
        } finally {
            closeQuietly(session);
        }
    }

    private void closeQuietly(Session session) {
        try {
            if (session != null) {
                session.disconnect();
            }
        } catch (RuntimeException e) {
        }
    }

    private void closeQuietly(ChannelSftp channel) {
        try {
            if (channel != null) {
                channel.disconnect();
            }
        } catch (RuntimeException e) {
        }
    }

    private Session login(JSch jsch) throws JSchException {
        Properties hash = new Properties();
        hash.put(STRICT_HOST_KEY_CHECKING, "no");

        if (UsernamePasswordSecurity.class.isInstance(securityPolicy)) {
            final UsernamePasswordSecurity usernameSecurity = UsernamePasswordSecurity.class.cast(securityPolicy);
            final Session session = jsch.getSession(usernameSecurity.getUser(), host);
            session.setConfig(hash);
            session.setPort(port);
            session.setPassword(usernameSecurity.getPassword());
            session.connect();
            return session;

        } else if (PkiSecurity.class.isInstance(securityPolicy)) {
            final PkiSecurity pkiSecurity = PkiSecurity.class.cast(securityPolicy);

            if (pkiSecurity.getPassphrase() == null || "".equals(pkiSecurity.getPassphrase())) {
                jsch.addIdentity(new File(pkiSecurity.getIdentityFile()).getAbsolutePath());
            } else {
                jsch.addIdentity(new File(pkiSecurity.getIdentityFile()).getAbsolutePath(), pkiSecurity.getPassphrase());
            }
            final Session session = jsch.getSession(pkiSecurity.getUser(), host);
            session.setConfig(hash);
            session.setPort(port);
            session.connect();
            return session;

        } else {
            throw new ServiceRuntimeException("Unknown security configuration");
        }
    }

    private void transfer(Session session, Message msg) throws JSchException, SftpException {
        ChannelSftp channelSftp = null;
        try {
            Object[] args = (Object[]) msg.getBody();
            String fileName = (String) args[0];
            InputStream data = (InputStream) args[1];

            channelSftp = (ChannelSftp) session.openChannel(CHANNEL_SFTP);
            channelSftp.connect();

            String remoteFileLocation = fileName;
            if (remotePath != null && !"".equals(remotePath)) {
                remoteFileLocation = normaliseRemoteFileLocation(remotePath, fileName);
            }

            String remoteTmpFileLocation = remoteFileLocation;
            if (tmpFileSuffix != null && !"".equals(tmpFileSuffix)) {
                remoteTmpFileLocation += tmpFileSuffix;
            }

            channelSftp.put(data, remoteTmpFileLocation);

            if (!remoteTmpFileLocation.equals(remoteFileLocation)) {
                channelSftp.rename(remoteTmpFileLocation, remoteFileLocation);
            }

        } finally {
            closeQuietly(channelSftp);
        }
    }
    
    private String normaliseRemoteFileLocation(String remotePath, String fileName) {
        String remoteFileLocation = remotePath.endsWith("/") ? remotePath + fileName : remotePath + "/" + fileName;
        if (remoteFileLocation.startsWith("/~/")) {// relative path
            return remoteFileLocation.substring(3, remoteFileLocation.length());
        } else { //absolute path
            return remoteFileLocation;
        }
    }

    /**
     * Sets the next interceptor
     */
    public void setNext(Interceptor next) {
        this.next = next;
    }

    /**
     * Gets the next interceptor
     */
    public Interceptor getNext() {
        return next;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public void setTmpFileSuffix(String tmpFileSuffix) {
        this.tmpFileSuffix = tmpFileSuffix;
    }
}
