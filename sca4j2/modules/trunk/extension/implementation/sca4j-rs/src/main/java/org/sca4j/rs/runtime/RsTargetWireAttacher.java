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
package org.sca4j.rs.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.osoa.sca.annotations.Reference;
import org.sca4j.rs.provision.RsWireTargetDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * @author meerajk
 *
 */
public class RsTargetWireAttacher implements TargetWireAttacher<RsWireTargetDefinition> {

    private final ClassLoaderRegistry classLoaderRegistry;

    public RsTargetWireAttacher(@Reference ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    public void attachToTarget(PhysicalWireSourceDefinition source, RsWireTargetDefinition target, Wire wire) throws WiringException {
        
        try {
            URI id = target.getClassLoaderId();
            ClassLoader classLoader = classLoaderRegistry.getClassLoader(id);
            final Class<?> referenceClass = classLoader.loadClass(target.getInterfaze());
            Path servicePath = referenceClass.getAnnotation(Path.class);
            for (Method method : referenceClass.getDeclaredMethods()) {
                String operationName = method.getName();
                InvocationChain invocationChain = null;
                for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
                    if (entry.getKey().getName().equals(operationName)) {
                        invocationChain = entry.getValue();
                    }
                }
                if (invocationChain == null) {
                    continue;
                }
                Path operationPath = method.getAnnotation(Path.class);
                Annotation[][] annotations = method.getParameterAnnotations();
                QueryParam[] queryParams = new QueryParam[method.getParameterTypes().length];
                for (int j = 0; j < queryParams.length;j++) {
                    queryParams[j] = (QueryParam) annotations[j][0];
                }
                invocationChain.addInterceptor(new RsInterceptor(servicePath, operationPath, queryParams, target.getUri().toString()));
            }
        } catch (ClassNotFoundException e) {
            throw new WiringException(e);
        }
        
    }
    
    private class RsInterceptor implements Interceptor {

        public RsInterceptor(Path servicePath, Path operationPath, QueryParam[] queryParams, String uri) {
            super();
            this.servicePath = servicePath;
            this.operationPath = operationPath;
            this.queryParams = queryParams;
            this.uri = uri;
        }

        private Interceptor next;
        private Path servicePath;
        private Path operationPath;
        private QueryParam[] queryParams;
        private String uri;
        
        
        
        public Interceptor getNext() {
            // TODO Auto-generated method stub
            return next;
        }

        public Message invoke(Message msg) {
            Object[] args = (Object[]) msg.getBody();
            UriBuilder uriBuilder = UriBuilder.fromPath(uri);
            uriBuilder.path(servicePath.value());
            uriBuilder.path(operationPath.value());
            for (int i = 0;i < queryParams.length;i++) {
                uriBuilder.queryParam(queryParams[i].value(), args[i].toString());
            }
            WebResource resource = Client.create().resource(uriBuilder.build());
            
            msg.setBody(resource.get(String.class));
            return msg;
        }

        public void setNext(Interceptor next) {
            this.next = next;
        }
        
    }

    public ObjectFactory<?> createObjectFactory(final RsWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }

}
