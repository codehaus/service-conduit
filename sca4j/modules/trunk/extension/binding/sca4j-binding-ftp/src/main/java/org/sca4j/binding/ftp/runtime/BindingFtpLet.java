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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.binding.ftp.runtime;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.sca4j.ftp.api.FtpConstants;
import org.sca4j.ftp.api.FtpLet;
import org.sca4j.ftp.api.Session;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * Handles incoming FTP puts from the protocol stack.
 *
 * @version $Revision$ $Date$
 */
public class BindingFtpLet implements FtpLet {
    
    private String servicePath;
    private BindingMonitor monitor;
    
    private InvocationChain downloadChain;
    private InvocationChain uploadChain;

    public BindingFtpLet(String servicePath, Wire wire, BindingMonitor monitor) {
        
        this.servicePath = servicePath;
        this.monitor = monitor;
        
        Map<PhysicalOperationDefinition, InvocationChain> invocationChains = wire.getInvocationChains();
        if (invocationChains.size() <= 0 || invocationChains.size() > 2) {
            throw new AssertionError("Expects one download and/or an upload method");
        }
        
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : invocationChains.entrySet()) {
            PhysicalOperationDefinition opd = entry.getKey();
            List<?> parameters = opd.getParameters();
            if (parameters.size() == 1) {
                if (!"java.lang.String".equals(parameters.get(0)) ) {
                    throw new AssertionError("Download methods take a single string argument");
                }
                if (!"java.io.InputStream".equals(opd.getReturnType())) {
                    throw new AssertionError("Download methods need to return an input stream");
                }
                downloadChain = entry.getValue();
            } else if (parameters.size() == 2) {
                if (!"java.lang.String".equals(parameters.get(0)) ) {
                    throw new AssertionError("Upload methods take a string and an input stream");
                }
                if (!"java.io.InputStream".equals(parameters.get(1)) ) {
                    throw new AssertionError("Upload methods take a string and an input stream");
                }
                uploadChain = entry.getValue();
            } else {
                throw new AssertionError("Unsupported operation signature: " + opd.getName());
            }
        }
        
    }

    public boolean onUpload(String fileName, Session ftpSession, InputStream uploadData) throws Exception {
        
        if (uploadChain == null) {
            throw new Exception("Target component doesn't support upload");
        }
        
        Object[] args = new Object[] {fileName, uploadData};
        WorkContext workContext = new WorkContext();
        workContext.setHeader(FtpConstants.HEADER_CONTENT_TYPE, ftpSession.getContentType());
        workContext.setHeader(FtpConstants.COMMANDS, ftpSession.getSiteCommands());
        Message input = new MessageImpl(args, false, workContext);
        Message msg = uploadChain.getHeadInterceptor().invoke(input);
        if (msg.isFault()) {
            monitor.fileProcessingError(servicePath, (Throwable) msg.getBody());
            return false;
        }        
        return true;
        
    }
    
    public InputStream onDownload(String fileName, Session ftpSession) throws Exception {
        
        if (downloadChain == null) {
            throw new Exception("Target component doesn't support download");
        }
        
        Object[] args = new Object[] {fileName};
        WorkContext workContext = new WorkContext();
        workContext.setHeader(FtpConstants.HEADER_CONTENT_TYPE, ftpSession.getContentType());
        workContext.setHeader(FtpConstants.COMMANDS, ftpSession.getSiteCommands());
        Message input = new MessageImpl(args, false, workContext);
        Message msg = downloadChain.getHeadInterceptor().invoke(input);
        if (msg.isFault()) {
            throw (Exception) msg.getBody();
        }
        return (InputStream) msg.getBody();
        
    }

}
