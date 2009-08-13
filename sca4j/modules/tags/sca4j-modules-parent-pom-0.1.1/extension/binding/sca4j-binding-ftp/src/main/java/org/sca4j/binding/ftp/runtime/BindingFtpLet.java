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
            throw new Exception("Target component doesn't supported upload");
        }
        
        Object[] args = new Object[] {fileName, uploadData};
        WorkContext workContext = new WorkContext();
        workContext.setHeader(FtpConstants.HEADER_CONTENT_TYPE, ftpSession.getContentType());
        workContext.setHeader(FtpConstants.COMMANDS, ftpSession.getQuoteCommands());
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
            throw new Exception("Target component doesn't supported upload");
        }
        
        Object[] args = new Object[] {fileName};
        WorkContext workContext = new WorkContext();
        workContext.setHeader(FtpConstants.HEADER_CONTENT_TYPE, ftpSession.getContentType());
        workContext.setHeader(FtpConstants.COMMANDS, ftpSession.getQuoteCommands());
        Message input = new MessageImpl(args, false, workContext);
        Message msg = downloadChain.getHeadInterceptor().invoke(input);
        if (msg.isFault()) {
            throw (Exception) msg.getBody();
        }
        return (InputStream) msg.getBody();
        
    }

}
