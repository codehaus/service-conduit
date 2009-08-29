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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.binding.hessian.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.ConversationContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;

import org.osoa.sca.Conversation;

/**
 * Servlet for handling the hessian service requests.
 *
 * @version $Revision: 3560 $ $Date: 2008-04-04 18:32:49 +0100 (Fri, 04 Apr 2008) $
 */
@SuppressWarnings("serial")
public class HessianServiceHandler extends HttpServlet {

    /**
     * Map of op names to operation definitions.
     */
    private Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> ops;

    private String callbackUri;
    /**
     * The classloader to deserialize parameters in. Referencing the classloader directly is ok given this class must be cleaned up if the target
     * component associated with the classloader for this service is removed.
     */
    private final ClassLoader classLoader;

    private final SerializerFactory serializerFactory;

    /**
     * Initializes the wire associated with the service.
     *
     * @param ops               Map of op names to operation definitions.
     * @param callbackUri       the callback URI or null if the wire targets a unidirectional service
     * @param classLoader       the classloader to load parameters in
     * @param serializerFactory the factory for Hessian serializers
     */
    public HessianServiceHandler(Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> ops,
                                 String callbackUri,
                                 ClassLoader classLoader,
                                 SerializerFactory serializerFactory) {
        this.ops = ops;
        this.callbackUri = callbackUri;
        this.classLoader = classLoader;
        this.serializerFactory = serializerFactory;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handles the hessian requests.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InputStream in = request.getInputStream();

        Hessian2Input hessianInput = new Hessian2Input(in);
        hessianInput.setSerializerFactory(serializerFactory);
        String header = hessianInput.readHeader();
        if (!"callFrames".equals(header)) {
            throw new InvalidTransportException("CallFrames header not found");
        }
        @SuppressWarnings("unchecked")
        List<CallFrame> callFrames = (List<CallFrame>) hessianInput.readObject();
        hessianInput.readMethod();

        // TODO handle method overloading
        String methodName = hessianInput.getMethod();
        PhysicalOperationDefinition op = ops.get(methodName).getKey();
        Interceptor head = ops.get(methodName).getValue().getHeadInterceptor();

        Object[] args = new Object[op.getParameters().size()];
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            // Hessian uses the TCCL to deserialize parameters
            Thread.currentThread().setContextClassLoader(classLoader);
            for (int i = 0; i < args.length; i++) {
                args[i] = hessianInput.readObject();
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
        hessianInput.completeCall();

        WorkContext workContext = new WorkContext();
        workContext.addCallFrames(callFrames);
        CallFrame previous = workContext.peekCallFrame();
        // Copy correlation and conversation information from incoming frame to new frame
        // Note that the callback URI is set to the callback address of this service so its callback wire can be mapped in the case of a
        // bidirectional service
        Object id = previous.getCorrelationId(Object.class);
        ConversationContext context = previous.getConversationContext();
        Conversation conversation = previous.getConversation();
        CallFrame frame = new CallFrame(callbackUri, id, conversation, context);
        callFrames.add(frame);
        Message input = new MessageImpl(args, false, workContext);

        Message output = head.invoke(input);

        OutputStream out = response.getOutputStream();
        Hessian2Output hessianOutput = new Hessian2Output(out);
        hessianOutput.setSerializerFactory(serializerFactory);

        hessianOutput.startReply();
        if (output.isFault()) {
            Throwable t = (Throwable) output.getBody();
            // FIXME work around for FABRICTHREE-161
            //  hessianOutput.writeFault("ServiceException", null, t);
            hessianOutput.writeFault("ServiceException", t.getMessage(), t.getClass());
        } else {
            hessianOutput.writeObject(output.getBody());
        }
        hessianOutput.completeReply();
        hessianOutput.flush();
        out.close();
        // note we don't need to pop the CallFrame as the CallFrame stack is thrown away
    }

}
