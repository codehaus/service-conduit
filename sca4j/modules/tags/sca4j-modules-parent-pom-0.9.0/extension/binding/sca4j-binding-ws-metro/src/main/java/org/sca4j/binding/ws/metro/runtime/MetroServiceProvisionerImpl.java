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

import javax.servlet.ServletConfig;
import javax.xml.namespace.QName;

import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.ws.metro.provision.MetroWireSourceDefinition;
import org.sca4j.host.runtime.HostInfo;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.host.ServletHost;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;
import org.xml.sax.EntityResolver;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.Invoker;
import com.sun.xml.ws.api.server.SDDocumentSource;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;

/**
 * @version $Revision$ $Date$
 */
@EagerInit
public class MetroServiceProvisionerImpl implements MetroServiceProvisioner {
    private String servicePath = "metro";
    private final ServletHost servletHost;
    private final HostInfo hostInfo;
    private final SCA4JWSServletDelegate wsServletDelegate;

    public MetroServiceProvisionerImpl(@Reference(required = false) ServletHost servletHost, @Reference HostInfo hostInfo) {
        if (servletHost == null) {
            throw new AssertionError("Please configure a servlet host");
        }
        this.servletHost = servletHost;
        this.hostInfo = hostInfo;
        this.wsServletDelegate = new SCA4JWSServletDelegate(servletHost.getServletContext());
    }

    @Property
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
            Class<?> serviceInterface = Class.forName(pwsd.getServiceInterface());
            
            QName serviceName = null;
            QName portName = null;
            Container container = null;
            WSBinding binding = null;
            SDDocumentSource primaryWsdl = null;
            
            //Create EndPoint
	    EntityResolver entityresolver = null;;
	    boolean isTransportSynchronous = true;
	    WSEndpoint<?> wsEndPoint = WSEndpoint.create(
                    serviceInterface, true,
                    createInvoker(wire),
                    serviceName, portName, container, binding,
                    primaryWsdl, null, entityresolver,isTransportSynchronous
            );
	    
	    wsServletDelegate.registerEndPoint(uri, wsEndPoint);
	    
        } catch (Exception e) {
            throw new WiringException(e);
        }
    }    
    
    private Invoker createInvoker(Wire wire) {
	final Map<String, InvocationChain> interceptors = new HashMap<String, InvocationChain>();
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
            interceptors.put(entry.getKey().getName(), entry.getValue());
        }
	
	return new Invoker() {

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
	};
	
    }
}