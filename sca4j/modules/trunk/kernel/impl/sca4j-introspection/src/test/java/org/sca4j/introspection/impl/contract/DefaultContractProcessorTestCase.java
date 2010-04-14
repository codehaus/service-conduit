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
package org.sca4j.introspection.impl.contract;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.oasisopen.sca.annotation.Callback;
import org.sca4j.api.annotation.scope.Conversational;
import org.sca4j.api.annotation.scope.EndsConversation;
import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.impl.DefaultIntrospectionHelper;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.DefaultValidationContext;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ValidationContext;

/**
 * @version $Rev: 4286 $ $Date: 2008-05-21 20:56:34 +0100 (Wed, 21 May 2008) $
 */
public class DefaultContractProcessorTestCase extends TestCase {
    private ContractProcessor impl;
    private TypeMapping boundMapping;
    private TypeMapping emptyMapping;

    public void testSimpleInterface() {
        ValidationContext context = new DefaultValidationContext();
        ServiceContract contract = impl.introspect(emptyMapping, Simple.class, context);
        assertEquals("Simple", contract.getInterfaceName());
        assertEquals(Simple.class.getName(), contract.getQualifiedInterfaceName());
        List<Operation<?>> operations = contract.getOperations();
        sort(operations);
        assertEquals(1, operations.size());
        Operation<Type> baseInt = (Operation<Type>) operations.get(0);
        assertNotNull(baseInt);

        DataType<Type> returnType = baseInt.getOutputType();
        assertEquals(Integer.TYPE, returnType.getPhysical());
        assertEquals(Integer.TYPE, returnType.getLogical());

        List<DataType<Type>> parameterTypes = baseInt.getInputType().getLogical();
        assertEquals(1, parameterTypes.size());
        DataType<Type> arg0 = parameterTypes.get(0);
        assertEquals(Integer.TYPE, arg0.getPhysical());
        assertEquals(Integer.TYPE, arg0.getLogical());

        List<DataType<Type>> faultTypes = baseInt.getFaultTypes();
        assertEquals(1, faultTypes.size());
        DataType<Type> fault0 = faultTypes.get(0);
        assertEquals(IllegalArgumentException.class, fault0.getPhysical());
        assertEquals(IllegalArgumentException.class, fault0.getLogical());
    }

    public void testBoundGenericInterface() {
        ValidationContext context = new DefaultValidationContext();
        ServiceContract contract = impl.introspect(boundMapping, Generic.class, context);
        assertEquals("Generic", contract.getInterfaceName());

        List<Operation<?>> operations = contract.getOperations();
        sort(operations);
        assertEquals(2, operations.size());
        Operation<Type> operation = (Operation<Type>) operations.get(0);
        assertEquals("echo", operation.getName());

        DataType<Type> returnType = operation.getOutputType();
        assertEquals(Base.class, returnType.getPhysical());

    }

    public void testMethodGeneric() {
        ValidationContext context = new DefaultValidationContext();
        ServiceContract contract = impl.introspect(boundMapping, Generic.class, context);
        List<Operation<?>> operations = contract.getOperations();
        sort(operations);
        Operation<Type> operation = (Operation<Type>) operations.get(1);
        assertEquals("echo2", operation.getName());

        DataType<Type> returnType = operation.getOutputType();
//        assertEquals(Collection.class, returnType.getPhysical());
    }

    public void testCallbackInterface() {
        ValidationContext context = new DefaultValidationContext();
        ServiceContract contract = impl.introspect(emptyMapping, ForwardInterface.class, context);
        ServiceContract callback = contract.getCallbackContract();
        assertEquals("CallbackInterface", callback.getInterfaceName());
        assertEquals(CallbackInterface.class.getName(), callback.getQualifiedInterfaceName());
        List<? extends Operation<?>> operations = callback.getOperations();
        assertEquals(1, operations.size());
        Operation<?> back = operations.get(0);
        assertEquals("back", back.getName());
    }

