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
package org.sca4j.xstream.factory;

import java.net.URI;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.DefaultMapper;

import org.sca4j.spi.classloader.MultiParentClassLoader;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

/**
 * Encodes classnames and the classloader they are to be loaded in. This Mapper is used by the XStreamFactory so classes loaded in extension
 * classloaders can be deserialized properly, e.g. a a set of commands provisioned to a runtime node.
 *
 * @version $Revision$ $Date$
 */
public class ClassLoaderMapper extends DefaultMapper {
    private ClassLoaderRegistry registry;
    private ClassLoader defaultClassLoader;

    public ClassLoaderMapper(ClassLoaderRegistry registry, ClassLoader defaultClassLoader) {
        super(null);
        this.registry = registry;
        this.defaultClassLoader = defaultClassLoader;
    }

    public String serializedClass(Class type) {
        ClassLoader cl = type.getClassLoader();
        if (cl instanceof MultiParentClassLoader) {
            return type.getName() + "_f3_" + encode(((MultiParentClassLoader) cl).getName().toString());
        }
        return super.serializedClass(type);
    }

    public Class realClass(String elementName) {
        String[] elements = elementName.split("_f3_");
        ClassLoader cl;
        if (elements.length < 1) {
            // programming error
            throw new AssertionError("Illegal classname");
        }
        if (elements.length != 2) {
            cl = defaultClassLoader;
        } else {
            String classLoaderId = decode(elements[1]);
            cl = registry.getClassLoader(URI.create(classLoaderId));
            if (cl == null) {
                // programming error
                throw new AssertionError("Classloader not found for deserializaion: " + classLoaderId);
            }
        }
        try {
            return cl.loadClass(elements[0]);
        } catch (ClassNotFoundException e) {
            throw new CannotResolveClassException(elements[0] + " : " + e);
        }
    }

    /**
     * Encodes a classname and classloader id combination, escaping illegal XML characters.
     *
     * @param name the string to encode
     * @return the encoded string
     */
    private String encode(String name) {
        return name.replace("/", "_f3slash").replace(":", "_f3colon");
    }

    /**
     * Decodes a classname and classloader id combination.
     *
     * @param name the string to decode
     * @return the decoded string
     */
    private String decode(String name) {
        return name.replace("_f3slash", "/").replace("_f3colon", ":");
    }


}
