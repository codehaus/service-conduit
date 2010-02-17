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

import java.util.Map;

import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.http.provision.HttpSourceWireDefinition;
import org.sca4j.binding.http.runtime.injection.DataBinder;
import org.sca4j.binding.http.runtime.introspection.DataBinding;
import org.sca4j.binding.http.runtime.introspection.OperationMetadata;
import org.sca4j.binding.http.runtime.introspection.ServiceMetadata;
import org.sca4j.binding.http.runtime.invocation.HttpBindingServlet;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.host.ServletHost;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * HTTP source wire attacher.
 *
 */
public class HttpSourceWireAttacher extends AbstractWireAttacher implements SourceWireAttacher<HttpSourceWireDefinition> {
    
    @Reference protected ServletHost servletHost;
    @Reference protected Map<DataBinding, DataBinder> inboundBinders;
    @Reference protected Map<DataBinding, DataBinder> outboundBinders;
    
    public void attachToSource(HttpSourceWireDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
        
        Class<?> serviceInterface = getServiceInterface(source.getClassloaderId(), source.getInterfaze());
        ServiceMetadata serviceMetadata = getServiceMetadata(serviceInterface);
        
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
        	OperationMetadata operationMetadata = serviceMetadata.getOperation(entry.getKey().getName());
        	String path = serviceMetadata.getRootResourcePath() + operationMetadata.getSubResourcePath();
        	HttpBindingServlet servlet = new HttpBindingServlet(operationMetadata, entry.getValue(), inboundBinders, outboundBinders, source.isUrlCase());
        	servletHost.registerMapping(path, servlet);
        }
        
    }

    public void detachFromSource(HttpSourceWireDefinition sourceDefsourceinition, PhysicalWireTargetDefinition target, Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachObjectFactory(HttpSourceWireDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition target) {
        throw new UnsupportedOperationException();
    }

}
