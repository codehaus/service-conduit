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
package org.sca4j.tests.binding.sftp;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Scope;

/**
 * Test SSHD server to test SFTP binding
 * 
 * @author dhillonn
 */
@Scope("COMPOSITE")
@EagerInit
public class TestSftpServer {
    @Property
    protected String serverIdentityKey;

    private SshServer sshd;

    @SuppressWarnings("unchecked")
    public void start(String currentDir) throws IOException {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2000);
        sshd.setFileSystemFactory(new MockFileSystemFactory(currentDir));
        sshd.setKeyPairProvider(new FileKeyPairProvider(new String[] { serverIdentityKey }));
        final NamedFactory<Command> factory = new SftpSubsystem.Factory();
        sshd.setSubsystemFactories(Arrays.asList(factory));
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            public boolean authenticate(String username, String password, ServerSession session) {
                return username != null && username.equals(password);
            }
        });
        sshd.setPublickeyAuthenticator(new PublickeyAuthenticator() {
            public boolean authenticate(String username, PublicKey key, ServerSession session) {
                System.out.println("Public key received for authentication:" + key);
                return true;
            }
        });
        sshd.start();
    }

    public void stop() throws InterruptedException {
        sshd.stop();
    }    
}
