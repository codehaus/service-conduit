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
        	HttpBindingServlet servlet = new HttpBindingServlet(operationMetadata, entry.getValue(), inboundBinders, outboundBinders);
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
