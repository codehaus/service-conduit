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
package org.sca4j.binding.ws.axis2.runtime.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.AxisServlet;

/**
 * @version $Revision$ $Date$
 */
@SuppressWarnings("serial")
public class SCA4JAxisServlet extends AxisServlet {

    /**
     * Initializes the Axis configuration context.
     *
     * @param configurationContext Axis configuration context.
     */
    public SCA4JAxisServlet(final ConfigurationContext configurationContext) {
        this.configContext = configurationContext;
    }

    /**
     * Adds the Axis configuration context to the servlet context.
     *
     * @see org.apache.axis2.transport.http.AxisServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {

        ServletContext servletContext = config.getServletContext();
        servletContext.setAttribute(AxisServlet.CONFIGURATION_CONTEXT, configContext);

        super.init(config);

    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Thread currentThread = Thread.currentThread();
        ClassLoader oldCl = currentThread.getContextClassLoader();

        try {

            // TODO May be we want to do an MPCL with app cl as well
            ClassLoader systemCl = getClass().getClassLoader();
            currentThread.setContextClassLoader(systemCl);
            super.service(request, response);

        } finally {
            currentThread.setContextClassLoader(oldCl);
        }

    }

    /**
     * Implementaion of POST interface
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    /*protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //set the initial buffer for a larger value
        response.setBufferSize(1024 * 8);

        initContextRoot(request);

        MessageContext msgContext;
        OutputStream out = response.getOutputStream();
        String contentType = request.getContentType();
        if (!HTTPTransportUtils.isRESTRequest(contentType)) {
            msgContext = createMessageContext(request, response);
            msgContext.setProperty(Constants.Configuration.CONTENT_TYPE, contentType);
            try {
                // adding ServletContext into msgContext;
                InvocationResponse pi = HTTPTransportUtils.
                        processHTTPPostRequest(msgContext,
                                new BufferedInputStream(request.getInputStream()),
                                new BufferedOutputStream(out),
                                contentType,
                                request.getHeader(HTTPConstants.HEADER_SOAP_ACTION),
                                request.getRequestURL().toString());

                Boolean holdResponse =
                        (Boolean) msgContext.getProperty(RequestResponseTransport.HOLD_RESPONSE);

                if (pi.equals(InvocationResponse.SUSPEND) ||
                        (holdResponse != null && Boolean.TRUE.equals(holdResponse))) {
                    ((RequestResponseTransport) msgContext
                            .getProperty(RequestResponseTransport.TRANSPORT_CONTROL))
                            .awaitResponse();
                }
                response.setContentType("text/xml; charset="
                        + msgContext
                        .getProperty(Constants.Configuration.CHARACTER_SET_ENCODING));
                // if data has not been sent back and this is not a signal response
                if (!TransportUtils.isResponseWritten(msgContext)  
                        && (((RequestResponseTransport) 
                                msgContext.getProperty(
                                        RequestResponseTransport.TRANSPORT_CONTROL)).
                                        getStatus() != RequestResponseTransport.
                                        RequestResponseTransportStatus.SIGNALLED)) {
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                }

            } catch (AxisFault e) {
                e.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                TransportUtils.deleteAttachments(msgContext);
            }
        } else {
            if (!disableREST) {
                new RestRequestProcessor(Constants.Configuration.HTTP_METHOD_POST, request, response)
                        .processXMLRequest();
            } else {
                showRestDisabledErrorMessage(response);
            }
        }
    }*/

}
