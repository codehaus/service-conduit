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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.pojo.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Performs an wire on a method of a given instance
 *
 * @version $Rev: 2973 $ $Date: 2008-02-29 23:06:57 +0000 (Fri, 29 Feb 2008) $
 */
public class MethodEventInvoker<T> implements EventInvoker<T> {
    private final Method method;

    /**
     * Intantiates an  invoker for the given method
     *
     * @param method the method to invoke on
     */
    public MethodEventInvoker(Method method) {
        assert method != null;
        this.method = method;
        this.method.setAccessible(true);
    }

    public void invokeEvent(T instance) throws ObjectCallbackException {
        try {
            method.invoke(instance);
        } catch (IllegalArgumentException e) {
            String name = method.toString();
            throw new ObjectCallbackException("Exception thrown by method: " + name, e.getCause());
        } catch (IllegalAccessException e) {
            String name = method.getName();
            throw new AssertionError("Method is not accessible: " + name);
        } catch (InvocationTargetException e) {
            String name = method.getName();
            throw new ObjectCallbackException("Exception thrown by callback method:" + name, e.getCause());
        }
    }

}
