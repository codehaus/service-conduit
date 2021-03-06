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
package org.sca4j.binding.http.runtime.introspection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
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
            if (method.getAnnotation(POST.class) != null) {
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
