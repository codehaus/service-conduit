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

package org.sca4j.binding.ws.mq.scdl;

import java.net.URI;

import javax.xml.namespace.QName;

import org.sca4j.scdl.BindingDefinition;
import org.osoa.sca.Constants;

public class MqBindingDefinition extends BindingDefinition {
	
	private static final long serialVersionUID = 2043799331585239948L;

	private static final QName BINDING_QNAME = new QName(Constants.SCA_NS, "binding.ws.mq");
	private final String destination;
	private final URI host;
	
	/**
	 * Initialize by MQ destination and host
	 * @param destination
	 * @param host
	 */
	public MqBindingDefinition(String destination, URI host) {
		super(BINDING_QNAME);
		this.destination = destination;
		this.host = host;
	}

	/**
	 * Return the queue destination
	 * @return the destination
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Return the host of the MQ server
	 * @return the host
	 */
	public URI getHost() {
		return host;
	}
}
