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
package org.sca4j.binding.http.runtime.invocation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sca4j.binding.http.runtime.injection.DataBinder;
import org.sca4j.binding.http.runtime.introspection.DataBinding;
import org.sca4j.binding.http.runtime.introspection.HttpMethod;
import org.sca4j.binding.http.runtime.introspection.OperationMetadata;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Servlet for receiving incoming invocations.
 *
 */
public class HttpBindingServlet extends HttpServlet {

    private static final long serialVersionUID = -6018601445344942027L;
    
    private OperationMetadata operationMetadata;
    private InvocationChain invocationChain;
    private Map<DataBinding, DataBinder> inboundBinders;
    private Map<DataBinding, DataBinder> outboundBinders;
    
    public HttpBindingServlet(OperationMetadata operationMetadata, InvocationChain invocationChain, Map<DataBinding, DataBinder> inboundBinders, Map<DataBinding, DataBinder> outboundBinders) {
        this.operationMetadata = operationMetadata;
        this.invocationChain = invocationChain;
        this.inboundBinders = inboundBinders;
        this.outboundBinders = outboundBinders;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doInternal(req, resp, HttpMethod.GET);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doInternal(req, resp, HttpMethod.POST);
    }

    private void doInternal(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod) throws ServletException, IOException {
    	
    	if (!operationMetadata.getHttpMethod().equals(httpMethod)) {
    		throw new ServletException("Http method " + httpMethod + " not supported");
    	}
            
        Message message = inboundBinders.get(operationMetadata.getInBinding()).unmarshal(req, operationMetadata);
        message = invocationChain.getHeadInterceptor().invoke(message);
        if (operationMetadata.getOutBinding() != null) {
            outboundBinders.get(operationMetadata.getOutBinding()).marshal(resp, message);
        } else {
            byte[] response = message.getBody().toString().getBytes();
            OutputStream outputStream = resp.getOutputStream();
            outputStream.write(response, 0, response.length);
            outputStream.flush();
        }
        
    }

}
