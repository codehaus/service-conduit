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
package org.sca4j.rs.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.sca4j.rs.provision.RsWireTargetDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
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

    public void attachToTarget(PhysicalWireSourceDefinition source, RsWireTargetDefinition target, Wire wire) throws WiringException {
        
        try {
            URI id = target.getClassLoaderId();
            ClassLoader classLoader = getClass().getClassLoader();
            final Class<?> referenceClass = classLoader.loadClass(target.getInterfaze());
            Path servicePath = referenceClass.getAnnotation(Path.class);
            for (Method method : referenceClass.getDeclaredMethods()) {
                String operationName = method.getName();
                InvocationChain invocationChain = null;
                for (Map.Entry<PhysicalOperationPair, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
                    if (entry.getKey().getSourceOperation().getName().equals(operationName)) {
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
