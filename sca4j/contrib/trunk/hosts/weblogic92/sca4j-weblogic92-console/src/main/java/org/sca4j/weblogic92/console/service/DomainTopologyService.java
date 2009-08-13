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

import javax.management.JMException;

/**
 * Service for getting the domain topology.
 * 
 * @author meerajk
 *
 */
public interface DomainTopologyService {
	
	/**
	 * Gets the F3 runtime topology for the weblogic domain.
	 * 
	 * @param url Listen address of the admin server.
	 * @param port Listen port of the admin server.
	 * @param user Admin user for the server.
	 * @param password Admin password for the server.
	 * @return List of servers in the domain.
	 * 
	 * @throws IOException If unable to connect to the admin server.
	 * @throws JMException In case of any unexpected JMX exception.
	 */
	Topology getDomainTopology(String url, int port, String user, String password) throws IOException, JMException;

}
