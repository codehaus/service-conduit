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
