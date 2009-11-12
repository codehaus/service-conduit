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
package org.sca4j.binding.ws.axis2.runtime;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

import org.apache.axis2.client.Options;
import org.apache.axis2.description.AxisService;
import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.ws.axis2.introspection.JaxbMethodInfo;
import org.sca4j.binding.ws.axis2.runtime.config.SCA4JConfigurator;
import org.sca4j.binding.ws.axis2.runtime.jaxb.JaxbInterceptor;
import org.sca4j.binding.ws.axis2.runtime.policy.PolicyApplier;
import org.sca4j.spi.binding.BindingProxyProvider;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;

/**
 * @author meerajk
 *
 */
public class Axis2BindingProxyProvider implements BindingProxyProvider {
    
    @Reference public SCA4JConfigurator configurator;
    @Reference public PolicyApplier policyApplier;

    /**
     * @see org.sca4j.spi.binding.BindingProxyProvider#getBinding(java.lang.Class, java.net.URI, javax.xml.namespace.QName[])
     */
    public <T> T getBinding(Class<T> endpointInterface, URI endpoint, QName... intents) {
        InvocationHandler invocationHandler = new AxisInvocationHandler(endpoint);
        return endpointInterface.cast(Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {endpointInterface}, invocationHandler));
    }
    
    private class AxisInvocationHandler implements InvocationHandler {
        
        private List<String> endpointUris;
        
        private AxisInvocationHandler(URI endpointUri) {
            this.endpointUris = Collections.singletonList(endpointUri.toString());
        }

        public Object invoke(Object target, Method method, Object[] parameters) throws Throwable {
            
            JaxbMethodInfo jaxbMethodInfo = new JaxbMethodInfo(method);
            List<Class<?>> jaxbClasses = jaxbMethodInfo.getJaxbClasses();
            JaxbInterceptor jaxbInterceptor = null;
            
            if (jaxbClasses.size() > 0) {
                JAXBContext context = JAXBContext.newInstance(jaxbClasses.toArray(new Class<?>[jaxbClasses.size()]));
                jaxbInterceptor = new JaxbInterceptor(context, false, jaxbMethodInfo.getFaultConstructors(), method, true);
            } else {
                jaxbInterceptor = new JaxbInterceptor(null, false, null, method, false);
            }
            
            URL wsdlUrl = new URL(endpointUris.get(0).toString() + "?WSDL");
            AxisService axisService = AxisService.createClientSideAxisService(wsdlUrl, null, null, new Options());
            jaxbInterceptor.setNext(new Axis2TargetInterceptor(endpointUris, method.getName(), null, null, null, configurator, policyApplier, axisService));
            Message message = new MessageImpl(parameters, false, new WorkContext());
            
            return jaxbInterceptor.invoke(message).getBody();
            
        }
        
    }

}
