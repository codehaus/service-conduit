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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.net.ftp.FTPClient;
import org.sca4j.ftp.server.codec.CodecFactory;
import org.sca4j.ftp.server.ftplet.DefaultFtpLetContainer;
import org.sca4j.ftp.server.handler.PassRequestHandler;
import org.sca4j.ftp.server.handler.PasvRequestHandler;
import org.sca4j.ftp.server.handler.StorRequestHandler;
import org.sca4j.ftp.server.handler.UserRequestHandler;
import org.sca4j.ftp.server.monitor.FtpMonitor;
import org.sca4j.ftp.server.passive.PassiveConnectionServiceImpl;
import org.sca4j.ftp.server.protocol.RequestHandler;
import org.sca4j.ftp.server.security.FileSystemUserManager;
import org.sca4j.ftp.spi.FtpLetContainer;

/**
 *
 * @version $Revision$ $Date$
 */
public class SCA4JFtpHostTest extends TestCase {
    
    private SCA4JFtpHost ftpHost;
    
    public void setUp() throws Exception {
        
        FtpMonitor ftpMonitor = new TestFtpMonitor();
        
        Map<String, RequestHandler> requestHandlers = new HashMap<String, RequestHandler>();
        
        Map<String, String> users = new HashMap<String, String>();
        users.put("meeraj", "password");
        FileSystemUserManager userManager = new FileSystemUserManager();
        userManager.setUsers(users);
        requestHandlers.put("USER", new UserRequestHandler());
        
        PassRequestHandler passCommandHandler = new PassRequestHandler();
        passCommandHandler.setUserManager(userManager);
        requestHandlers.put("PASS", passCommandHandler);
        
        PassiveConnectionServiceImpl passiveConnectionService = new PassiveConnectionServiceImpl();
        passiveConnectionService.setMinPort(50000);
        passiveConnectionService.setMaxPort(60000);
        passiveConnectionService.init();
        PasvRequestHandler pasvRequestHandler = new PasvRequestHandler();
        pasvRequestHandler.setPassivePortService(passiveConnectionService);
        requestHandlers.put("PASV", pasvRequestHandler);
        
        StorRequestHandler storRequestHandler = new StorRequestHandler();
        storRequestHandler.setPassivePortService(passiveConnectionService);
        storRequestHandler.setFtpMonitor(ftpMonitor);
        FtpLetContainer ftpLetContainer = new DefaultFtpLetContainer();
        ftpLetContainer.registerFtpLet("/", new DummyFtpLet());
        storRequestHandler.setFtpLetContainer(ftpLetContainer);
        requestHandlers.put("STOR", storRequestHandler);
        
        ftpHost = new SCA4JFtpHost();
        
        FtpHandler ftpHandler = new FtpHandler();
        ftpHandler.setRequestHandlers(requestHandlers);
        ftpHandler.setFtpMonitor(ftpMonitor);
        
        ftpHost.setFtpHandler(ftpHandler);
        ftpHost.setCommandPort(1234);
        ftpHost.setCodecFactory(new CodecFactory());
        ftpHost.start();
        
    }
    
    public void tearDown() throws Exception {
        ftpHost.stop();
    }

    public void testValidLogin() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(InetAddress.getLocalHost(), 1234);
        ftpClient.user("meeraj");
        assertEquals(230, ftpClient.pass("password"));        
    }

    public void testInvalidLogin() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(InetAddress.getLocalHost(), 1234);
        ftpClient.user("meeraj");
        assertEquals(530, ftpClient.pass("password1"));        
    }
    
    public void testStor() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(InetAddress.getLocalHost(), 1234);
        ftpClient.user("meeraj");
        ftpClient.pass("password");
        ftpClient.enterLocalPassiveMode();
        ftpClient.storeFile("/resource/test.dat", new ByteArrayInputStream("TEST\r\n".getBytes()));
    }

}
