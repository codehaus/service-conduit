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
package org.sca4j.introspection.impl.annotation;

import java.lang.reflect.Method;
import java.net.URI;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;

import org.oasisopen.sca.annotation.Scope;
import org.oasisopen.sca.annotation.Init;
@SuppressWarnings("unchecked")
public class InitProcessorTestCase extends TestCase {

    public void testInvalidInit() throws Exception {
        TestInvalidInitClass componentToProcess = new TestInvalidInitClass();
        Init annotation = componentToProcess.getClass().getAnnotation(Init.class);
        InitProcessor<Implementation<? extends InjectingComponentType>> processor =
                new InitProcessor<Implementation<? extends InjectingComponentType>>();
        IntrospectionContext context = new DefaultIntrospectionContext((URI) null, null, null);
        processor.visitMethod(annotation, TestInvalidInitClass.class.getDeclaredMethod("init"), new TestImplementation(), context);
        assertEquals(1, context.getWarnings().size());
        assertTrue(context.getWarnings().get(0) instanceof InvalidAccessor);
    }

    public void testInit() throws Exception {
        TestClass componentToProcess = new TestClass();
        Init annotation = componentToProcess.getClass().getAnnotation(Init.class);
        InitProcessor<Implementation<? extends InjectingComponentType>> processor =
                new InitProcessor<Implementation<? extends InjectingComponentType>>();
        IntrospectionContext context = new DefaultIntrospectionContext((URI) null, null, null);
        TestImplementation impl = new TestImplementation();
        InjectingComponentType componentType = new InjectingComponentType() {

        };
        impl.setComponentType(componentType);
        Method method = TestClass.class.getDeclaredMethod("init");
        processor.visitMethod(annotation, method, impl, context);
        assertEquals(0, context.getWarnings().size());
        assertEquals(method, impl.getComponentType().getInitMethod().getMethod(TestClass.class));
    }


    @Scope("CONVERSATION")
    public static class TestClass {
        @Init
        public void init() {

        }
    }

    @Scope("CONVERSATION")
    public static class TestInvalidInitClass {
        @Init
        private void init() {

        }
    }

    public static class TestImplementation extends Implementation<InjectingComponentType> {
        private static final long serialVersionUID = 2759280710238779821L;

        public QName getType() {
            return null;
        }

    }

}
