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
