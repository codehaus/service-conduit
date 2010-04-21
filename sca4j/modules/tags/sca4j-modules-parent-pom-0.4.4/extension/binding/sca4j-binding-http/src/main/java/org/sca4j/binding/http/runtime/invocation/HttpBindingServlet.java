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
    private boolean urlCase;

    public HttpBindingServlet(OperationMetadata operationMetadata, InvocationChain invocationChain, Map<DataBinding, DataBinder> inboundBinders, Map<DataBinding, DataBinder> outboundBinders, boolean urlCase) {
        this.operationMetadata = operationMetadata;
        this.invocationChain = invocationChain;
        this.inboundBinders = inboundBinders;
        this.outboundBinders = outboundBinders;
        this.urlCase = urlCase;
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

        Message message = inboundBinders.get(operationMetadata.getInBinding()).unmarshal(req, operationMetadata, urlCase);
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
