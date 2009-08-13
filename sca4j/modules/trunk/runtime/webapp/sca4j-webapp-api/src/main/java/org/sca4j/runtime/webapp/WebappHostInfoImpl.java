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

import java.io.File;
import java.net.URI;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.sca4j.host.runtime.AbstractHostInfo;

/**
 * @version $Rev: 5139 $ $Date: 2008-08-02 17:18:18 +0100 (Sat, 02 Aug 2008) $
 */
public class WebappHostInfoImpl extends AbstractHostInfo implements WebappHostInfo {
    
    private final ServletContext servletContext;
    private final File baseDir;
    private final File tempDir;

    public WebappHostInfoImpl(ServletContext servletContext, URI domain, File baseDir, File tempDir) {
        super(domain, convert(servletContext));
        this.servletContext = servletContext;
        this.baseDir = baseDir;
        this.tempDir = tempDir;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getInstallDirectory() {
        throw new UnsupportedOperationException();
    }

    public boolean supportsClassLoaderIsolation() {
        return false;
    }

    public File getTempDir() {
        return tempDir;
    }
    
    private static Properties convert(ServletContext servletContext) {
        Properties props = new Properties();
        Enumeration<String> initParameters = servletContext.getInitParameterNames();
        while (initParameters.hasMoreElements()) {
            String name = initParameters.nextElement();
            props.put(name, servletContext.getInitParameter(name));
        }
        return props;
    }
}
