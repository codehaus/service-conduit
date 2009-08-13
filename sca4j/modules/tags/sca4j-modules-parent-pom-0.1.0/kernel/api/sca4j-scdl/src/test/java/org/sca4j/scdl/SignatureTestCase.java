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
