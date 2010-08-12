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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.oasisopen.sca.annotation.Reference;

/**
 * 
 * @version $Revision$ $Date$
 */
public class FileServiceImpl implements FileService {
    @Reference protected LatchService latchService;
    
    public void receive(String fileName, InputStream payload) throws IOException {
        System.out.println("File received with fileName: " + fileName);
        latchService.setPayload(IOUtils.toString(payload));
    }
}
