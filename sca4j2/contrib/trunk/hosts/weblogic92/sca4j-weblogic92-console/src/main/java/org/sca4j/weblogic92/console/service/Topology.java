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

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SCA4J runtime topology for the WLS domain.
 * 
 * @author meerajk
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class Topology {

	@XmlAttribute private String name = "SCA4J Runtime Topology";
	@XmlElement(name = "server") private Set<Server> servers;
	
	/**
	 * Default constructor.
	 */
	public Topology() {
	}

	/**
	 * Initializes the set of servers.
	 * 
	 * @param servers Set of servers in the domain.
	 */
	public Topology(Set<Server> servers) {
		this.servers = servers;
	}

}
