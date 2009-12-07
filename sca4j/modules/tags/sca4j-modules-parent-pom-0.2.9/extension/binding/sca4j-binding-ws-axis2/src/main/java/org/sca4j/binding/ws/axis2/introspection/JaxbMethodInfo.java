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
 */
package org.sca4j.binding.ws.axis2.introspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.ws.WebFault;

/**
 * @author meerajk
 *
 */
public class JaxbMethodInfo {
    
    private List<Class<?>> parameterTypes = new LinkedList<Class<?>>();
    private Class<?> returnType;
    private List<Class<?>> faultInfos = new LinkedList<Class<?>>();
    private List<Class<?>> faults = new LinkedList<Class<?>>();
    private Map<Class<?>, Constructor<?>> faultConstructors = new HashMap<Class<?>, Constructor<?>>();
    
    /**
     * @param method
     */
    public JaxbMethodInfo(Method method) {
        
        try {
            
            for (Class<?> paramType : method.getParameterTypes()) {
                if (paramType.getAnnotation(XmlRootElement.class) != null) {
                    parameterTypes.add(paramType);
                }
            }
            
            Class<?> returnType = method.getReturnType();
            if (returnType.getAnnotation(XmlRootElement.class) != null) {
                this.returnType = returnType;
            }

            for (Class<?> fault : method.getExceptionTypes()) {
                if (fault.getAnnotation(WebFault.class) != null) {
                    faultInfos.add(fault.getMethod("getFaultInfo").getReturnType());
                    faults.add(fault);
                }
            }

            for (Class<?> fault : faults) {
                Constructor<?> constructor = fault.getConstructor(String.class, fault.getMethod("getFaultInfo").getReturnType());
                faultConstructors.put(fault.getMethod("getFaultInfo").getReturnType(), constructor);
            }
            
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }
        
    }
    
    /**
     * @return
     */
    public List<Class<?>> getJaxbClasses() {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        classes.addAll(parameterTypes);
        classes.addAll(faultInfos);
        if (returnType != null) {
            classes.add(returnType);
        }
        return classes;
    }
    
    /**
     * @return
     */
    public Map<Class<?>, Constructor<?>> getFaultConstructors() {
        return faultConstructors;
    }

}
