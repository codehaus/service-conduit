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
package org.sca4j.spi.classloader;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import sun.security.util.SecurityConstants;

/**
 * A classloader implementation that supports a multi-parent hierarchy. Each classloader has a name that can be used to reference it in the runtime.
 *
 * @version $Rev: 3018 $ $Date: 2008-03-03 17:16:35 +0000 (Mon, 03 Mar 2008) $
 */
public class MultiParentClassLoader extends URLClassLoader {
    private static final URL[] NOURLS = {};

    private final URI name;
    private final ClassLoader parent;
    private final List<ClassLoader> parents = new CopyOnWriteArrayList<ClassLoader>();

    /**
     * Constructs a classloader with a name and a single parent.
     *
     * @param name   a name used to identify this classloader
     * @param parent the initial parent
     */
    public MultiParentClassLoader(URI name, ClassLoader parent) {
        this(name, NOURLS, parent);
    }

    /**
     * Constructs a classloader with a name, a set of resources and a single parent.
     *
     * @param name   a name used to identify this classloader
     * @param urls   the URLs from which to load classes and resources
     * @param parent the initial parent
     */
    public MultiParentClassLoader(URI name, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.name = name;
        this.parent = parent;
    }


    /**
     * Add a resource URL to this classloader's classpath. The "createClassLoader" RuntimePermission is required.
     *
     * @param url an additional URL from which to load classes and resources
     */
    public void addURL(URL url) {
        // Require RuntimePermission("createClassLoader")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkCreateClassLoader();
        }
        super.addURL(url);
    }

    /**
     * Add a parent to this classloader. The "createClassLoader" RuntimePermission is required.
     *
     * @param parent an additional parent classloader
     */
    public void addParent(ClassLoader parent) {
        // Require RuntimePermission("createClassLoader")
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkCreateClassLoader();
        }
        if (parent != null) {
            parents.add(parent);
        }
    }

    /**
     * Returns the name of this classloader.
     *
     * @return the name of this classloader
     */
    public URI getName() {
        return name;
    }

    /**
     * Returns the parent classLoaders. The "getClassLoader" RuntimePermission is required.
     *
     * @return the parent classLoaders
     */
    public List<ClassLoader> getParents() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
        }
        List<ClassLoader> list = new ArrayList<ClassLoader>();
        if (parent != null) {
            list.add(parent);
        }
        list.addAll(parents);
        return list;
    }

    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // look for already loaded classes
        Class clazz = findLoadedClass(name);
        if (clazz == null) {
            try {
                // look in the primary parent
                try {
                    clazz = Class.forName(name, resolve, parent);
                } catch (ClassNotFoundException e) {
                    // continue
                }
                if (clazz == null) {
                    // look in our parents
                    for (ClassLoader parent : parents) {
                        try {
                            clazz = parent.loadClass(name);
                            break;
                        } catch (ClassNotFoundException e) {
                            continue;
                        }
                    }
                }
                // look in our classpath
                if (clazz == null) {
                    clazz = findClass(name);
                }
            } catch (NoClassDefFoundError e) {
                throw e;
            } catch (ClassNotFoundException e) {
                throw e;
            }
        }
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }


    protected Class<?> findClass(String string) throws ClassNotFoundException {
        return super.findClass(string);
    }

    public URL findResource(String name) {
        // look in our parents
        for (ClassLoader parent : parents) {
            URL resource = parent.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        // look in our classpath
        return super.findResource(name);
    }

    public Enumeration<URL> findResources(String name) throws IOException {
        // LinkedHashSet because we want all resources in the order found but no duplicates
        Set<URL> resources = new LinkedHashSet<URL>();
        for (ClassLoader parent : parents) {
            Enumeration<URL> parentResources = parent.getResources(name);
            while (parentResources.hasMoreElements()) {
                resources.add(parentResources.nextElement());
            }
        }
        Enumeration<URL> myResources = super.findResources(name);
        while (myResources.hasMoreElements()) {
            resources.add(myResources.nextElement());
        }
        return Collections.enumeration(resources);
    }


    public String toString() {
        return name.toString();
    }
}
