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
package org.sca4j.binding.http.runtime;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.http.provision.HttpTargetWireDefinition;
import org.sca4j.binding.http.provision.security.AuthenticationPolicy;
import org.sca4j.binding.http.runtime.introspection.OperationMetadata;
import org.sca4j.binding.http.runtime.introspection.ServiceMetadata;
import org.sca4j.binding.http.runtime.invocation.HttpBindingInterceptor;
import org.sca4j.binding.http.runtime.invocation.security.ConnectionProvider;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * HTTP target wire attacher.
 *
 */
public class HttpTargetWireAttacher extends AbstractWireAttacher implements TargetWireAttacher<HttpTargetWireDefinition> {
    
    @Reference protected Map<Class<? extends AuthenticationPolicy>, ConnectionProvider<?>> connectionProviders;

    public void attachToTarget(PhysicalWireSourceDefinition source, HttpTargetWireDefinition target, Wire wire) throws WiringException {
    
        try {
            Class<?> seiClass = getServiceInterface(target.getClassLoaderId(), target.getInterfaze());
            ServiceMetadata serviceMetadata = getServiceMetadata(seiClass);
            URI uri = target.getUri();
            
            for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
                OperationMetadata operationMetadata = serviceMetadata.getOperation(entry.getKey().getName());
                URL url = new URL(uri + serviceMetadata.getRootResourcePath() + operationMetadata.getSubResourcePath());
                HttpBindingInterceptor<?> interceptor = createInterceptor(target.getAuthenticationPolicy(), url, operationMetadata, target.getClassLoaderId());
                entry.getValue().addInterceptor(interceptor);
            }
        } catch (MalformedURLException e) {
            throw new WiringException(e);
        }
    
    }

    public ObjectFactory<?> createObjectFactory(HttpTargetWireDefinition target) throws WiringException {
        throw new UnsupportedOperationException();
    }
    
    @SuppressWarnings("unchecked")
    private <T extends AuthenticationPolicy> HttpBindingInterceptor<T> createInterceptor(T authenticationPolicy, URL url, OperationMetadata operationMetadata, URI classLoaderId) {
        ConnectionProvider<T> connectionProvider = (ConnectionProvider<T>) connectionProviders.get(authenticationPolicy.getClass());
        return new HttpBindingInterceptor<T>(url, operationMetadata, connectionProvider, authenticationPolicy, classLoaderId);
    }

}