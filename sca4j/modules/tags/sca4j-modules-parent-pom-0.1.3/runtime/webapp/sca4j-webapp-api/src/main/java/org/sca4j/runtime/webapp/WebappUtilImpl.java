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
package org.sca4j.runtime.webapp;

import static org.sca4j.runtime.webapp.Constants.APPLICATION_SCDL_PATH_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.APPLICATION_SCDL_PATH_PARAM;
import static org.sca4j.runtime.webapp.Constants.BOOTSTRAP_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.BOOTSTRAP_PARAM;
import static org.sca4j.runtime.webapp.Constants.COORDINATOR_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.COORDINATOR_PARAM;
import static org.sca4j.runtime.webapp.Constants.INTENTS_PATH_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.INTENTS_PATH_PARAM;
import static org.sca4j.runtime.webapp.Constants.LOG_FORMATTER_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.LOG_FORMATTER_PARAM;
import static org.sca4j.runtime.webapp.Constants.MONITOR_FACTORY_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.MONITOR_FACTORY_PARAM;
import static org.sca4j.runtime.webapp.Constants.RUNTIME_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.RUNTIME_PARAM;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_CONFIG_PARAM;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_MONITORING_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_MONITORING_PARAM;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_SCDL_PATH_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_SCDL_PATH_PARAM;

import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import javax.management.MBeanServer;
import javax.servlet.ServletContext;

import org.sca4j.host.runtime.Bootstrapper;
import org.sca4j.host.runtime.RuntimeLifecycleCoordinator;
import org.sca4j.host.runtime.ScdlBootstrapper;
import org.sca4j.jmx.agent.DefaultAgent;
import org.sca4j.monitor.MonitorFactory;
import org.xml.sax.InputSource;

/**
 * @version $Rev: 5395 $ $Date: 2008-09-13 14:35:47 +0100 (Sat, 13 Sep 2008) $
 */
public class WebappUtilImpl implements WebappUtil {

    private static final String SYSTEM_CONFIG = "/WEB-INF/systemConfig.xml";

    private final ServletContext servletContext;

