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
package org.sca4j.binding.ws.metro.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.transport.http.servlet.ServletAdapterList;
import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;

/**
 * Delegate for JAX-WS end-points, called from {@link WSServlet}. It keeps map of registered end-points wrapped in {@link ServletAdapter} keyed on end-point URI specified WS-Binding.
 * 
 */
public class SCA4JWSServletDelegate extends WSServletDelegate {

    private Map<String, ServletAdapter> fixedUrlPatternEndpoints = new HashMap<String, ServletAdapter>();
    private ServletAdapterList servletAdaptorFactory;

    public SCA4JWSServletDelegate(ServletContext context) {
        super(new ArrayList<ServletAdapter>(), context);
        servletAdaptorFactory = new ServletAdapterList(context);
    }

    public void registerEndPoint(String endPointUrl, WSEndpoint<?> wsEndPoint) {
        fixedUrlPatternEndpoints.put(endPointUrl, servletAdaptorFactory.createAdapter(endPointUrl, endPointUrl, wsEndPoint));
    }

    @Override
    protected ServletAdapter getTarget(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String endPointUrl = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        return fixedUrlPatternEndpoints.get(endPointUrl);
    }

}
