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
