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
