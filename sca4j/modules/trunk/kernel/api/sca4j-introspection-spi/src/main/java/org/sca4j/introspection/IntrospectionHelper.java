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
package org.sca4j.introspection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

import org.sca4j.introspection.java.ImplementationNotFoundException;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.ServiceDefinition;

/**
 * Helper service that provides support methods to simplify introspection.
 *
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public interface IntrospectionHelper {

    /**
     * Load the class using the supplied ClassLoader. The class will be defined so any initializers present will be
     * fired. As the class is being loaded, the Thread context ClassLoader will be set to the supplied classloader.
     *
     * @param name the name of the class to load
     * @param cl   the classloader to use to load it
     * @return the class
     * @throws org.sca4j.introspection.java.ImplementationNotFoundException if the class could not be found
     */
    Class<?> loadClass(String name, ClassLoader cl) throws ImplementationNotFoundException;

    /**
     * Derive the name of an injection site from a field.
     *
     * @param field    the field to inspect
     * @param override an override specified in an annotation
     * @return the name of the injection site
     */
    String getSiteName(Field field, String override);

    /**
     * Derive the name of an injection site from a setter method.
     *
     * @param setter   the setter method to inspect
     * @param override an override specified in an annotation
     * @return the name of the injection site
     */
    String getSiteName(Method setter, String override);

    /**
     * Derive the name of an injection site from a setter method.
     *
     * @param constructor the constructor to inspect
     * @param index       the index of the constructor parameter to inspect
     * @param override    an override specified in an annotation
     * @return the name of the injection site
     */
    String getSiteName(Constructor<?> constructor, int index, String override);

    /**
     * Returns the generic type of a setter method.
     *
     * @param setter the method to inspect
     * @return the type of value the setter method injects
     */
    Type getGenericType(Method setter);

    /**
     * Returns the generic type of a method parameter.
     *
     * @param method the method to inspect
     * @param index  the parameter index
     * @return the type of value the method injects
     */
    Type getGenericType(Method method, int index);

    /**
     * Returns the generic type of a constructor parameter.
     *
     * @param constructor the constructor to inspect
     * @param index       the parameter index
     * @return the type of value the constructor injects
     */
    Type getGenericType(Constructor<?> constructor, int index);

    /**
     * Returns true if the supplied type should be treated as many-valued.
     * <p/>
     * This is generally true for arrays, Collection or Map types.
     *
     * @param typeMapping the mapping to use to resolve any formal types
     * @param type        the type to check
     * @return true if the type should be treated as many-valued
     */
    boolean isManyValued(TypeMapping typeMapping, Type type);

    InjectableAttributeType inferType(Type type, TypeMapping typeMapping);

    /**
     * Determine if an annotation is present on this interface or any superinterface.
     * <p/>
     * This is similar to the use of @Inherited on classes (given @Inherited does not apply to interfaces).
     *
     * @param type           the interface to check
     * @param annotationType the annotation to look for
     * @return true if the annotation is present
     */
    boolean isAnnotationPresent(Class<?> type, Class<? extends Annotation> annotationType);

    /**
     * Map the formal parameters of a type, its superclass and superinterfaces to the actual parameters of the class.
     *
     * @param type the class whose parameters should be mapped
     * @return a mapping of formal type parameters to actual types
     */
    TypeMapping mapTypeParameters(Class<?> type);

    /**
     * Returns the base type for the supplied type.
     * <p/>
     * The base type is the actual type of a property or reference having removed any decoration for arrays or collections.
     *
     * @param type        the type of a field or parameter
     * @param typeMapping the mapping to use to resolve any formal types
     * @return the actual type of the property or reference corresponding to the parameter
     */
    Type getBaseType(Type type, TypeMapping typeMapping);

    /**
     * Returns all service interfaces directly implemented by a class or any superclass.
     * <p/>
     * Class#getInterfaces only returns interfaces directly implemented by the class. This method returns all interfaces including those implemented
     * by any superclasses. It excludes interfaces that are super-interfaces of those implemented by subclasses.
     *
     * @param type the class whose interfaces should be returned
     * @return the unique interfaces immplemented by that class
     */
    Set<Class<?>> getImplementedInterfaces(Class<?> type);

    /**
     * Returns method injection sites provided by a class or any superclass.
     * <p/>
     * Methods that are part of any service contract are excluded.
     *
     * @param type     the class whose method sites should be returned
     * @param services the services implemented by the class
     * @return the method injection sites for the class
     */
    Set<Method> getInjectionMethods(Class<?> type, Collection<ServiceDefinition> services);

    /**
     * Returns method injection sites provided by a class or any superclass.
     * <p/>
     * Methods that are part of any service contract are excluded.
     *
     * @param type the class whose field injection sites should be returned
     * @return the setter injection sites for the class
     */
    Set<Field> getInjectionFields(Class<?> type);
}
