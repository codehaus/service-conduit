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
package org.sca4j.binding.burlap.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.caucho.burlap.io.BurlapInput;
import com.caucho.burlap.io.BurlapOutput;
import com.caucho.burlap.io.SerializerFactory;

import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;

/**
 * Servlet for handling the hessian service requests.
 *
 * @version $Revision: 3134 $ $Date: 2008-03-17 17:56:20 +0000 (Mon, 17 Mar 2008) $
 */
@SuppressWarnings("serial")
public class BurlapServiceHandler extends HttpServlet {

    /**
     * Map of op names to operation definitions.
     */
    private Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> ops;

    /**
     * The classloader to deserialize parameters in. Referencing the classloader directly is ok given this class must be cleaned up if the target
     * component associated with the classloader for this service is removed.
     */
    private ClassLoader classLoader;


    /**
     * Initializes the handler associated with the service.
     *
     * @param ops         Map of op names to operation definitions.
     * @param classLoader the classloader to load service interfaces with
     */
    public BurlapServiceHandler(Map<String, Map.Entry<PhysicalOperationDefinition, InvocationChain>> ops,
                                ClassLoader classLoader) {
        this.ops = ops;
        this.classLoader = classLoader;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            InputStream in = request.getInputStream();

            BurlapInput burlapInput = new BurlapInput(in);
            burlapInput.setSerializerFactory(new SerializerFactory());
            burlapInput.startCall();

            String methodName = burlapInput.getMethod();

            PhysicalOperationDefinition op = ops.get(methodName).getKey();
            Interceptor head = ops.get(methodName).getValue().getHeadInterceptor();

            Object[] args = new Object[op.getParameters().size()];
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                // Hessian uses the TCCL to deserialize parameters
                Thread.currentThread().setContextClassLoader(classLoader);
                for (int i = 0; i < args.length; i++) {
                    args[i] = burlapInput.readObject();
                }
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }


            burlapInput.completeCall();

            Message input = new MessageImpl(args, false, new WorkContext());

            Message output = head.invoke(input);
            Object ret = output.getBody();

            OutputStream out = response.getOutputStream();
            BurlapOutput burlapOutput = new BurlapOutput(out);

            burlapOutput.startReply();
            if (output.isFault()) {
                Throwable t = (Throwable) output.getBody();
                burlapOutput.writeFault("ServiceException", null, t);
            } else {
                burlapOutput.writeObject(ret);
            }
            burlapOutput.completeReply();

            out.close();
        } catch (RuntimeException e) {
            // TODO Add error handling and method overloading
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            // TODO Add error handling and method overloading
            e.printStackTrace();
            throw e;
        }
    }

}