    public WebappUtilImpl(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public WebappRuntime getRuntime(ClassLoader bootClassLoader) throws SCA4JInitException {

        WebappRuntime runtime = createRuntime(bootClassLoader);

        MonitorFactory factory = createMonitorFactory(bootClassLoader);
        MBeanServer mBeanServer = createMBeanServer();


        runtime.setMonitorFactory(factory);
        runtime.setMBeanServer(mBeanServer);

        return runtime;

    }

    public ScdlBootstrapper getBootstrapper(ClassLoader bootClassLoader) throws SCA4JInitException {

        try {

            String className = getInitParameter(BOOTSTRAP_PARAM, BOOTSTRAP_DEFAULT);
            ScdlBootstrapper scdlBootstrapper = (ScdlBootstrapper) bootClassLoader.loadClass(className).newInstance();
            
            String systemConfig = getInitParameter(SYSTEM_CONFIG_PARAM, null);
            if (systemConfig != null) {
                Reader reader = new StringReader(systemConfig);
                InputSource source = new InputSource(reader);
                scdlBootstrapper.setSystemConfig(source);
            } else {
                scdlBootstrapper.setSystemConfig(servletContext.getResource(SYSTEM_CONFIG));
            }

            return scdlBootstrapper;

        } catch (InstantiationException e) {
            throw new SCA4JInitException(e);
        } catch (IllegalAccessException e) {
            throw new SCA4JInitException(e);
        } catch (ClassNotFoundException e) {
            throw new SCA4JInitException("Bootstrapper Implementation not found", e);
        } catch (MalformedURLException e) {
            throw new SCA4JInitException(e);
        }

    }

    @SuppressWarnings({"unchecked"})
    public RuntimeLifecycleCoordinator<WebappRuntime, Bootstrapper> getCoordinator(ClassLoader bootClassLoader) throws SCA4JInitException {

        try {

            String className = getInitParameter(COORDINATOR_PARAM, COORDINATOR_DEFAULT);
            return (RuntimeLifecycleCoordinator<WebappRuntime, Bootstrapper>) bootClassLoader.loadClass(className).newInstance();

        } catch (InstantiationException e) {
            throw new SCA4JInitException(e);
        } catch (IllegalAccessException e) {
            throw new SCA4JInitException(e);
        } catch (ClassNotFoundException e) {
            throw new SCA4JInitException("Bootstrapper Implementation not found", e);
        }

    }

    public URL getSystemScdl(ClassLoader bootClassLoader) throws InvalidResourcePath {

        String path = getInitParameter(SYSTEM_SCDL_PATH_PARAM, SYSTEM_SCDL_PATH_DEFAULT);
        try {
            return convertToURL(path, bootClassLoader);
        } catch (MalformedURLException e) {
            throw new InvalidResourcePath(SYSTEM_SCDL_PATH_PARAM, path, e);
        }

    }

    public URL getIntentsLocation(ClassLoader bootClassLoader) throws InvalidResourcePath {

        String path = getInitParameter(INTENTS_PATH_PARAM, INTENTS_PATH_DEFAULT);
        try {
            return convertToURL(path, bootClassLoader);
        } catch (MalformedURLException e) {
            throw new InvalidResourcePath(SYSTEM_SCDL_PATH_PARAM, path, e);
        }

    }

    public String getApplicationName() {

        String name = servletContext.getServletContextName();
        if (name == null) {
            name = "application";
        }
        return name;

    }

    public URL getApplicationScdl(ClassLoader bootClassLoader) throws InvalidResourcePath {

        String path = getInitParameter(APPLICATION_SCDL_PATH_PARAM, APPLICATION_SCDL_PATH_DEFAULT);
        try {
            return convertToURL(path, bootClassLoader);
        } catch (MalformedURLException e) {
            throw new InvalidResourcePath(APPLICATION_SCDL_PATH_PARAM, path, e);
        }

    }

    public URL convertToURL(String path, ClassLoader classLoader) throws MalformedURLException {

        URL ret = null;
        if (path.charAt(0) == '/') {
            // user supplied an absolute path - look up as a webapp resource
            ret = servletContext.getResource(path);
        }
        if (ret == null) {
            // user supplied a relative path - look up as a boot classpath resource
            ret = classLoader.getResource(path);
        }
        return ret;

    }

    public String getInitParameter(String name, String value) {

        String result = servletContext.getInitParameter(name);
        if (result != null && result.length() != 0) {
            return result;
        }
        return value;

    }
    
    /**
     * Extension point for creating the MBean server.
     * 
     * @return MBean server.
	 * @throws SCA4JInitException If unable to initialize the MBean server.
     */
    protected MBeanServer createMBeanServer() throws SCA4JInitException {
    	return new DefaultAgent().getMBeanServer();
    }

	/**
	 * Extension point for creating the runtime.
	 * 
	 * @param bootClassLoader Classloader for loading the runtime class.
	 * @return Webapp runtime instance.
	 * @throws SCA4JInitException If unable to initialize the runtime.
	 */
	protected WebappRuntime createRuntime(ClassLoader bootClassLoader) throws SCA4JInitException {

        try {
			String className = getInitParameter(RUNTIME_PARAM, RUNTIME_DEFAULT);
			return (WebappRuntime) bootClassLoader.loadClass(className).newInstance();
        } catch (InstantiationException e) {
            throw new SCA4JInitException(e);
        } catch (IllegalAccessException e) {
            throw new SCA4JInitException(e);
        } catch (ClassNotFoundException e) {
            throw new SCA4JInitException("Runtime Implementation not found", e);
        }
        
	}

	/**
	 * Extension point for creating the monitor factory.
	 * 
	 * @param bootClassLoader Classloader for loading the monitor factory class.
	 * @return Monitor factory instance.
	 * @throws SCA4JInitException If unable to initialize the monitor factory.
	 */
	protected MonitorFactory createMonitorFactory(ClassLoader bootClassLoader) throws SCA4JInitException {

        try {
        	
			String monitorFactoryClass = getInitParameter(MONITOR_FACTORY_PARAM, MONITOR_FACTORY_DEFAULT);
			MonitorFactory factory = (MonitorFactory) bootClassLoader.loadClass(monitorFactoryClass).newInstance();
	
			String level = getInitParameter(SYSTEM_MONITORING_PARAM, SYSTEM_MONITORING_DEFAULT);
			factory.setDefaultLevel(Level.parse(level));
			factory.setBundleName("sca4j");
			String formatterClass = getInitParameter(LOG_FORMATTER_PARAM, LOG_FORMATTER_DEFAULT);
			Properties configuration = new Properties();
			configuration.setProperty("sca4j.jdkLogFormatter", formatterClass);
			factory.setConfiguration(configuration);
		
			return factory;

        } catch (InstantiationException e) {
            throw new SCA4JInitException(e);
        } catch (IllegalAccessException e) {
            throw new SCA4JInitException(e);
        } catch (ClassNotFoundException e) {
            throw new SCA4JInitException("Monitor factory Implementation not found", e);
        }
        
	}
	
}
