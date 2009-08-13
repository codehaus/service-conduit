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

import java.net.URI;

import org.osoa.sca.annotations.Reference;
import org.sca4j.binding.http.runtime.introspection.ServiceIntrospector;
import org.sca4j.binding.http.runtime.introspection.ServiceMetadata;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

/**
 * Abstract super class for the source and target wire attacher.
 *
 */
public class AbstractWireAttacher {
    
    @Reference protected ClassLoaderRegistry classLoaderRegistry;
    @Reference protected ServiceIntrospector serviceIntrospector;
    
    protected Class<?> getServiceInterface(URI classloaderId, String interfaceFqn) throws WiringException {
        try {
            return classLoaderRegistry.getClassLoader(classloaderId).loadClass(interfaceFqn);
        } catch (ClassNotFoundException e) {
            throw new WiringException(e);
        }
    }
    
    protected ServiceMetadata getServiceMetadata(Class<?> serviceInterface) throws WiringException {
        return serviceIntrospector.introspect(serviceInterface);
    }
    
}
