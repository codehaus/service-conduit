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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.oasisopen.sca.ServiceUnavailableException;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;

public class ServiceInvoker extends Invoker {
	final Map<String, InvocationChain> interceptors;

	public ServiceInvoker(Wire wire) {
		interceptors = new HashMap<String, InvocationChain>();
		for (Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
			interceptors.put(entry.getKey().getSourceOperation().getName(), entry.getValue());
		}
	}

	@Override
	public Object invoke(Packet p, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
		Interceptor headInterceptor = interceptors.get(method.getName()).getHeadInterceptor();
		Message input = new MessageImpl(args, false, new WorkContext());
		Message ret = headInterceptor.invoke(input);
		if (ret.isFault()) {
			throw new ServiceUnavailableException(Throwable.class.cast(ret.getBody()));
		}

		return ret.getBody();
	}

	@SuppressWarnings("unchecked")
    @Override
    public void start(WSWebServiceContext wsc, WSEndpoint endpoint) {		
    }
}
