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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Callback;

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
public class CallbackProcessorTestCase extends TestCase {
    private CallbackProcessor<Implementation<? extends InjectingComponentType>> processor;

    public void testInvalidMethodAccessor() throws Exception {
        Method method = TestPrivateClass.class.getDeclaredMethod("setCallback", TestPrivateClass.class);
        Callback annotation = method.getAnnotation(Callback.class);
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitMethod(annotation, method, new TestImplementation(), context);
        assertEquals(1, context.getErrors().size());
        assertTrue(context.getErrors().get(0) instanceof InvalidAccessor);
    }

    public void testInvalidFieldAccessor() throws Exception {
        Field field = TestPrivateClass.class.getDeclaredField("callbackField");
        Callback annotation = field.getAnnotation(Callback.class);
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitField(annotation, field, new TestImplementation(), context);
        assertEquals(1, context.getErrors().size());
        assertTrue(context.getErrors().get(0) instanceof InvalidAccessor);
    }


    public static class TestPrivateClass {
        @Callback
        private void setCallback(TestPrivateClass clazz) {

        }

        @Callback
        private TestPrivateClass callbackField;

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
        processor = new CallbackProcessor<Implementation<? extends InjectingComponentType>>(contractProcessor, helper);

    }
}
