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
package org.sca4j.jetty;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.ServletMapping;
import org.mortbay.jetty.servlet.SessionHandler;
import org.mortbay.log.Log;
import org.mortbay.log.Logger;
import org.mortbay.thread.BoundedThreadPool;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.sca4j.api.annotation.Monitor;

/**
 * Implements an HTTP transport service using Jetty.
 *
 * @version $$Rev: 5352 $$ $$Date: 2008-09-08 21:49:06 +0100 (Mon, 08 Sep 2008) $$
 */
@EagerInit
public class JettyServiceImpl implements JettyService {

    private static final String ROOT = "/";

    private final Object joinLock = new Object();
    
    @Property protected int httpPort = 8080;
    @Property protected int httpsPort = -1;
    @Property protected String keystore;
    @Property protected String certPassword;
    @Property protected String keyPassword;
    @Property protected String truststore;
    @Property protected String trustPassword;
    @Property protected boolean sendServerVersion;
    @Property protected boolean clientAuth;

    private TransportMonitor monitor;
    private boolean debug;
    private Server server;
    private ServletHandler servletHandler;
    private ContextHandlerCollection rootHandler;


    static {
        // hack to replace the static Jetty logger
        System.setProperty("org.mortbay.log.class", JettyLogger.class.getName());
    }


    @Constructor
    public JettyServiceImpl(@Monitor TransportMonitor monitor) {
        this.monitor = monitor;
        // Jetty uses a static logger, so jam in the monitor into a static reference
        Logger logger = Log.getLogger(null);
        if (logger instanceof JettyLogger) {
            JettyLogger jettyLogger = (JettyLogger) logger;
            jettyLogger.setMonitor(this.monitor);
            if (debug) {
                jettyLogger.setDebugEnabled(true);
            }
        }
    }

    @Init
    public void init() throws Exception {
        server = new Server();
        initializeThreadPool();
        initializeConnector();
        initializeRootContextHandler();
        server.setStopAtShutdown(true);
        server.setSendServerVersion(sendServerVersion);
        monitor.extensionStarted();
        monitor.startHttpListener(httpPort);
        if (httpsPort > -1) {
            monitor.startHttpsListener(httpsPort);
        }
        server.start();
    }

    @Destroy
    public void destroy() throws Exception {
        synchronized (joinLock) {
            joinLock.notifyAll();
        }
        server.stop();
        monitor.extensionStopped();
    }

    public ServletContext getServletContext() {
        return servletHandler.getServletContext();
    }

    public void registerMapping(String path, Servlet servlet) {
        ServletHolder holder = new ServletHolder(servlet);
        servletHandler.addServlet(holder);
        ServletMapping mapping = new ServletMapping();
        mapping.setServletName(holder.getName());
        mapping.setPathSpec(path);
        servletHandler.addServletMapping(mapping);
    }

    public Servlet unregisterMapping(String string) {
        return null;
    }

    public boolean isMappingRegistered(String path) {
        throw new UnsupportedOperationException();
    }

    public Server getServer() {
        return server;
    }

    public void registerHandler(Handler handler) {
        rootHandler.addHandler(handler);
    }

    public int getHttpPort() {
        return httpPort;
    }

    private void initializeConnector() {
        if (httpsPort > -1) {
            Connector httpConnector = new SelectChannelConnector();
            httpConnector.setPort(httpPort);
            SslSocketConnector sslConnector = new SslSocketConnector();
            sslConnector.setPort(httpsPort);
            sslConnector.setKeystore(keystore);
            sslConnector.setPassword(certPassword);
            sslConnector.setKeyPassword(keyPassword);
            sslConnector.setNeedClientAuth(clientAuth);
            if (truststore == null) {
                truststore = keystore;
            }
            if (trustPassword == null) {
                trustPassword = keyPassword;
            }
            sslConnector.setTruststore(truststore);
            sslConnector.setTrustPassword(trustPassword);
            server.setConnectors(new Connector[]{httpConnector, sslConnector});
        } else {
            SelectChannelConnector selectConnector = new SelectChannelConnector();
            selectConnector.setPort(httpPort);
            selectConnector.setSoLingerTime(-1);
            server.setConnectors(new Connector[]{selectConnector});
        }
    }

    private void initializeThreadPool() {
        BoundedThreadPool threadPool = new BoundedThreadPool();
        threadPool.setMaxThreads(100);
        server.setThreadPool(threadPool);
    }

    private void initializeRootContextHandler() {
        // setup the root context handler which dispatches to other contexts based on the servlet path
        rootHandler = new ContextHandlerCollection();
        server.setHandler(rootHandler);
        ContextHandler contextHandler = new ContextHandler(rootHandler, ROOT);
        SessionHandler sessionHandler = new SessionHandler();
        servletHandler = new ServletHandler();
        sessionHandler.addHandler(servletHandler);
        contextHandler.addHandler(sessionHandler);
    }

}
