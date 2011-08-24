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
package org.sca4j.binding.ws.axis2.runtime.jaxb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.sca4j.binding.ws.axis2.introspection.JaxbMethodInfo;
import org.sca4j.binding.ws.axis2.provision.jaxb.JaxbInterceptorDefinition;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.interceptor.InterceptorBuilder;

/**
 * @version $Revision$ $Date$
 */
public class JaxbInterceptorBuilder implements InterceptorBuilder<JaxbInterceptorDefinition, JaxbInterceptor> {

    private static ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<String, CacheEntry>(); 
    
    public JaxbInterceptor build(JaxbInterceptorDefinition definition) throws BuilderException {

        try {
            
            String key = definition.getInterfaze() + definition.getOperation();
            CacheEntry cacheEntry = cache.get(key);
            if (cacheEntry == null) {
                cacheEntry = createCacheEntry(definition);
                cache.putIfAbsent(key, cacheEntry);
            }
            
            return new JaxbInterceptor(cacheEntry.context, definition.isService(), cacheEntry.faultConstructors, cacheEntry.method, cacheEntry.isJaxb);
        } catch (ClassNotFoundException e) {
            throw new JaxbBuilderException(e);
        } catch (JAXBException e) {
            throw new JaxbBuilderException(e);
        }

    }
    
    private CacheEntry createCacheEntry(JaxbInterceptorDefinition definition) throws ClassNotFoundException, JAXBException {
        
        ClassLoader classLoader = getClass().getClassLoader();
        
        Class<?> interfaceClass = classLoader.loadClass(definition.getInterfaze());
        Method interceptedMethod = null;
        for (Method method : interfaceClass.getDeclaredMethods()) {
            if (definition.getOperation().equals(method.getName())) {
                interceptedMethod = method;
            }
        }
        
        JaxbMethodInfo jaxbMethodInfo = new JaxbMethodInfo(interceptedMethod);
        List<Class<?>> jaxbClasses = jaxbMethodInfo.getJaxbClasses();

        CacheEntry result = new CacheEntry();
        result.method = interceptedMethod;
        if (jaxbClasses.size() > 0) {
            //The JAXBContext is thread safe. 
            result.context = JAXBContext.newInstance(jaxbClasses.toArray(new Class<?>[jaxbClasses.size()]));
            result.faultConstructors = jaxbMethodInfo.getFaultConstructors();
            result.isJaxb = true;
        }
         
        return result;
    }

    private static class CacheEntry {
        JAXBContext context;
        Map<Class<?>, Constructor<?>> faultConstructors;
        Method method;
        boolean isJaxb;
    }
    
}
