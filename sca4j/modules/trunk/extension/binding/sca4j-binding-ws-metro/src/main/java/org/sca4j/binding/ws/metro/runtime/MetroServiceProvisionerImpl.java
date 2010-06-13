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
 */
package org.sca4j.binding.ws.metro.runtime;

import javax.servlet.ServletConfig;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.ws.metro.provision.MetroWireSourceDefinition;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.host.ServletHost;
import org.sca4j.spi.wire.Wire;

import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class MetroServiceProvisionerImpl implements MetroServiceProvisioner {
	private String servicePath = "metro";
	private final ServletHost servletHost;
	private final SCA4JWSServletDelegate wsServletDelegate;
	private final EndPointSynthesizer endPointSynthesizer;

	public MetroServiceProvisionerImpl(@Reference(required = false) ServletHost servletHost, @Reference EndPointSynthesizer endPointSynthesizer) {
		if (servletHost == null) {
			throw new AssertionError("Please configure a servlet host");
		}
		this.servletHost = servletHost;
		this.endPointSynthesizer = endPointSynthesizer;
		this.wsServletDelegate = new SCA4JWSServletDelegate(servletHost.getServletContext());
	}

	@Property(required = false)
	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

	/**
	 * Initializes the servlet mapping.
	 * 
	 * @throws Exception If unable to create configuration context.
	 */
	@SuppressWarnings("serial")
	@Init
	public void start() throws Exception {
		WSServlet metroServlet = new WSServlet() {
			@Override
			protected WSServletDelegate getDelegate(ServletConfig servletConfig) {
				return wsServletDelegate;
			}
		};

		servletHost.registerMapping("/" + servicePath + "/*", metroServlet);
	}

	public void provision(MetroWireSourceDefinition pwsd, Wire wire) throws WiringException {

		try {
			String uri = pwsd.getUri().toASCIIString();
			wsServletDelegate.registerEndPoint(uri, endPointSynthesizer.synthesize(new ServiceInvoker(wire), pwsd));

		} catch (Exception e) {
			throw new WiringException(e);
		}
	}

}