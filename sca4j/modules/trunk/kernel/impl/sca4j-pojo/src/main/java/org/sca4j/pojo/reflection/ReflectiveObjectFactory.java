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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.pojo.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.sca4j.spi.ObjectCreationException;
import org.sca4j.spi.ObjectFactory;

/**
 * Reflectively instantiates a Java-based component instance.
 *
 * @version $Rev: 5239 $ $Date: 2008-08-20 20:31:46 +0100 (Wed, 20 Aug 2008) $
 */
public class ReflectiveObjectFactory<T> implements ObjectFactory<T> {
    private final Constructor<T> constructor;
    private final ObjectFactory<?>[] paramFactories;

    /**
     * Constructor.
     *
     * @param constructor    the constructor to use for instance instantiation
     * @param paramFactories factories for creating constructor parameters
     */
    public ReflectiveObjectFactory(Constructor<T> constructor, ObjectFactory<?>[] paramFactories) {
        this.constructor = constructor;
        this.paramFactories = paramFactories;
    }

    public T getInstance() throws ObjectCreationException {
        try {
            if (paramFactories == null) {
                return constructor.newInstance();
            } else {
                Object[] params = new Object[paramFactories.length];
                for (int i = 0; i < paramFactories.length; i++) {
                    ObjectFactory<?> paramFactory = paramFactories[i];
                    params[i] = paramFactory.getInstance();
                }
                try {
                    return constructor.newInstance(params);
                } catch (IllegalArgumentException e) {
                    // check which of the parameters could not be assigned
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    String name = constructor.toString();
                    for (int i = 0; i < paramTypes.length; i++) {
                        Class<?> paramType = paramTypes[i];
                        if (paramType.isPrimitive() && params[i] == null) {
                            throw new NullPrimitiveException(name, i);
                        }
                        if (params[i] != null && paramType.isInstance(params[i])) {
                            throw new IncompatibleArgumentException(name, i, params[i].getClass().getName());
                        }
                    }
                    // did not fail because of incompatible assignment
                    throw new ObjectCreationException(name, e);
                }
            }
        } catch (InstantiationException e) {
            String name = constructor.getDeclaringClass().getName();
            throw new AssertionError("Class is not instantiable:" + name);
        } catch (IllegalAccessException e) {
            String id = constructor.toString();
            throw new AssertionError("Constructor is not accessible: " + id);
        } catch (InvocationTargetException e) {
            String id = constructor.toString();
            throw new ObjectCreationException("Exception thrown by constructor: " + id, id, e.getCause());
        }
    }
}
