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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebFault;

import org.osoa.sca.annotations.Reference;

import org.sca4j.binding.ws.axis2.provision.jaxb.JaxbInterceptorDefinition;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.interceptor.InterceptorBuilder;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

/**
 * @version $Revision$ $Date$
 */
public class JaxbInterceptorBuilder implements InterceptorBuilder<JaxbInterceptorDefinition, JaxbInterceptor> {

    private ClassLoaderRegistry classLoaderRegistry;

    public JaxbInterceptorBuilder(@Reference ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    public JaxbInterceptor build(JaxbInterceptorDefinition definition) throws BuilderException {

        URI classLoaderId = definition.getClassLoaderId();

        ClassLoader classLoader = classLoaderRegistry.getClassLoader(classLoaderId);

        try {
            Set<String> classNames = definition.getClassNames();
            Set<String> faultNames = definition.getFaultNames();
            Map<Class<?>, Constructor<?>> faultMapping = getFaultMapping(classLoader, faultNames);

            JAXBContext context = getJAXBContext(classLoader, classNames);
            return new JaxbInterceptor(classLoader, context, definition.isService(), faultMapping);

        } catch (NoSuchMethodException e) {
            throw new JaxbBuilderException(e);
        } catch (ClassNotFoundException e) {
            throw new JaxbBuilderException(e);
        } catch (JAXBException e) {
            throw new JaxbBuilderException(e);
        }

    }

    private JAXBContext getJAXBContext(ClassLoader classLoader, Set<String> classNames) throws JAXBException, ClassNotFoundException {
        Class<?>[] classes = new Class<?>[classNames.size()];
        int i = 0;
        for (String className : classNames) {
            classes[i++] = classLoaderRegistry.loadClass(classLoader, className);
        }
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            // The JAXBContext searches the TCCL for the JAXB-RI. Set the TCCL to the Axis classloader (which loaded this class), as it has 
            // visibility to the JAXB RI.
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return JAXBContext.newInstance(classes);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }

    private Map<Class<?>, Constructor<?>> getFaultMapping(ClassLoader classLoader, Set<String> faultNames)
            throws ClassNotFoundException, NoSuchMethodException {
        Map<Class<?>, Constructor<?>> mapping = new HashMap<Class<?>, Constructor<?>>(faultNames.size());
        for (String faultName : faultNames) {
            Class<?> clazz = classLoaderRegistry.loadClass(classLoader, faultName);
            WebFault fault = clazz.getAnnotation(WebFault.class);
            if (fault == null) {
                // FIXME throw someting
                throw new RuntimeException();
            }
            Method getFaultInfo = clazz.getMethod("getFaultInfo");
            Class<?> faultType = getFaultInfo.getReturnType();
            Constructor<?> constructor = clazz.getConstructor(String.class, faultType);
            mapping.put(faultType, constructor);
        }
        return mapping;
    }
}
