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
package org.sca4j.introspection.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.impl.contract.DefaultContractProcessor;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ServiceDefinition;
import org.sca4j.scdl.ValidationContext;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class DefaultIntrospectionHelperTestCase extends TestCase {
    private DefaultIntrospectionHelper helper;
    private TypeMapping boundMapping;
    private TypeMapping baseMapping;

    private static interface SuperInterface {
    }

    private static interface ServiceInterface extends SuperInterface {
    }

    private static interface SubInterface extends ServiceInterface {
    }

    private static class Base {
    }

    private static class BaseWithInterface implements ServiceInterface {
    }

    private static class ExtendsBaseWithInterface extends BaseWithInterface {
    }

    private static class ExtendsBaseWithSubInterface extends BaseWithInterface implements SubInterface {
    }

    private static class ExtendsBase extends Base {
    }

    private static class BaseTypes<T extends Base> {
        public T t;
        public Collection<String> stringCollection;
        public Map<String, Integer> intMap;

        public T[] tArray;
        public Collection<T> tCollection;
        public Map<String, T> tMap;
    }

    private static class BoundTypes extends BaseTypes<ExtendsBase> {
    }

    public void testTypeMappingBoundTypes() {
        assertEquals(ExtendsBase.class, boundMapping.getActualType(getType(BaseTypes.class, "t")));
    }

    public void testBaseType() {
        assertEquals(String.class, helper.getBaseType(String.class, baseMapping));
        assertEquals(int.class, helper.getBaseType(int.class, baseMapping));
        assertEquals(int.class, helper.getBaseType(Integer.TYPE, baseMapping));
        assertEquals(Integer.class, helper.getBaseType(Integer.class, baseMapping));

        assertEquals(int.class, helper.getBaseType(int[].class, baseMapping));
        assertEquals(String.class, helper.getBaseType(String[].class, baseMapping));

        assertEquals(String.class, helper.getBaseType(getType(BaseTypes.class, "stringCollection"), baseMapping));
        assertEquals(Integer.class, helper.getBaseType(getType(BaseTypes.class, "intMap"), baseMapping));

        assertEquals(Base.class, helper.getBaseType(getType(BaseTypes.class, "tArray"), baseMapping));
        assertEquals(Base.class, helper.getBaseType(getType(BaseTypes.class, "tCollection"), baseMapping));
        assertEquals(Base.class, helper.getBaseType(getType(BaseTypes.class, "tMap"), baseMapping));
    }

    public void testBoundTypes() {
        assertEquals(ExtendsBase.class, helper.getBaseType(getType(BoundTypes.class, "tArray"), boundMapping));
        assertEquals(ExtendsBase.class, helper.getBaseType(getType(BoundTypes.class, "tCollection"), boundMapping));
    }

    protected Type getType(Class<?> type, String fieldName) {
        try {
            return type.getField(fieldName).getGenericType();
        } catch (NoSuchFieldException e) {
            throw new AssertionError(fieldName);
        }
    }

    public void testImplementedInterfacesWithNoInterfaces() {
        assertTrue(helper.getImplementedInterfaces(Base.class).isEmpty());
        assertTrue(helper.getImplementedInterfaces(ExtendsBase.class).isEmpty());
    }

    public void testImplementedInterfaces() {
        assertEquals(Collections.singleton(ServiceInterface.class), helper.getImplementedInterfaces(BaseWithInterface.class));
        assertEquals(Collections.singleton(ServiceInterface.class), helper.getImplementedInterfaces(ExtendsBaseWithInterface.class));
        assertEquals(Collections.singleton(SubInterface.class), helper.getImplementedInterfaces(ExtendsBaseWithSubInterface.class));
    }

    private abstract static class InjectionBase {
        // these fields should be found
        public int publicBase;
        protected int protectedBase;

        // these fields should not
        int packageBase;
        private int privateBase;
        public static int staticInt;
        public final int finalBase = 0;

        // these methods should be found
        public void setPublicBase(int value) {
        }

        public void setPublicOverride(int value) {
        }

        protected void setProtectedBase(int value) {
        }

        // these methods should not
        void setPackageBase(int value) {
        }

        private void setPrivateBase(int value) {
        }

        public static void setStatic(int value) {
        }

        public abstract void setAbstract(int value);

        public void set(int value) {
        }

        public void setNoValue() {
        }

        public int setIntReturn(int value) {
            return 0;
        }
    }

    private static class InjectionSubClass extends InjectionBase {
        public int publicSub;
        protected Object protectedBase; // field that obscures the one in the superclass (yuck)

        public void setAbstract(int value) {
        }

        public void setPublicOverride(int value) {
        }
    }

    private static interface InterfaceWithSetter {
        void setFromInterface(int value);
    }

    private static class InjectionWithInterface implements InterfaceWithSetter {
        // this should not be included
        public void setFromInterface(int value) {
        }
    }

    public void testGetInjectionFields() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<Field>();
        expected.add(InjectionBase.class.getDeclaredField("publicBase"));
        expected.add(InjectionBase.class.getDeclaredField("protectedBase"));
        assertEquals(expected, helper.getInjectionFields(InjectionBase.class));
    }

    public void testGetInjectionFieldsOnSubclass() throws NoSuchFieldException {
        Set<Field> expected = new HashSet<Field>();
        expected.add(InjectionBase.class.getDeclaredField("publicBase"));
        expected.add(InjectionSubClass.class.getDeclaredField("publicSub"));
        expected.add(InjectionSubClass.class.getDeclaredField("protectedBase"));
        assertEquals(expected, helper.getInjectionFields(InjectionSubClass.class));
    }

    public void testGetInjectionMethods() throws NoSuchMethodException {
        Set<Method> expected = new HashSet<Method>();
        expected.add(InjectionBase.class.getDeclaredMethod("setPublicBase", int.class));
        expected.add(InjectionBase.class.getDeclaredMethod("setPublicOverride", int.class));
        expected.add(InjectionBase.class.getDeclaredMethod("setProtectedBase", int.class));
        Collection<ServiceDefinition> services = Collections.emptySet();
        assertEquals(expected, helper.getInjectionMethods(InjectionBase.class, services));
    }

    public void testGetInjectionMethodsOnSubclass() throws NoSuchMethodException {
        Set<Method> expected = new HashSet<Method>();
        expected.add(InjectionBase.class.getDeclaredMethod("setPublicBase", int.class));
        expected.add(InjectionBase.class.getDeclaredMethod("setProtectedBase", int.class));
        expected.add(InjectionSubClass.class.getDeclaredMethod("setPublicOverride", int.class));
        expected.add(InjectionSubClass.class.getDeclaredMethod("setAbstract", int.class));
        Collection<ServiceDefinition> services = Collections.emptySet();
        assertEquals(expected, helper.getInjectionMethods(InjectionSubClass.class, services));
    }

    public void testGetInjectionMethodsExcludesService() throws NoSuchMethodException {
        Set<Method> expected = Collections.emptySet();
        Set<ServiceDefinition> services = new HashSet<ServiceDefinition>();
        ValidationContext context = new DefaultValidationContext();
        ServiceContract contract = new DefaultContractProcessor(helper).introspect(new TypeMapping(), InterfaceWithSetter.class, context);
        ServiceDefinition definition = new ServiceDefinition("InterfaceWithSetter", contract);
        services.add(definition);
        assertEquals(expected, helper.getInjectionMethods(InjectionWithInterface.class, services));
    }

    protected void setUp() throws Exception {
        super.setUp();
        helper = new DefaultIntrospectionHelper();
        baseMapping = helper.mapTypeParameters(BaseTypes.class);
        boundMapping = helper.mapTypeParameters(BoundTypes.class);
    }
}
