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
package org.sca4j.pojo.instancefactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.sca4j.scdl.Signature;

/**
 * Utility methods used when creating ReflectiveInstanceFactoryProviders.
 *
 * @version $Rev: 5238 $ $Date: 2008-08-20 20:27:39 +0100 (Wed, 20 Aug 2008) $
 */
public interface InstanceFactoryBuildHelper {

    /**
     * Loads the class using the given classloader
     *
     * @param cl   the classloader to load the class with
     * @param name the name of the class
     * @return the loaded class
     * @throws ClassNotFoundException if the class is not accessible to the classloader
     */
    Class<?> loadClass(ClassLoader cl, String name) throws ClassNotFoundException;

    /**
     * Returns the constructor on the given class matching the signature.
     *
     * @param implClass the class
     * @param signature the constructor signature
     * @return the constructor
     * @throws ClassNotFoundException if one of the constructor parameters could not be loaded
     * @throws NoSuchMethodException  if no matching constructor could be found
     */
    <T> Constructor<T> getConstructor(Class<T> implClass, Signature signature) throws ClassNotFoundException, NoSuchMethodException;

    /**
     * Returns the method on the given class matching the signature.
     *
     * @param implClass the class
     * @param signature the method signature
     * @return the constructor
     * @throws ClassNotFoundException if one of the method parameters could not be loaded
     * @throws NoSuchMethodException  if no matching method could be found
     */
    Method getMethod(Class<?> implClass, Signature signature) throws NoSuchMethodException, ClassNotFoundException;

}
