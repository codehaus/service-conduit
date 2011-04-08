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
import static org.sca4j.runtime.webapp.Constants.DOMAIN_PARAM;
import static org.sca4j.runtime.webapp.Constants.INTENTS_PATH_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.INTENTS_PATH_PARAM;
import static org.sca4j.runtime.webapp.Constants.LOG_FORMATTER_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.LOG_FORMATTER_PARAM;
import static org.sca4j.runtime.webapp.Constants.MONITOR_FACTORY_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.MONITOR_FACTORY_PARAM;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_MONITORING_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_MONITORING_PARAM;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_SCDL_PATH_DEFAULT;
import static org.sca4j.runtime.webapp.Constants.SYSTEM_SCDL_PATH_PARAM;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.ServletContext;

import org.sca4j.monitor.MonitorFactory;

/**
 * @version $Rev: 5395 $ $Date: 2008-09-13 14:35:47 +0100 (Sat, 13 Sep 2008) $
 */
public class WebappUtilImpl implements WebappUtil {

    private final ServletContext servletContext;

    public WebappUtilImpl(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public WebappRuntime getRuntime(ClassLoader webappClassLoader) throws SCA4JInitException {

        try {
    
            URI domain = new URI(getInitParameter(DOMAIN_PARAM, "sca4j://domain"));
            File tempDir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"), ".sca4j");
            }
            WebappHostInfo info = new WebappHostInfoImpl(servletContext, domain);
    
            WebappRuntime runtime = (WebappRuntime) webappClassLoader.loadClass("org.sca4j.runtime.webapp.WebappRuntimeImpl").newInstance();
    
            MonitorFactory factory = createMonitorFactory(webappClassLoader);
    
            runtime.setMonitorFactory(factory);
            runtime.setHostInfo(info);
    
            return runtime;

        } catch (URISyntaxException e) {
            throw new SCA4JInitException(e);
        } catch (InstantiationException e) {
            throw new SCA4JInitException(e);
        } catch (IllegalAccessException e) {
            throw new SCA4JInitException(e);
        } catch (ClassNotFoundException e) {
            throw new SCA4JInitException(e);
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
	 * Extension point for creating the monitor factory.
	 * 
	 * @param bootClassLoader Classloader for loading the monitor factory class.
	 * @return Monitor factory instance.
	 * @throws SCA4JInitException If unable to initialize the monitor factory.
	 */
	private MonitorFactory createMonitorFactory(ClassLoader bootClassLoader) throws SCA4JInitException {

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
