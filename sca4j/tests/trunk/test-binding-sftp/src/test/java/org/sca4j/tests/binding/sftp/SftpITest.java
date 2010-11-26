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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

/**
 * ITest for SFTP binding
 */
public class SftpITest extends TestCase {
    @Reference protected SftpService sftpUserPwdService;
    @Reference protected SftpService sftpPkiService;
    @Reference protected TestSftpServer sftpServer;

    @Property protected String targetLocation;

    @Override
    protected void setUp() throws Exception {
        sftpServer.start(targetLocation);
    }

    public void testTransferWithUsernamePwd() throws IOException, InterruptedException {
        sftpUserPwdService.receive("sftp_userpwd.txt", new ByteArrayInputStream("Sftp userpwd".getBytes("UTF-8")));
        assertTrue("unable to upload file using userpwd security", new File(targetLocation, "sftp_userpwd.txt").exists());
    }

    public void testSftpWithPKI() throws IOException, InterruptedException {
        sftpPkiService.receive("sftp_pki.txt", new ByteArrayInputStream("Sftp pki".getBytes("UTF-8")));
        assertTrue("unable to upload file using pki security", new File(targetLocation, "sftp_pki.txt").exists());
    }

    @Override
    protected void tearDown() throws Exception {
        sftpServer.stop();
    }
}
