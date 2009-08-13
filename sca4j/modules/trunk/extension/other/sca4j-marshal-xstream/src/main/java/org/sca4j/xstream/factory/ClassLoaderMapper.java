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
