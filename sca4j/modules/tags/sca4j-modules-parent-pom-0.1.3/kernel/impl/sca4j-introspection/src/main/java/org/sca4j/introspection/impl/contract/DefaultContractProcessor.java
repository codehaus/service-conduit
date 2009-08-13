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
package org.sca4j.introspection.impl.contract;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

import org.osoa.sca.Constants;
import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Conversational;
import org.osoa.sca.annotations.EndsConversation;
import org.osoa.sca.annotations.OneWay;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Remotable;

import org.sca4j.introspection.IntrospectionHelper;
import org.sca4j.introspection.TypeMapping;
import org.sca4j.introspection.contract.ContractProcessor;
import org.sca4j.introspection.contract.InterfaceIntrospector;
import org.sca4j.introspection.contract.OperationIntrospector;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import static org.sca4j.scdl.Operation.CONVERSATION_END;
import static org.sca4j.scdl.Operation.NO_CONVERSATION;
import org.sca4j.scdl.ServiceContract;
import org.sca4j.scdl.ValidationContext;

/**
 * Default implementation of a ContractProcessor for Java interfaces.
 *
 * @version $Rev: 5306 $ $Date: 2008-08-31 17:28:33 +0100 (Sun, 31 Aug 2008) $
 */
public class DefaultContractProcessor implements ContractProcessor {
    public static final String IDL_INPUT = "idl:input";
    public static final QName ONEWAY_INTENT = new QName(Constants.SCA_NS, "oneWay");

    private final IntrospectionHelper helper;
    private List<InterfaceIntrospector> interfaceIntrospectors;
    private List<OperationIntrospector> operationIntrospectors;


    public DefaultContractProcessor(@Reference IntrospectionHelper helper) {
        this.helper = helper;
        interfaceIntrospectors = new ArrayList<InterfaceIntrospector>();
        operationIntrospectors = new ArrayList<OperationIntrospector>();
    }

    @Reference(required = false)
    public void setInterfaceIntrospectors(List<InterfaceIntrospector> interfaceIntrospectors) {
        this.interfaceIntrospectors = interfaceIntrospectors;
    }

    @Reference(required = false)
    public void setOperationIntrospectors(List<OperationIntrospector> operationIntrospectors) {
        this.operationIntrospectors = operationIntrospectors;
    }

    public ServiceContract<Type> introspect(TypeMapping typeMapping, Type type, ValidationContext context) {
        if (type instanceof Class) {
            return introspect(typeMapping, (Class<?>) type, context);
        } else {
            throw new UnsupportedOperationException("Interface introspection is only supported for classes");
        }
    }

    private JavaServiceContract introspect(TypeMapping typeMapping, Class<?> interfaze, ValidationContext context) {
        JavaServiceContract contract = introspectInterface(typeMapping, interfaze, context);
        Callback callback = interfaze.getAnnotation(Callback.class);
        if (callback != null) {
            Class<?> callbackClass = callback.value();
            if (Void.class.equals(callbackClass)) {
                context.addError(new MissingCallback(interfaze));
                return contract;
            }
            JavaServiceContract callbackContract = introspectInterface(typeMapping, callbackClass, context);
            contract.setCallbackContract(callbackContract);
        }
        return contract;
    }

    /**
     * Introspects a class, returning its service contract. Errors and warnings are reported in the ValidationContext.
     *
     * @param typeMapping generics mappings
     * @param interfaze   the interface to introspect
     * @param context     the current validation context to report errors
     * @return the service contract
     */
    private JavaServiceContract introspectInterface(TypeMapping typeMapping, Class<?> interfaze, ValidationContext context) {
        JavaServiceContract contract = new JavaServiceContract(interfaze);
        contract.setInterfaceName(interfaze.getSimpleName());

        boolean remotable = interfaze.isAnnotationPresent(Remotable.class);
        contract.setRemotable(remotable);

        boolean conversational = helper.isAnnotationPresent(interfaze, Conversational.class);
        contract.setConversational(conversational);

        List<Operation<Type>> operations = getOperations(typeMapping, interfaze, remotable, conversational, context);
        contract.setOperations(operations);
        for (InterfaceIntrospector introspector : interfaceIntrospectors) {
            introspector.introspect(contract, interfaze, context);
        }
        return contract;
    }

    private <T> List<Operation<Type>> getOperations(TypeMapping typeMapping,
                                                    Class<T> type,
                                                    boolean remotable,
                                                    boolean conversational,
                                                    ValidationContext context) {
        Method[] methods = type.getMethods();
        List<Operation<Type>> operations = new ArrayList<Operation<Type>>(methods.length);
        for (Method method : methods) {
            String name = method.getName();
            if (remotable) {
                boolean error = false;
                for (Operation<Type> operation : operations) {
                    if (operation.getName().equals(name)) {
                        context.addError(new OverloadedOperation(method));
                        error = true;
                        break;
                    }
                }
                if (error) {
                    continue;
                }
            }

            Class<?> returnType = method.getReturnType();
            Class<?>[] paramTypes = method.getParameterTypes();
            Class<?>[] faultTypes = method.getExceptionTypes();

            int conversationSequence = NO_CONVERSATION;
            if (method.isAnnotationPresent(EndsConversation.class)) {
                if (!conversational) {
                    context.addError(new InvalidConversationalOperation(method));
                }
                conversationSequence = CONVERSATION_END;
            } else if (conversational) {
                conversationSequence = Operation.CONVERSATION_CONTINUE;
            }

            Type actualReturnType = typeMapping.getActualType(returnType);
            DataType<Type> returnDataType = new DataType<Type>(actualReturnType, actualReturnType);
            List<DataType<Type>> paramDataTypes = new ArrayList<DataType<Type>>(paramTypes.length);
            for (Type paramType : paramTypes) {
                Type actualType = typeMapping.getActualType(paramType);
                paramDataTypes.add(new DataType<Type>(actualType, actualType));
            }
            List<DataType<Type>> faultDataTypes = new ArrayList<DataType<Type>>(faultTypes.length);
            for (Type faultType : faultTypes) {
                Type actualType = typeMapping.getActualType(faultType);
                faultDataTypes.add(new DataType<Type>(actualType, actualType));
            }

            DataType<List<DataType<Type>>> inputType = new DataType<List<DataType<Type>>>(Object[].class, paramDataTypes);
            Operation<Type> operation = new Operation<Type>(name, inputType, returnDataType, faultDataTypes, conversationSequence);

            if (method.isAnnotationPresent(OneWay.class)) {
                operation.addIntent(ONEWAY_INTENT);
            }
            for (OperationIntrospector introspector : operationIntrospectors) {
                introspector.introspect(operation, method, context);
            }
            operations.add(operation);
        }
        return operations;
    }

}
