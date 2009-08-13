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
package org.sca4j.spi.services.classloading;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Registry for classloaders available to the local runtime.
 *
 * @version $Rev: 2301 $ $Date: 2007-12-22 10:13:17 +0000 (Sat, 22 Dec 2007) $
 */
public interface ClassLoaderRegistry {
    /**
     * Register a ClassLoader with the runtime.
     *
     * @param id          a unique id for the classloader
     * @param classLoader the classloader to register
     * @throws DuplicateClassLoaderException if there is already a classloader registered with the same id
     */
    void register(URI id, ClassLoader classLoader) throws DuplicateClassLoaderException;

    /**
     * Unregister the specified classloader from the system.
     *
     * @param id the id for the classloader
     * @return the classloader that was registed with the id, or null if none
     */
    ClassLoader unregister(URI id);

    /**
     * Returns the classloader registered with the supplied id, or null if none is registered.
     *
     * @param id the id for the classloader
     * @return the ClassLoader registered with that id, or null
     */
    ClassLoader getClassLoader(URI id);

    /**
     * Returns all registered classloaders.
     *
     * @return all registered classloaders
     */
    Map<URI, ClassLoader> getClassLoaders();

    /**
     * Load and define a class from a specific classloader.
     *
     * @param classLoaderId the id of the ClassLoader to use
     * @param className     the name of the class
     * @return the class
     * @throws ClassNotFoundException if the class could not be found by that classloader
     */
    Class<?> loadClass(URI classLoaderId, String className) throws ClassNotFoundException;

    /**
     * Load and define a class from a specific classloader.
     *
     * @param cl        the ClassLoader to use
     * @param className the name of the class
     * @return the class
     * @throws ClassNotFoundException if the class could not be found by that classloader
     */
    Class<?> loadClass(ClassLoader cl, String className) throws ClassNotFoundException;

    List<URI> resolveParentUris(ClassLoader cl);

}
