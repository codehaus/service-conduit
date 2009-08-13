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
package org.sca4j.introspection;

import java.lang.reflect.TypeVariable;
import java.util.Collection;

import junit.framework.TestCase;

import org.sca4j.introspection.TypeMapping;

/**
 * @version $Rev: 3105 $ $Date: 2008-03-15 16:47:31 +0000 (Sat, 15 Mar 2008) $
 */
public class TypeMappingTestCase extends TestCase {
    private TypeMapping typeMapping;
    private TypeVariable<Class<Types>> t;

    private static class Base {
    }

    private static class ExtendsBase extends Base {
    }

    private static class Types<T extends Base> {
        public T t;
        public T[] tArray;
        public Collection<T> tCollection;
    }

    public void testGetRawTypeForUnbound() throws NoSuchFieldException {
        assertEquals(String.class, typeMapping.getRawType(String.class));
        assertEquals(Base.class, typeMapping.getRawType(Types.class.getField("t").getGenericType()));
        assertEquals(Base[].class, typeMapping.getRawType(Types.class.getField("tArray").getGenericType()));
        assertEquals(Collection.class, typeMapping.getRawType(Types.class.getField("tCollection").getGenericType()));
    }

    public void testGetRawTypeForBound() throws NoSuchFieldException {
        typeMapping.addMapping(t, ExtendsBase.class);
        assertEquals(ExtendsBase.class, typeMapping.getRawType(Types.class.getField("t").getGenericType()));
        assertEquals(ExtendsBase[].class, typeMapping.getRawType(Types.class.getField("tArray").getGenericType()));
        assertEquals(Collection.class, typeMapping.getRawType(Types.class.getField("tCollection").getGenericType()));
    }

    protected void setUp() throws Exception {
        super.setUp();
        typeMapping = new TypeMapping();
        t = Types.class.getTypeParameters()[0];
    }
}
