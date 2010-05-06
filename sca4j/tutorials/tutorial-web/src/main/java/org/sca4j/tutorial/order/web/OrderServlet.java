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
package org.sca4j.tutorial.order.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sca4j.tutorial.order.OrderComponent;

public class OrderServlet extends HttpServlet {

    private ServletContext servletContext;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        servletContext = config.getServletContext();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        
        String productName = request.getParameter("productName");
        String address = request.getParameter("address");
        String creditCard = request.getParameter("creditCard");
        
        OrderComponent orderComponent = (OrderComponent) servletContext.getAttribute("orderComponent");
        boolean result = orderComponent.placeOrder(productName, address, creditCard);
        
        PrintWriter printWriter = response.getWriter();
        printWriter.print("Order placed " + result);
        printWriter.flush();
        printWriter.close();

    }

}
