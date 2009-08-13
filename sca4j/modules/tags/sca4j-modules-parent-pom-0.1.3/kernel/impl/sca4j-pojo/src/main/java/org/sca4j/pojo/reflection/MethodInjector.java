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

import org.sca4j.pojo.injection.MultiplicityObjectFactory;
import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;

/**
 * Injects a value created by an {@link org.sca4j.spi.ObjectFactory} using a given method
 *
 * @version $Rev: 5234 $ $Date: 2008-08-20 19:33:22 +0100 (Wed, 20 Aug 2008) $
 */
public class MethodInjector<T> implements Injector<T> {
    private final Method method;
    private ObjectFactory<?> objectFactory;

    public MethodInjector(Method method, ObjectFactory<?> objectFactory) {
        assert method != null;
        assert objectFactory != null;
        this.method = method;
        this.method.setAccessible(true);
        this.objectFactory = objectFactory;
    }

    public void inject(T instance) throws ObjectCreationException {
        Object target = objectFactory.getInstance();
        if (target == null) {
            // The object factory is "empty", e.g. a reference has not been wired yet. Avoid injecting onto the instance.
            // Note this is a correct assumption as there is no mechanism for configuring null values in SCA
            return;
        }
        try {
            method.invoke(instance, target);
        } catch (IllegalAccessException e) {
            throw new AssertionError("Method is not accessible:" + method);
        } catch (IllegalArgumentException e) {
            String id = method.toString();
            throw new ObjectCreationException("Exception thrown by setter: " + id, id, e);
        } catch (InvocationTargetException e) {
            String id = method.toString();
            throw new ObjectCreationException("Exception thrown by setter: " + id, id, e);
        }
    }

    public void setObjectFactory(ObjectFactory<?> objectFactory, Object key) {

        if (this.objectFactory instanceof MultiplicityObjectFactory<?>) {
            ((MultiplicityObjectFactory<?>) this.objectFactory).addObjectFactory(objectFactory, key);
        } else {
            this.objectFactory = objectFactory;
        }

    }
}
