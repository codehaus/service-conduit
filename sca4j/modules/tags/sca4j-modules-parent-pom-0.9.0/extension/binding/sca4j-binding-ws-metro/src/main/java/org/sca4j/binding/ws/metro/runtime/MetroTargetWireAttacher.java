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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.ws.Service;

import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.annotations.EagerInit;
import org.sca4j.binding.ws.metro.provision.MetroWireTargetDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

@EagerInit
public class MetroTargetWireAttacher implements TargetWireAttacher<MetroWireTargetDefinition> {
    
    public MetroTargetWireAttacher() {
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, MetroWireTargetDefinition target, Wire wire) throws WiringException {
        
        try {
            URL endpointUrl = target.getUri().toURL();
            Service service = Service.create(endpointUrl, target.getWsdlElement().getServiceName());
            Class<?> serviceEndpointInterface = Class.forName(target.getReferenceInterface());
            Object wsPort = service.getPort(serviceEndpointInterface);
            
            for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
        	
        	Method wsOperation = getWsOperation(serviceEndpointInterface, entry.getKey().getName());        	
		entry.getValue().addInterceptor(createTargetInterceptor(wsOperation, wsPort));
            }
            
        } catch (ClassNotFoundException e) {
            throw new WiringException(e);
            
	} catch (MalformedURLException e) {
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
    
    private Method getWsOperation(Class<?> serviceEndpointInterface, String methodName) {
	for (Method method : serviceEndpointInterface.getMethods()) {
	    if(method.getName().equals(methodName)){
		return method;
	    }
	}
	return null;
	
	
    }
    
}
