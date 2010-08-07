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
 */
package org.sca4j.introspection.impl.cdi.annotation;

import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createStrictControl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.easymock.classextension.IMocksControl;
import org.sca4j.introspection.DefaultIntrospectionContext;
import org.sca4j.introspection.IntrospectionContext;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.introspection.impl.annotation.InvalidAccessor;
import org.sca4j.scdl.AbstractComponentType;
import org.sca4j.scdl.ConstructorInjectionSite;
import org.sca4j.scdl.FieldInjectionSite;
import org.sca4j.scdl.Implementation;
import org.sca4j.scdl.InjectingComponentType;
import org.sca4j.scdl.MethodInjectionSite;
import org.sca4j.scdl.ReferenceDefinition;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ValidationContext;

/**
 * JUnit tests for basic (unqualified) @Inject processing.
 */
@SuppressWarnings("unchecked")
public class InjectProcessorBasicTestCase extends TestCase {
	
    private InjectProcessor<Implementation<? extends InjectingComponentType>> processor;
       
    /**
     * Tests that for an annotated method with an unsupported access modifier, a correctly 
     * populated error type is put into the introspection context  
     * @throws Exception on error.
     */
    public void testErrorMethodAccessor() throws Exception {
        Method method = TestPrivateClass.class.getDeclaredMethod("setErrorReference", TestPrivateClass.class);
        Inject annotation = method.getAnnotation(Inject.class);
        
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitMethod(annotation, method, new TestImplementation(new InjectingComponentType()), context);
        assertEquals(1, context.getErrors().size());
        assertTrue(context.getErrors().get(0) instanceof InvalidAccessor);
    }

    /**
     * Tests that for an annotated field with an unsupported access modifier, a correctly 
     * populated error type is put into the introspection context  
     * @throws Exception on error.
     */
    public void testErrorFieldAccessor() throws Exception {
        Field field = TestPrivateClass.class.getDeclaredField("errorFieldTarget");
        Inject annotation = field.getAnnotation(Inject.class);
        
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);

        processor.visitField(annotation, field, new TestImplementation(new InjectingComponentType()), context);
        assertEquals(1, context.getErrors().size());
        assertTrue(context.getErrors().get(0) instanceof InvalidAccessor);
        String expectedPrefix = "Invalid injection site. The @Inject annotated field 'errorFieldTarget'";
        assertTrue(context.getErrors().get(0).getMessage().startsWith(expectedPrefix));        
    }
    
    /**
     * Tests that for an annotated field with an supported access modifier, a correctly 
     * populated ReferenceDefinition is put in the in-context component type.  
     * @throws Exception on error.
     */
    public void testSuccessFieldAccessor() throws Exception {
        Field field = TestPrivateClass.class.getDeclaredField("succesFieldTarget");
        Inject annotation = field.getAnnotation(Inject.class);
        
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);
        
        IMocksControl iMocksControl = createStrictControl();
        InjectingComponentType mockComponentType = iMocksControl.createMock(InjectingComponentType.class);
        mockComponentType.add(isA(ReferenceDefinition.class), isA(FieldInjectionSite.class));
        iMocksControl.replay();

        processor.visitField(annotation, field, new TestImplementation(mockComponentType), context);
        assertEquals(0, context.getErrors().size());
        iMocksControl.verify();
    }    
    
    /**
     * Tests that for an annotated method with an supported access modifier, a correctly 
     * populated ReferenceDefinition is put in the in-context component type.  
     * @throws Exception on error.
     */    
    public void testSuccessMethodAccessor() throws Exception {
        Method method = TestPrivateClass.class.getDeclaredMethod("setSuccessReference", TestPrivateClass.class);
        Inject annotation = method.getAnnotation(Inject.class);
        
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);
        
        IMocksControl iMocksControl = createStrictControl();
        InjectingComponentType mockComponentType = iMocksControl.createMock(InjectingComponentType.class);
        mockComponentType.add(isA(ReferenceDefinition.class), isA(MethodInjectionSite.class));
        iMocksControl.replay();

        processor.visitMethod(annotation, method, new TestImplementation(mockComponentType), context);
        assertEquals(0, context.getErrors().size());
        iMocksControl.verify();
    }     
    
    
    /**
     * Tests that for an annotated constructor, a correctly populated ReferenceDefinition is put in the 
     * in-context component type for each parameter.  
     * @throws Exception on error.
     */    
    public void testSuccessConstructor() throws Exception {
        Constructor<TestPrivateClass> constructor = TestPrivateClass.class.getDeclaredConstructor(TestPrivateClass.class, String.class);
        Inject annotation = constructor.getAnnotation(Inject.class);
        
        TypeMapping mapping = new TypeMapping();
        IntrospectionContext context = new DefaultIntrospectionContext(null, null, null, null, mapping);
        
        IMocksControl iMocksControl = createStrictControl();
        InjectingComponentType mockComponentType = iMocksControl.createMock(InjectingComponentType.class);
        mockComponentType.add(isA(ReferenceDefinition.class), isA(ConstructorInjectionSite.class));        
        expectLastCall().times(2);
        iMocksControl.replay();

        processor.visitConstructor(annotation, constructor, new TestImplementation(mockComponentType), context);
        assertEquals(0, context.getErrors().size());
        iMocksControl.verify();
    }     

    /**
     * The class reflected on by the tests.     
     */
    @SuppressWarnings("unused")
    private static class TestPrivateClass {
    	
        @Inject
        private void setErrorReference(TestPrivateClass instance) {}

        @Inject
        public void setSuccessReference(TestPrivateClass instance) {}        
        
        @Inject
        private TestPrivateClass errorFieldTarget;
        
        @Inject
        protected TestPrivateClass succesFieldTarget;
        
        @Inject
        public TestPrivateClass(TestPrivateClass instance, String s) {}
        
    }

    /**
     * The test only component implementation.     
     */
    public static class TestImplementation extends Implementation {
    	
        private static final long serialVersionUID = 2763728394728394514L;
        private final InjectingComponentType componentType;
        
        TestImplementation(final InjectingComponentType componentType) {
        	this.componentType = componentType;
        }
        
        public QName getType() {
            return null;
        }

        public AbstractComponentType getComponentType() {
            return componentType;
        }
    }

    /**
     * Create a properly configured InjectProcessor.
     */    
    protected void setUp() throws Exception {
        super.setUp();
        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        final ServiceContract contract = new ServiceContract() {

            public boolean isAssignableFrom(ServiceContract serviceContract) {
                return false;
            }

            public String getQualifiedInterfaceName() {
                return null;
            }
        };

        ContractProcessor contractProcessor = new ContractProcessor() {

            public ServiceContract introspect(TypeMapping typeMapping, Type type, ValidationContext context) {
                return contract;
            }
        };
        
        processor = new InjectProcessor<Implementation<? extends InjectingComponentType>>(contractProcessor, helper);

    }
}
