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
