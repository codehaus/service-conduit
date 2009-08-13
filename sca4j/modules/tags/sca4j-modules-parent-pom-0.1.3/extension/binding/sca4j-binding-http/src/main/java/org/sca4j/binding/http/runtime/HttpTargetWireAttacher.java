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
