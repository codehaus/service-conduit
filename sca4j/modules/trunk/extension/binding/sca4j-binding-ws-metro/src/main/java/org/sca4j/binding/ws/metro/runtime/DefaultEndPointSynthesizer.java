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

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.ws.metro.provision.EndPointPolicy;
import org.sca4j.binding.ws.metro.provision.MetroWireSourceDefinition;
import org.sca4j.binding.ws.metro.runtime.policy.PolicyHelper;
import org.xml.sax.EntityResolver;

import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.binding.BindingImpl;

public class DefaultEndPointSynthesizer implements EndPointSynthesizer {
	private PolicyHelper policyHelper;	

	public DefaultEndPointSynthesizer(@Reference PolicyHelper policyHelper) {
	    super();
	    this.policyHelper = policyHelper;
    }

	@Override
	public WSEndpoint<?> synthesize(Invoker invoker, MetroWireSourceDefinition pwsd) {
		try {
			Class<?> sei = getClass().getClassLoader().loadClass(pwsd.getServiceInterface());

			QName serviceName = getServiceName(sei);
			QName portName = getPortName(sei);
			EndPointPolicy policyDefinition = pwsd.getPolicyDefinition();			
			final BindingID bindingId = policyHelper.getBindingId(policyDefinition);
			final WebServiceFeature[] wsFeatures = policyHelper.getWSFeatures(policyDefinition);
			
			WSBinding binding = BindingImpl.create(bindingId, wsFeatures);
			
			SDDocumentSource primaryWsdl = null; // TODO
			Container container = null; // TODO

			EntityResolver entityresolver = null; // TODO
			boolean isTransportSynchronous = true;
			return WSEndpoint.create(sei, true, invoker, serviceName, portName, container, binding, primaryWsdl, null, entityresolver,
			        isTransportSynchronous);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);// TODO: change with proper exception
		}
	}

	private QName getServiceName(Class<?> sei) {
		final WebService ws = sei.getAnnotation(WebService.class);
		return new QName(ws.targetNamespace(), ws.serviceName());
	}

	private QName getPortName(Class<?> sei) {
		final WebService ws = sei.getAnnotation(WebService.class);
		return new QName(ws.targetNamespace(), ws.portName());
	}
	
	

}
