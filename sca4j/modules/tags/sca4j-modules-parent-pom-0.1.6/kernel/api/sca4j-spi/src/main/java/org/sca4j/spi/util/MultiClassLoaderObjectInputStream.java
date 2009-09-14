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
package org.sca4j.spi.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.URI;

import org.sca4j.spi.services.classloading.ClassLoaderRegistry;

/**
 * A specialized ObjectInputStream that can deserialize objects loaded in different runtime classloaders. When ObjectInputStream.resolveClass() is
 * called, a classloader id is read if present in the byte stream. The id is used to resolve the classloader to load the class in.
 *
 * @version $Revision$ $Date$
 */
public class MultiClassLoaderObjectInputStream extends ObjectInputStream {
    private ClassLoaderRegistry registry;

    public MultiClassLoaderObjectInputStream(InputStream in, ClassLoaderRegistry registry) throws IOException {
        super(in);
        this.registry = registry;
    }

    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        int val = readByte();
        if (val == -1) {
            return super.resolveClass(desc);
        } else {
            byte[] bytes = new byte[val];
            int result = read(bytes);
            if (result == -1) {
                throw new IOException("Invalid classloader URL");
            }
            String id = new String(bytes);
            URI uri = URI.create(id);
            ClassLoader cl = registry.getClassLoader(uri);
            if (cl == null) {
                throw new IOException("Classloader not found: " + id);
            }
            return Class.forName(desc.getName(), false, cl);
        }
    }

}
