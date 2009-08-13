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

package org.sca4j.introspection.impl.annotation;

import java.net.URI;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.DefaultIntrospectionContext;

import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;

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
