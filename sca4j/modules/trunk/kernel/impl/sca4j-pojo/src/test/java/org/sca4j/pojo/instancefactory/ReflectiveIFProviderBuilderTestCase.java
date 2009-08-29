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
package org.sca4j.pojo.instancefactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.InjectableAttribute;
import org.sca4j.scdl.InjectableAttributeType;
import org.sca4j.scdl.InjectionSite;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.Signature;
import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.spi.services.classloading.ClassLoaderRegistry;
import org.sca4j.spi.services.classloading.DuplicateClassLoaderException;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;

/**
 * @version $Date: 2008-08-20 22:25:50 +0100 (Wed, 20 Aug 2008) $ $Revision: 5245 $
 */
public class ReflectiveIFProviderBuilderTestCase extends TestCase {
    private InstanceFactoryBuildHelper helper = new BuildHelperImpl(new MockClassLoaderRegistry());
    private ReflectiveInstanceFactoryBuilder builder = new ReflectiveInstanceFactoryBuilder(null, helper);
    private InstanceFactoryDefinition definition;
    private Constructor<Foo> constructor;
    private ClassLoader cl;

    /**
     * Verifies an InjectableAttribute is set properly for constructor parameters
     *
     * @throws Exception
     */
    public void testCdiSource() throws Exception {
        InstanceFactoryProvider provider = builder.build(definition, cl);
        assertEquals(String.class, provider.getMemberType(new InjectableAttribute(InjectableAttributeType.PROPERTY, "a")));
    }

    /**
     * Verifies an InjectableAttribute is set properly for protected fields
     *
     * @throws Exception
     */
    public void testProtectedFieldInjectionSource() throws Exception {
        InjectableAttribute injectableAttribute = new InjectableAttribute(InjectableAttributeType.REFERENCE, "xyz");
        Field field = Foo.class.getDeclaredField("xyz");
        InjectionSite injectionSite = new FieldInjectionSite(field);
        definition.getPostConstruction().put(injectionSite, injectableAttribute);

        InstanceFactoryProvider provider = builder.build(definition, cl);
        Class<?> clazz = provider.getMemberType(injectableAttribute);
        assertEquals(Bar.class, clazz);
    }

    /**
     * Verifies an InjectableAttribute is set properly for setter methods
     *
     * @throws Exception
     */
    public void testMethodInjectionSource() throws Exception {
        InjectableAttribute injectableAttribute = new InjectableAttribute(InjectableAttributeType.REFERENCE, "abc");
        Method method = Foo.class.getMethod("setAbc", Bar.class);
        InjectionSite injectionSite = new MethodInjectionSite(method, 0);
        definition.getPostConstruction().put(injectionSite, injectableAttribute);

        InstanceFactoryProvider provider = builder.build(definition, cl);
        Class<?> clazz = provider.getMemberType(injectableAttribute);
        assertEquals(Bar.class, clazz);
    }


    protected void setUp() throws Exception {
        super.setUp();
        cl = getClass().getClassLoader();
        constructor = Foo.class.getConstructor(String.class, Long.class);

        definition = new InstanceFactoryDefinition();
        definition.setImplementationClass(Foo.class.getName());
        definition.setConstructor(new Signature(constructor));
        definition.setInitMethod(new Signature("init"));
        definition.setDestroyMethod(new Signature("destroy"));
        Map<InjectionSite, InjectableAttribute> construction = definition.getConstruction();
        construction.put(new ConstructorInjectionSite(constructor, 0), new InjectableAttribute(InjectableAttributeType.PROPERTY, "a"));
        construction.put(new ConstructorInjectionSite(constructor, 1), new InjectableAttribute(InjectableAttributeType.REFERENCE, "b"));
    }

    public static class Foo {

        protected Bar xyz;

        public Foo(String a, Long b) {
        }

        public void setAbc(Bar abc) {
        }

        public void init() {
        }

        public void destroy() {
        }

    }

    public static class Bar {

    }

    private class MockClassLoaderRegistry implements ClassLoaderRegistry {

        public void register(URI id, ClassLoader classLoader) throws DuplicateClassLoaderException {

        }

        public ClassLoader unregister(URI id) {
            return null;
        }

        public ClassLoader getClassLoader(URI id) {
            return null;
        }

        public Map<URI, ClassLoader> getClassLoaders() {
            return null;
        }

        public Class<?> loadClass(URI classLoaderId, String className) throws ClassNotFoundException {
            return null;
        }

        public Class<?> loadClass(ClassLoader cl, String className) throws ClassNotFoundException {
            return Class.forName(className, true, cl);
        }

        public List<URI> resolveParentUris(ClassLoader cl) {
            return null;
        }
    }
}