    public void testConversationalInformationIntrospection() throws Exception {
        ValidationContext context = new DefaultValidationContext();
        ServiceContract contract = impl.introspect(emptyMapping, Foo.class, context);
        assertTrue(contract.isConversational());
        boolean testedContinue = false;
        boolean testedEnd = false;
        List<Operation<?>> operations = contract.getOperations();
        sort(operations);
		for (Operation<?> operation : operations) {
            if (operation.getName().equals("operation")) {
                assertEquals(Operation.CONVERSATION_CONTINUE, operation.getConversationSequence());
                testedContinue = true;
            } else if (operation.getName().equals("endOperation")) {
                assertEquals(Operation.CONVERSATION_END, operation.getConversationSequence());
                testedEnd = true;
            }
        }
        assertTrue(testedContinue);
        assertTrue(testedEnd);
    }

    public void testNonConversationalInformationIntrospection() throws Exception {
        ValidationContext context = new DefaultValidationContext();
        ServiceContract contract = impl.introspect(emptyMapping, NonConversationalFoo.class, context);
        assertFalse(contract.isConversational());
        boolean tested = false;
        for (Operation<?> operation : contract.getOperations()) {
            if (operation.getName().equals("operation")) {
                int seq = operation.getConversationSequence();
                assertEquals(Operation.NO_CONVERSATION, seq);
                tested = true;
            }
        }
        assertTrue(tested);
    }

    public void testInvalidConversationalAttribute() throws Exception {
        ValidationContext context = new DefaultValidationContext();
        impl.introspect(emptyMapping, BadConversation.class, context);
        assertTrue(context.getErrors().get(0) instanceof InvalidConversationalOperation);
    }

/*
    public void testUnregister() throws Exception {
        JavaInterfaceProcessor processor = createMock(JavaInterfaceProcessor.class);
        processor.visitInterface(eq(Base.class), isA(JavaServiceContract.class));
        processor.visitOperation(eq(Base.class.getMethod("baseInt", Integer.TYPE)), isA(Operation.class));
        expectLastCall().once();
        replay(processor);
        impl.registerProcessor(processor);
        impl.introspect(Base.class);
        impl.unregisterProcessor(processor);
        impl.introspect(Base.class);
        verify(processor);
    }
*/

    protected void setUp() throws Exception {
        super.setUp();
        IntrospectionHelper helper = new DefaultIntrospectionHelper();
        impl = new DefaultContractProcessor(helper);
        emptyMapping = new TypeMapping();
        boundMapping = helper.mapTypeParameters(BoundImpl.class);

    }
    
    /*
     * The reflection API call to get the methods on an interface doesn't always return them in the same
     * order. There's no reason to have the corresponding runtime Operation list sorted by name but some of
     * the tests depend on ordering so sort the operations here rather than in the runtime implementation.  
     */
    private void sort(List<Operation<?>> operations) {
        Collections.sort(operations, new Comparator<Operation<?>>(){
			@Override
			public int compare(Operation<?> o1, Operation<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}        	
        });  
    }

    private static interface Base {
        int baseInt(int param) throws IllegalArgumentException;
    }

    private static interface Simple extends Base {
    }

    private static interface Generic<T extends Base> {
        T echo(T t);

        <Q extends Collection<?>> Q echo2(Q q);
    }

    private static class GenericImpl<T extends Base> implements Generic<T> {
        public T echo(T t) {
            return t;
        }

        public <Q extends Collection<?>> Q echo2(Q q) {
            return q;
        }
    }

    private static class BoundImpl extends GenericImpl<Simple> {
    }

    @Callback(CallbackInterface.class)
    private static interface ForwardInterface {
        int forward() throws IllegalArgumentException;
    }

    private static interface CallbackInterface {
        int back() throws IllegalArgumentException;
    }

    private interface NonConversationalFoo {
        void operation();
    }

    @Conversational
    private interface Foo {
        void operation();

        @EndsConversation
        void endOperation();
    }

    private static interface BadConversation {
        void operation();

        @EndsConversation
        void endOperation();
    }

}
