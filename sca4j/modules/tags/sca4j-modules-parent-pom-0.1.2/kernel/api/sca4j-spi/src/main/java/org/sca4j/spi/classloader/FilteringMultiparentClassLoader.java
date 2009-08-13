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

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A ClassLoader implementations that only loads classes matching a set of regular expression patterns from its parent. For example,
 * <code>org.sca4j.test.*</code> loads all classes in the <code>org.sca4j.test</code> package and its subpackages.
 *
 * @version $Revision$ $Date$
 */
public class FilteringMultiparentClassLoader extends MultiParentClassLoader {
    private Set<Pattern> patterns = new HashSet<Pattern>();

    public FilteringMultiparentClassLoader(URI name, ClassLoader parent, Set<String> filters) {
        super(name, parent);
        compile(filters);
    }

    public FilteringMultiparentClassLoader(URI name, URL[] urls, ClassLoader parent, Set<String> filters) {
        super(name, urls, parent);
        compile(filters);
    }

    public Set<Pattern> getPatterns() {
        return patterns;
    }

    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = findLoadedClass(name);
        if (clazz == null) {
            boolean found = false;
            for (Pattern pattern : patterns) {
                String replacedName = name.replace(".", "\\.");
                if (pattern.matcher(replacedName).matches()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // check in the current classloader's classpath
                clazz = findClass(name);
            }
            if (clazz == null) {
                clazz = super.loadClass(name, resolve);
                return clazz;
            }
        }
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }

    private void compile(Set<String> filters) {
        for (String filter : filters) {
            String replacedFilter = filter.replace(".", "..");
            Pattern p = Pattern.compile(replacedFilter);
            patterns.add(p);
        }
    }

}
