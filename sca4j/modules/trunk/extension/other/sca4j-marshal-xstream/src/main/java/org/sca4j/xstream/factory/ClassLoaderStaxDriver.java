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
package org.sca4j.xstream.factory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Overrides the default behavior of StaxDriver to use a specific classloader when searching for the default STaX
 * implementation.
 *
 * @version $Rev: 2401 $ $Date: 2007-12-29 13:46:39 +0000 (Sat, 29 Dec 2007) $
 */
public class ClassLoaderStaxDriver extends StaxDriver {
    private ClassLoader classLoader;
    private XMLInputFactory inputFactory;
    private XMLOutputFactory outputFactory;


    public ClassLoaderStaxDriver(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public XMLInputFactory getInputFactory() {
        if (inputFactory == null) {
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(classLoader);
                inputFactory = XMLInputFactory.newInstance();
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }

        }
        return inputFactory;
    }

    public XMLOutputFactory getOutputFactory() {
        // set the classloader to load STaX in on the TCCL as XMLOutputFactory.newInstance(String, ClassLoader)
        // mistakenly returns an XMLInputFactory in STaX 1.0 and Sun has decided not to fix it in JDK 6.
        if (outputFactory == null) {
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(classLoader);
                outputFactory = XMLOutputFactory.newInstance();
            } finally {
                Thread.currentThread().setContextClassLoader(oldCl);
            }
        }
        return outputFactory;
    }
}
