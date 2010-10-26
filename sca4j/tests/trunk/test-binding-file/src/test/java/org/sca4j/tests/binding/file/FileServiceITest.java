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
package org.sca4j.tests.binding.file;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;

/**
 * ITest for File binding
 */
public class FileServiceITest extends TestCase {
    @Reference protected FileService staticFileService1;
    @Reference protected FileService staticFileService2;
    @Reference protected FileService dynamicFileService;
    @Reference protected LatchService latchService1;
    @Reference protected LatchService latchService2;
    @Property protected String archiveFileLocation;

    /**
     * Test file service write and read operation.
     */
    public void testSimpleWriteReadDelete() throws Exception {
        String testData = "File binding test";
        staticFileService1.receive("filebinding.text", IOUtils.toInputStream(testData));
        latchService1.await();
        assertEquals(testData, latchService1.getPayload());
    }
    
    /**
     * Test file service write, read and archive operation
     */
    public void testFileWriteReadArchive() throws Exception {
        String testData = "file archive test";
        final String fileName = "archivefile.text";
        staticFileService2.receive(fileName, IOUtils.toInputStream(testData));
        latchService2.await();
        //Check Archive file and read contents
        assertEquals(testData, latchService2.getPayload());
        
        //Check if file is archived okay
        Thread.sleep(2000); //TODO: wait for 2 seconds before runtime archives the file
        final File archivedFile = new File(archiveFileLocation);
        final String[] archivedFiles = archivedFile.list();
        assertTrue("File not archived", archivedFiles.length == 1);
    }
    
    /**
     * Test that file could be written using Reference with dynamic endpoint.
     */
    public void testReferenceWithDynamicEndPoint() throws IOException {
        String testData = "File binding test";
        File tempFile = new File("dynamic.txt");
        dynamicFileService.receive(tempFile.getPath(), IOUtils.toInputStream(testData));
        
        //Check if file has been created
        assertTrue("Dynamic file is not created", tempFile.exists());
        tempFile.delete();
    }
}
