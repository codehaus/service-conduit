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
package org.sca4j.pojo.reflection;

import java.lang.reflect.Field;

import junit.framework.TestCase;
import org.easymock.EasyMock;

import org.sca4j.spi.ObjectFactory;

/**
 * @version $Rev: 198 $ $Date: 2007-06-08 21:39:49 +0100 (Fri, 08 Jun 2007) $
 */
public class FieldInjectorTestCase extends TestCase {

    protected Field protectedField;
    private ObjectFactory<String> objectFactory;
    private Foo foo;

    public void testIllegalAccess() throws Exception {
        String value = "foo";
        EasyMock.expect(objectFactory.getInstance()).andReturn(value);
        EasyMock.replay(objectFactory);

        FieldInjector<Foo> injector = new FieldInjector<Foo>(protectedField, objectFactory);
        injector.inject(foo);
        assertEquals(value, foo.hidden);
    }


    protected void setUp() throws Exception {
        super.setUp();
        protectedField = Foo.class.getDeclaredField("hidden");
        objectFactory = EasyMock.createMock(ObjectFactory.class);
        foo = new Foo();
    }

    private class Foo {
        private String hidden;
    }
}
