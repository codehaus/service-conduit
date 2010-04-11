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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.osoa.sca.annotations.Reference;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ValidationContext;

@SuppressWarnings("unchecked")
public class ReferenceProcessorTestCase extends TestCase {
    private ReferenceProcessor<Implementation<? extends InjectingComponentType>> processor;

    public void testInvalidMethodAccessor() throws Exception {
        Method method = TestPrivateClass.class.getDeclaredMethod("setRequiredReference", TestPrivateClass.class);
        Reference annotation = method.getAnnotation(Reference.class);
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitMethod(annotation, method, new TestImplementation(), context);
        assertEquals(1, context.getErrors().size());
        assertTrue(context.getErrors().get(0) instanceof InvalidAccessor);
    }

    public void testWarningMethodAccessor() throws Exception {
        Method method = TestPrivateClass.class.getDeclaredMethod("setReference", TestPrivateClass.class);
        Reference annotation = method.getAnnotation(Reference.class);
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitMethod(annotation, method, new TestImplementation(), context);
        assertEquals(1, context.getWarnings().size());
        assertTrue(context.getWarnings().get(0) instanceof InvalidAccessor);
    }

    public void testInvalidFieldAccessor() throws Exception {
        Field field = TestPrivateClass.class.getDeclaredField("requiredFieldReference");
        Reference annotation = field.getAnnotation(Reference.class);
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitField(annotation, field, new TestImplementation(), context);
        assertEquals(1, context.getErrors().size());
        assertTrue(context.getErrors().get(0) instanceof InvalidAccessor);
    }

    public void testWarningFieldAccessor() throws Exception {
        Field field = TestPrivateClass.class.getDeclaredField("fieldReference");
        Reference annotation = field.getAnnotation(Reference.class);
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitField(annotation, field, new TestImplementation(), context);
        assertEquals(1, context.getWarnings().size());
        assertTrue(context.getWarnings().get(0) instanceof InvalidAccessor);
    }


    public static class TestPrivateClass {
        @Reference
        private void setRequiredReference(TestPrivateClass clazz) {

        }

        @Reference(required = false)
        private void setReference(TestPrivateClass clazz) {

        }

        @Reference
        private TestPrivateClass requiredFieldReference;

        @Reference(required = false)
        private TestPrivateClass fieldReference;

    }

    public static class TestImplementation extends Implementation {
        private static final long serialVersionUID = 2759280710238779821L;

        public QName getType() {
            return null;
        }

        public AbstractComponentType getComponentType() {
            return new InjectingComponentType();
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        final ServiceContract<Type> contract = new ServiceContract<Type>() {
            private static final long serialVersionUID = -1453983556738324512L;

            public boolean isAssignableFrom(ServiceContract serviceContract) {
                return false;
            }

            public String getQualifiedInterfaceName() {
                return null;
            }
        };

        ContractProcessor contractProcessor = new ContractProcessor() {

            public ServiceContract<Type> introspect(TypeMapping typeMapping, Type type, ValidationContext context) {
                return contract;
            }
        };
        processor = new ReferenceProcessor<Implementation<? extends InjectingComponentType>>(contractProcessor, helper);

    }
}
