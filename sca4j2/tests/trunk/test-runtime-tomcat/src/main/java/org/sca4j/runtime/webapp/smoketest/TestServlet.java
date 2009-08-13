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
package org.sca4j.runtime.webapp.smoketest;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osoa.sca.ComponentContext;

import org.sca4j.runtime.webapp.smoketest.model.Employee;

/**
 * @version $Rev: 4959 $ $Date: 2008-07-06 18:19:24 +0100 (Sun, 06 Jul 2008) $
 */
public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1532086282614089270L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        String test = request.getParameter("test");
        if ("context".equals(test)) {
            HttpSession session = request.getSession();
            ComponentContext context = (ComponentContext) session.getAttribute("org.osoa.sca.ComponentContext");
            if (context == null) {
                response.sendError(500, "Context was not bound");
                return;
            }
            HelloService service = context.getService(HelloService.class, "hello");
            if (!"Hello World".equals(service.getGreeting())) {
                response.sendError(500, "Failed to create HelloService");
                return;
            }

            EmployeeService employeeService = context.getService(EmployeeService.class, "employeeService");
            employeeService.createEmployee(123l, "Barney Rubble");
            Employee employee = employeeService.findEmployee(123L);
            if (employee == null) {
                response.sendError(500, "Failed to persist Employee");
                return;
            }

            out.print("component URI is " + context.getURI());
        } else {
            response.sendError(500, "No test specified");
        }
    }
}
