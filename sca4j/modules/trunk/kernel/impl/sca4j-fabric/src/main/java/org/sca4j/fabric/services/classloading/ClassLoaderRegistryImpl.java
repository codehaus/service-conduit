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
package org.sca4j.fabric.services.classloading;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sca4j.spi.classloader.MultiParentClassLoader;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.classloading.DuplicateClassLoaderException;

/**
 * Implementation of a registry for classloaders.
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class ClassLoaderRegistryImpl implements ClassLoaderRegistry {
    private final Map<URI, ClassLoader> registry = new ConcurrentHashMap<URI, ClassLoader>();
    private static final Map<String, Class<?>> PRIMITIVES;

    static {
        PRIMITIVES = new HashMap<String, Class<?>>();
        PRIMITIVES.put("boolean", Boolean.TYPE);
        PRIMITIVES.put("byte", Byte.TYPE);
        PRIMITIVES.put("short", Short.TYPE);
        PRIMITIVES.put("int", Integer.TYPE);
        PRIMITIVES.put("long", Long.TYPE);
        PRIMITIVES.put("float", Float.TYPE);
        PRIMITIVES.put("double", Double.TYPE);
        PRIMITIVES.put("void", Void.TYPE);
    }

    public synchronized void register(URI id, ClassLoader classLoader) throws DuplicateClassLoaderException {
        if (registry.containsKey(id)) {
            String identifier = id.toString();
            throw new DuplicateClassLoaderException("Duplicate class loader: " + identifier, identifier);
        }
        registry.put(id, classLoader);
    }

    public ClassLoader unregister(URI id) {
        return registry.remove(id);
    }

    public ClassLoader getClassLoader(URI id) {
        return registry.get(id);
    }

    public Map<URI, ClassLoader> getClassLoaders() {
        return registry;
    }


    public Class<?> loadClass(URI classLoaderId, String className) throws ClassNotFoundException {
        ClassLoader cl = getClassLoader(classLoaderId);
        return loadClass(cl, className);
    }

    public Class<?> loadClass(ClassLoader cl, String className) throws ClassNotFoundException {
        Class<?> clazz = PRIMITIVES.get(className);
        if (clazz == null) {
            clazz = Class.forName(className, true, cl);
        }
        return clazz;
    }

    public List<URI> resolveParentUris(ClassLoader cl) {
        if (cl instanceof MultiParentClassLoader) {
            MultiParentClassLoader loader = (MultiParentClassLoader) cl;
            List<ClassLoader> parents = loader.getParents();
            List<URI> uris = new ArrayList<URI>(parents.size());
            for (ClassLoader parent : parents) {
                URI resolved = resolveUri(parent);
                if (resolved != null) {
                    uris.add(resolved);
                }
            }
            return uris;
        } else {
            List<URI> uris = new ArrayList<URI>();
            ClassLoader parent = cl.getParent();
            URI resolved = resolveUri(parent);
            if (resolved != null) {
                uris.add(resolved);
            }
            return uris;
        }
    }

    private URI resolveUri(ClassLoader cl) {
        for (Map.Entry<URI, ClassLoader> entry : registry.entrySet()) {
            if (entry.getValue().equals(cl)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
