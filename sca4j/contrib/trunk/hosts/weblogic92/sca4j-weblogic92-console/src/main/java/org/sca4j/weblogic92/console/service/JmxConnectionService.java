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

package org.sca4j.weblogic92.console.service;

import java.io.IOException;

import javax.management.remote.JMXConnector;

/**
 * Service for getting managed JMX connections.
 * 
 * @author meerajk
 *
 */
public interface JmxConnectionService {
	
	/**
	 * Gets a remote JMX connector.
	 * 
	 * @param url URL for the remote server.
	 * @param port Port for the remote server.
	 * @param mbeanServer JNDI name of the MBean server.
	 * @param user User to connect to the server.
	 * @param password Password to connect to the server.
	 * 
	 * @return An instance of the remote JMX connector.
	 * @throws IOException If unable to establish a connection.
	 */
	JMXConnector getConnector(String url, int port, String mbeanServer, String user, String password) throws IOException;

}
