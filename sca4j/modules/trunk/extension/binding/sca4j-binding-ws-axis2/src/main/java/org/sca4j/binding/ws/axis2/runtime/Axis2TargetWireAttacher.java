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
 * Original Codehaus Header
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
 * Original Apache Header
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
package org.sca4j.binding.ws.axis2.runtime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.description.AxisService;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.ws.axis2.provision.Axis2WireTargetDefinition;
import org.sca4j.binding.ws.axis2.provision.AxisPolicy;
import org.sca4j.binding.ws.axis2.runtime.config.SCA4JConfigurator;
import org.sca4j.binding.ws.axis2.runtime.policy.PolicyApplier;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * @version $Revision$ $Date$
 *          <p/>
 *
 */
@EagerInit
public class Axis2TargetWireAttacher implements TargetWireAttacher<Axis2WireTargetDefinition> {
    private final PolicyApplier policyApplier;
    private final SCA4JConfigurator f3Configurator;

    public Axis2TargetWireAttacher(@Reference PolicyApplier policyApplier,
                                   @Reference SCA4JConfigurator f3Configurator) {
        this.policyApplier = policyApplier;
        this.f3Configurator = f3Configurator;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, Axis2WireTargetDefinition target, Wire wire) throws WiringException {
        
        try {
        
            List<String> endpointUris = new LinkedList<String>();
            String endpointUri = URLDecoder.decode(target.getUri().toASCIIString(), "UTF-8");
            StringTokenizer tok = new StringTokenizer(endpointUri);
            while (tok.hasMoreElements()) {
                endpointUris.add(tok.nextToken().trim());
            }
            
            AxisService axisService = createAxisClientService(target);
            for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
    
                String operation = entry.getKey().getName();
    
                Set<AxisPolicy> policies = target.getPolicies(operation);
                Map<String, String> opInfo = target.getOperationInfo() != null ? target.getOperationInfo().get(operation) : null;
                
                Interceptor interceptor = new Axis2TargetInterceptor(endpointUris, operation, policies, opInfo, target.getConfig(), f3Configurator, policyApplier, axisService);
                entry.getValue().addInterceptor(interceptor);
            }
            
        } catch (UnsupportedEncodingException e) {
            throw new WiringException(e);
        }

    }

    public ObjectFactory<?> createObjectFactory(Axis2WireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }
    
    private URL getWsdlURL(String wsdlLocation, URI classLoaderId) {
        if (wsdlLocation == null) {
            return null;
        }        
        try {
            return new URL(wsdlLocation);
        } catch (MalformedURLException e) {
            return getClass().getClassLoader().getResource(wsdlLocation);
        }        
    }
    
    /*
     * Create instance of client side Axis2 service to get info about the Webservice
     */
    private AxisService createAxisClientService(Axis2WireTargetDefinition target) throws WiringException{
        
        URL wsdlURL = getWsdlURL(target.getWsdlLocation(), target.getClassloaderURI());    
        if(wsdlURL != null) {
            try {
                return AxisService.createClientSideAxisService(wsdlURL,
                                                               target.getWsdlElement().getServiceName(),
                                                               target.getWsdlElement().getPortName().getLocalPart(),
                                                               new Options());
            } catch (AxisFault e) {
                throw new WiringException(e);
            }
        } else {
            return null;
        }
    }
}
