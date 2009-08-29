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
 *
 *
 * ---- Original Codehaus Header ----
 *
 * Copyright (c) 2007 - 2008 fabric3 project contributors
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
 *
 * ---- Original Apache Header ----
 *
 * Copyright (c) 2005 - 2006 The Apache Software Foundation
 *
 * Apache Tuscany is an effort undergoing incubation at The Apache Software
 * Foundation (ASF), sponsored by the Apache Web Services PMC. Incubation is
 * required of all newly accepted projects until a further review indicates that
 * the infrastructure, communications, and decision making process have stabilized
 * in a manner consistent with other successful ASF projects. While incubation
 * status is not necessarily a reflection of the completeness or stability of the
 * code, it does indicate that the project has yet to be fully endorsed by the ASF.
 *
 * This product includes software developed by
 * The Apache Software Foundation (http://www.apache.org/).
 */
package org.sca4j.proxy.jdk;

import java.lang.reflect.Method;
import java.util.Map;

import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.services.proxy.ProxyService;
import org.sca4j.spi.wire.InvocationChain;

/**
 * Returns a proxy instance for a callback wire.
 * 
 * @version $Rev: 3150 $ $Date: 2008-03-21 14:12:51 -0700 (Fri, 21 Mar 2008) $
 */
public class CallbackWireObjectFactory<T> implements ObjectFactory<T> {
	private ScopeContainer<?> container;
	private ProxyService proxyService;
	private Map<String, Map<Method, InvocationChain>> mappings;
	private Class<T> interfaze;

	/**
	 * Constructor.
	 * 
	 * @param interfaze the proxy interface
	 * @param container the scope container of the component implementation the proxy will be injected on
	 * @param proxyService  the service for creating proxies
	 * @param mappings the callback URI to invocation chain mappings
	 */
	public CallbackWireObjectFactory(Class<T> interfaze, ScopeContainer<?> container, ProxyService proxyService, Map<String, Map<Method, InvocationChain>> mappings) {
		this.interfaze = interfaze;
		this.container = container;
		this.proxyService = proxyService;
		this.mappings = mappings;
	}

	/**
	 * Return the correct instance related to the wire Object Factory
	 */
	public T getInstance() throws ObjectCreationException {
		if (Scope.COMPOSITE.equals(container.getScope())) {
			return interfaze.cast(proxyService.createCallbackProxy(interfaze,mappings));
		} else {
			CallFrame frame = PojoWorkContextTunnel.getThreadWorkContext().peekCallFrame();
			String callbackUri = frame.getCallbackUri();
			assert callbackUri != null;
			Map<Method, InvocationChain> mapping = mappings.get(callbackUri);
			assert mapping != null;
			return interfaze.cast(proxyService.createStatefullCallbackProxy(interfaze, mapping, container));
		}
	}

	/**
	 * Return the interface
	 */
	public Class<T> getInterface() {
		return interfaze;
	}

	/**
	 * Updates the method and invocation chain
	 * @param callbackUri
	 * @param chains
	 */
	public void updateMappings(String callbackUri, Map<Method, InvocationChain> chains) {
		mappings.put(callbackUri, chains);
	}

}
