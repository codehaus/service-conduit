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
package org.sca4j.runtime.weblogic92;

import javax.management.MBeanServer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletContext;

import org.sca4j.runtime.webapp.SCA4JInitException;
import org.sca4j.runtime.webapp.WebappUtilImpl;

/**
 * Wires the services provided by Weblogic.
 * 
 * TODO Wire in other facilities like work managers, txm etc.
 */
public class WeblogicUtil extends WebappUtilImpl {

    /**
     * @param servletContext
     */
    public WeblogicUtil(ServletContext servletContext) {
        super(servletContext);
    }
    
    /*
     * Gets the MBean server from Weblogic.
     */
    @Override
    protected MBeanServer createMBeanServer() throws SCA4JInitException {
        
        Context ctx = null;        
        try {
            ctx = new InitialContext();
            Object mbeanServer = ctx.lookup("java:comp/env/jmx/runtime");
            return (MBeanServer) PortableRemoteObject.narrow(mbeanServer, MBeanServer.class);
        } catch (NamingException e) {
            throw new SCA4JInitException(e);
		} finally {
            try {
				ctx.close();
			} catch (NamingException e) {
	            throw new SCA4JInitException(e);
			}
        }
        
    }

}
