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
package org.sca4j.binding.http.runtime.introspection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.sca4j.spi.builder.WiringException;

/**
 * Default implementation of the service introspector.
 */
public class DefaultServiceIntrospector implements ServiceIntrospector {

    public ServiceMetadata introspect(Class<?> serviceInterface) throws WiringException {
        
        Path rootResourcePath = serviceInterface.getAnnotation(Path.class);
        if (rootResourcePath == null) {
            throw new WiringException("No root resource path on interface " + serviceInterface);
        }
        
        Map<String, OperationMetadata> operations = new HashMap<String, OperationMetadata>();
        for (Method method : serviceInterface.getDeclaredMethods()) {
            
            // TODO Add support for JAXB
            DataBinding inBinding = DataBinding.JAXRS;
            DataBinding outBinding = DataBinding.JAXRS;
            
            HttpMethod subResourceMethod = HttpMethod.GET;
            if (method.getAnnotation(PUT.class) != null) {
                subResourceMethod = HttpMethod.POST;
            }
            
            Path subResourcePath = method.getAnnotation(Path.class);
            if (subResourcePath == null) {
                throw new WiringException("Sub resource path not specified for " + method);
            }

            
            List<ParameterMetadata> parameters = new LinkedList<ParameterMetadata>();
            int index = 0;
            for (Annotation[] annotations : method.getParameterAnnotations()) {
                Annotation annotation = null;
                if (annotations.length == 1) {
                    annotation = annotations[0];
                }
                Class<?> type = method.getParameterTypes()[index++];
                parameters.add(new ParameterMetadata(annotation, type));
            }
            
            operations.put(method.getName(), new OperationMetadata(subResourcePath.value(), subResourceMethod, inBinding, outBinding, parameters));
            
        }
        
        return new ServiceMetadata(rootResourcePath.value(), operations);
        
    }

}
