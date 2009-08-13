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
package org.sca4j.tests.binding.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMText;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;
import org.sca4j.api.SCA4JRequestContext;

/**
 * @version $Revision$ $Date$
 */
public class FtpDataTransferServiceImpl implements FtpDataTransferService {

    @Context
    protected SCA4JRequestContext context;

    @Reference
    protected WsDataTransferService wsDataTransferService;

    public InputStream downloadData(String fileName) throws Exception {
        return new ByteArrayInputStream("TEST".getBytes());
    }

    public void uploadData(String fileName, InputStream data) throws Exception {
        String type = context.getHeader(String.class, "f3.contentType");
        if ("BINARY".equals(type)) {
            handleBinary(data);
        } else {
            handleText(fileName, data);
        }
        System.err.println("****************" + context.getHeader(List.class, "f3.ftp.commands"));
    }
    
    private void handleBinary(InputStream data) throws IOException {
        // test basic binary transfer
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte buffer[] = new byte[1024];
        for (int count; (count = data.read(buffer, 0, buffer.length)) > 0;) {
            stream.write(buffer, 0, count);
        }
        byte[] expected = new byte[]{0x9};
        if (!Arrays.equals(expected, stream.toByteArray())) {
            throw new AssertionError("Invalid binary file received");
        }
    }

    private void handleText(String fileName, InputStream data) {
        try {
            // test text streaming to a web service
            OMElement wrapper = createWrapper(fileName, data);
            wsDataTransferService.transferData(wrapper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private OMElement createWrapper(final String fileName, final InputStream data) {

        OMFactory factory = OMAbstractFactory.getOMFactory();

        DataHandler dataHandler = new DataHandler(new DataSource() {
            public String getContentType() {
                return "text/dat";
            }

            public InputStream getInputStream() throws IOException {
                return data;
            }

            public String getName() {
                return fileName;
            }

            public OutputStream getOutputStream() throws IOException {
                return null;
            }
        });

        OMElement wrapper = factory.createOMElement("wrapper", null);

        OMElement fileElement = factory.createOMElement("fileName", null);
        fileElement.addChild(factory.createOMText(fileName));

        OMElement dataElement = factory.createOMElement("data", null);
        OMText text = factory.createOMText(dataHandler, true);
        text.setOptimize(true);
        dataElement.addChild(text);

        wrapper.addChild(fileElement);
        wrapper.addChild(dataElement);

        return wrapper;

    }

}
