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
package org.sca4j.scdl;

import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * @version $Rev: 2973 $ $Date: 2008-02-29 23:06:57 +0000 (Fri, 29 Feb 2008) $
 */
public class SignatureTestCase extends TestCase {

    public void testComplexType() throws Exception {
        Method method = Foo.class.getMethod("complex", Foo.class);
        Signature signature = new Signature("complex", Foo.class.getName());
        assertEquals(method, signature.getMethod(Foo.class));
    }

    public void testPrimitiveInt() throws Exception {
        Method method = Foo.class.getMethod("primitiveInt", Integer.TYPE);
        Signature signature = new Signature("primitiveInt", "int");
        assertEquals(method, signature.getMethod(Foo.class));
    }

    public void testPrimitiveDouble() throws Exception {
        Method method = Foo.class.getMethod("primitiveDouble", Double.TYPE);
        Signature signature = new Signature("primitiveDouble", "double");
        assertEquals(method, signature.getMethod(Foo.class));
    }

    public void testPrimitiveBoolean() throws Exception {
        Method method = Foo.class.getMethod("primitiveBoolean", Boolean.TYPE);
        Signature signature = new Signature("primitiveBoolean", "boolean");
        assertEquals(method, signature.getMethod(Foo.class));
    }

    public void testPrimitiveByte() throws Exception {
        Method method = Foo.class.getMethod("primitiveByte", Byte.TYPE);
        Signature signature = new Signature("primitiveByte", "byte");
        assertEquals(method, signature.getMethod(Foo.class));
    }

    public void testPrimitiveShort() throws Exception {
        Method method = Foo.class.getMethod("primitiveShort", Short.TYPE);
        Signature signature = new Signature("primitiveShort", "short");
        assertEquals(method, signature.getMethod(Foo.class));
    }

    public void testPrimitiveLong() throws Exception {
        Method method = Foo.class.getMethod("primitiveLong", Long.TYPE);
        Signature signature = new Signature("primitiveLong", "long");
        assertEquals(method, signature.getMethod(Foo.class));
    }

    public void testPrimitiveFloat() throws Exception {
        Method method = Foo.class.getMethod("primitiveFloat", Float.TYPE);
        Signature signature = new Signature("primitiveFloat", "float");
        assertEquals(method, signature.getMethod(Foo.class));
    }

    private class Foo {
        public void complex(Foo foo) {

        }

        public void primitiveInt(int i) {

        }

        public void primitiveDouble(double i) {

        }

        public void primitiveBoolean(boolean i) {

        }

        public void primitiveByte(byte i) {

        }

        public void primitiveShort(short i) {

        }

        public void primitiveLong(long i) {

        }

        public void primitiveFloat(float i) {

        }

    }
}
