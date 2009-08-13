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
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.QueryParam;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * Default implementation of the domain topology service.
 *
 * @author meerajk
 *
 */
@Path("/console")
public class DefaultDomainTopologyService implements DomainTopologyService {

	private JmxConnectionService jmxConnectionService;
	private String domainServer;
	private String runtimeServer;
	private ObjectName domainRuntimeService;

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
    @GET
    @ProduceMime("application/xml")
	public Topology getDomainTopology(@QueryParam("url") String url,
									  @QueryParam("port") int port,
									  @QueryParam("user") String user,
									  @QueryParam("password") String password) throws IOException, JMException {

		JMXConnector connector = jmxConnectionService.getConnector(url, port, domainServer, user, password);

		try {

			MBeanServerConnection connection = connector.getMBeanServerConnection();

			ObjectName[] serverNames = (ObjectName[]) connection.getAttribute(domainRuntimeService, "ServerRuntimes");
			Set<Server> servers = new HashSet<Server>();

			for (int i = 0;i < serverNames.length;i++) {

				ObjectName serverName = serverNames[i];

				String name = (String) connection.getAttribute(serverName, "Name");
				int listenPort = (Integer) connection.getAttribute(serverName, "ListenPort");
				String listenAddress = (String) connection.getAttribute(serverName, "ListenAddress");
				StringTokenizer strTok = new StringTokenizer(listenAddress,"/",false);
				listenAddress = strTok.nextToken();
				String state = (String) connection.getAttribute(serverName, "State");

				Set<F3Runtime> f3Runtimes = getF3Runtimes(listenAddress, listenPort, user, password);

				servers.add(new Server(name, listenPort, listenAddress, state, f3Runtimes));

			}

			return new Topology(servers);

		} finally {
			connector.close();
		}

	}

	/**
	 * Sets the JMX connection service.
	 *
	 * @param jmxConnectionService JMX connection service.
	 */
	@Reference
	public void setJmxConnectionService(JmxConnectionService jmxConnectionService) {
		this.jmxConnectionService = jmxConnectionService;
	}

	/**
	 * Sets the domain MBean server name.
	 *
	 * @param domainServer Domain MBean server name.
	 */
	@Property
	public void setDomainServer(String domainServer) {
		this.domainServer = domainServer;
	}

	/**
	 * Sets the runtime MBean server name.
	 *
	 * @param runtimeServer Runtime MBean server name.
	 */
	@Property
	public void setRuntimeServer(String runtimeServer) {
		this.runtimeServer = runtimeServer;
	}

	/**
	 * Sets the domain runtime service MBean name.
	 *
	 * @param domainRuntimeService Domain runtime service MBean name.
	 * @throws MalformedObjectNameException If the object name is not valid.
	 */
	@Property
	public void setDomainRuntimeService(String domainRuntimeService) throws MalformedObjectNameException {
		this.domainRuntimeService = new ObjectName(domainRuntimeService);
	}

	/*
	 * Gets the configured runtime on a managed server.
	 */
	private Set<F3Runtime> getF3Runtimes(String url, int port, String user, String password) throws IOException, JMException {

		JMXConnector connector = jmxConnectionService.getConnector(url, port, runtimeServer, user, password);

		try {

			MBeanServerConnection con = connector.getMBeanServerConnection();
			Set<?> objectInstances = con.queryMBeans(new ObjectName("f3-management:*"), null);

			Set<F3Runtime> f3Runtimes = new HashSet<F3Runtime>();

			for (Object obj : objectInstances) {
				ObjectInstance objectInstance = (ObjectInstance) obj;
				ObjectName objectName = objectInstance.getObjectName();
				String subDomain = objectName.getKeyProperty("SubDomain");
				f3Runtimes.add(new F3Runtime(subDomain));
			}

			return f3Runtimes;

		} finally {
			connector.close();
		}

	}

}
