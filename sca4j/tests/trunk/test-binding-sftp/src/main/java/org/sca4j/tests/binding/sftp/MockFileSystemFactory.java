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

import java.io.File;

import org.apache.sshd.server.FileSystemFactory;
import org.apache.sshd.server.FileSystemView;
import org.apache.sshd.server.SshFile;
import org.apache.sshd.server.filesystem.NativeSshFile;

/**
 * Mock FileSystemFactory which allows to configure current directory for the uploaded files.
 * 
 * @author dhillonn
 */
public class MockFileSystemFactory implements FileSystemFactory {
    private final String currDir;

    public MockFileSystemFactory(String currDir) {
        this.currDir = currDir;
    }

    @Override
    public FileSystemView createFileSystemView(String userName) {
        return new MockFileSystemView(userName);
    }

    private class MockFileSystemView implements FileSystemView {
        private String userName;

        public MockFileSystemView(String userName) {
            this.userName = userName;
        }

        @Override
        public SshFile getFile(String file) {
            // get actual file object
            String physicalName = NativeSshFile.getPhysicalName("/", currDir, file, false);
            File fileObj = new File(physicalName);

            // strip the root directory and return
            String userFileName = physicalName.substring("/".length() - 1);
            return new MockNativeSshFile(userFileName, fileObj, userName);
        }
    }

    private class MockNativeSshFile extends NativeSshFile {
        protected MockNativeSshFile(String fileName, File file, String userName) {
            super(fileName, file, userName);
        }
    }
}
