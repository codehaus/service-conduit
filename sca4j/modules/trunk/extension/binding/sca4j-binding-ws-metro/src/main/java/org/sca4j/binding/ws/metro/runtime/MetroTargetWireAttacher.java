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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;

import org.oasisopen.sca.ServiceUnavailableException;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.ws.metro.provision.EndPointPolicy;
import org.sca4j.binding.ws.metro.provision.MetroWireTargetDefinition;
import org.sca4j.binding.ws.metro.runtime.policy.PolicyHelper;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

@EagerInit
public class MetroTargetWireAttacher implements TargetWireAttacher<MetroWireTargetDefinition> {
	private PolicyHelper policyHelper;

	public MetroTargetWireAttacher(@Reference PolicyHelper policyHelper) {
		this.policyHelper = policyHelper;
	}

	public void attachToTarget(PhysicalWireSourceDefinition source, MetroWireTargetDefinition target, Wire wire) throws WiringException {

		try {
			Class<?> sei = Class.forName(target.getReferenceInterface());
			QName serviceName = getServiceName(sei);
			QName portName = getPortName(sei);
			final URL wsdlUrl = getClass().getClassLoader().getResource(target.getWsdlLocation());
			String endpointUri = URLDecoder.decode(target.getUri().toASCIIString(), "UTF-8");
			final EndPointPolicy policyDefinition = target.getPolicyDefinition();
			final WebServiceFeature[] wsFeatures = policyHelper.getWSFeatures(policyDefinition);
			Service service = Service.create(wsdlUrl, serviceName);
			Object wsPort = service.getPort(portName, sei, wsFeatures);
			BindingProvider.class.cast(wsPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUri);

			for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
				Method wsOperation = getWsOperation(sei, entry.getKey().getTargetOperation().getName());
				entry.getValue().addInterceptor(createTargetInterceptor(wsOperation, wsPort));
			}

		} catch (ClassNotFoundException e) {
			throw new WiringException(e);

		} catch (UnsupportedEncodingException e) {
			throw new WiringException(e);
		}
	}

	public ObjectFactory<?> createObjectFactory(MetroWireTargetDefinition target) throws WiringException {
		throw new AssertionError();
	}

	private Interceptor createTargetInterceptor(final Method wsOperation, final Object wsPort) {
		return new Interceptor() {
			private Interceptor next;

			public Interceptor getNext() {
				return next;
			}

			public Message invoke(Message msg) {
				Object[] wsParameters = (Object[]) msg.getBody();
				try {
					Object wsResponse = wsOperation.invoke(wsPort, wsParameters);
					return new MessageImpl(wsResponse, false, null);

				} catch (InvocationTargetException e) {
					return new MessageImpl(e.getTargetException(), true, null);

				} catch (IllegalArgumentException e) {
					throw new ServiceUnavailableException(e);

				} catch (IllegalAccessException e) {
					throw new ServiceUnavailableException(e);
				}
			}

			public void setNext(Interceptor next) {
				this.next = next;

			}

		};
	}

	private Method getWsOperation(Class<?> sei, String methodName) {
		for (Method method : sei.getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;

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
