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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;
import org.sca4j.scdl.DataType;
import org.sca4j.scdl.Operation;
import org.sca4j.scdl.ServiceContract;

/**
 * Represents a service contract specified using a Java interface
 *
 * @version $Rev: 5441 $ $Date: 2008-09-19 06:10:33 +0100 (Fri, 19 Sep 2008) $
 */
public class JavaServiceContract extends ServiceContract<Type> {

    private static final long serialVersionUID = -7360275776965712638L;
    // NOTE: this class cannot reference the actual Java class it represents as #isAssignableFrom may be performed
    // accross classloaders. This class may also be deserialized as part of a domain assembly in a context where the
    // Java class may not be present on the classpath.
    private String interfaceClass;
    private List<String> interfaces;
    private String superType;
    private List<MethodSignature> methodSignatures;
    
    //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6176992
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TYPES = new HashMap<Class<?>, Class<?>>();

    static {
        PRIMITIVE_TYPES.put(byte.class, Byte.class);
        PRIMITIVE_TYPES.put(short.class, Short.class);
        PRIMITIVE_TYPES.put(char.class, Character.class);
        PRIMITIVE_TYPES.put(int.class, Integer.class);
        PRIMITIVE_TYPES.put(long.class, Long.class);
        PRIMITIVE_TYPES.put(float.class, Float.class);
        PRIMITIVE_TYPES.put(double.class, Double.class);
        PRIMITIVE_TYPES.put(boolean.class, Boolean.class);
        PRIMITIVE_TYPES.put(void.class, Void.class);
    }

    public JavaServiceContract(Class<?> interfaceClazz) {
        methodSignatures = new ArrayList<MethodSignature>();
        Class<?> superClass = interfaceClazz.getSuperclass();
        if (superClass != null) {
            superType = superClass.getName();
        }
        interfaces = new ArrayList<String>();
        for (Method method : interfaceClazz.getDeclaredMethods()) {
            MethodSignature signature = new MethodSignature(method);
            if (!methodSignatures.contains(signature)) {
                methodSignatures.add(signature);
            }
        }
        this.interfaceClass = interfaceClazz.getName();
        addInterfaces(interfaceClazz, interfaces);
    }

    public String getQualifiedInterfaceName() {
        return getInterfaceClass();
    }

    /**
     * Returns the fully qualified class name used to represent the service contract.
     *
     * @return the class name used to represent the service contract
     */
    public String getInterfaceClass() {
        return interfaceClass;
    }

    /*
     * Determines if the class or interface represented by this
     * <code>Class</code> object is either the same as, or is a superclass or
     * superinterface of, the class or interface represented by the specified
     * <code>Class</code> parameter. It returns <code>true</code> if so;
    
     */
    public boolean isAssignableFrom(ServiceContract<?> contract) {
        if (JavaServiceContract.class.isInstance(contract)) {
            return isJavaAssignableFrom(JavaServiceContract.class.cast(contract));
        } else {
            return isNonJavaAssignableFrom(contract);
        }
    // TODO handle the case where the contract is defined using a different IDL
    //return false;
    }

    private boolean isJavaAssignableFrom(JavaServiceContract contract) {
        if ((superType == null && contract.superType != null) || (superType != null && !superType.equals(contract.superType))) {
            return false;
        }
        if (interfaceClass.equals(contract.interfaceClass)) {
            for (MethodSignature signature : methodSignatures) {
                if (!contract.methodSignatures.contains(signature)) {
                    return false;
                }
            }
            return true;
        } else {
            // check the interfaces 
            for (String superType : contract.interfaces) {
                if (superType.equals(interfaceClass)) {
                    // need to match params as well
                    return true;
                }
            }
        }
        return false;

    }

    private boolean isNonJavaAssignableFrom(ServiceContract contract) {
        //compare contract operations
        List<Operation<?>> theirOperations = contract.getOperations();
        Map<String, Operation<?>> theirOperationNames = new HashMap<String, Operation<?>>();
        for (Operation o : theirOperations) {
            theirOperationNames.put(o.getName(), o);
        }
        List<Operation<Type>> myOperations = this.getOperations();
        for (Operation o : myOperations) {
            Operation theirs = theirOperationNames.remove(o.getName());
            if (theirs == null) {
                return false;
            }
            if (!compareTypes(o.getInputType(), theirs.getInputType())) {
                return false;
            }
            List<DataType<?>> myParams = (List<DataType<?>>) o.getInputType().getLogical();
            List<DataType<?>> theirParams = (List<DataType<?>>) theirs.getInputType().getLogical();

            if (myParams.size() == theirParams.size()) {
                for (int i = 0; i < myParams.size(); i++) {
                    if (!compareTypes(myParams.get(i), theirParams.get(i))) {
                        return false;
                    }
                }
            }else{
                return false;
            }

            if (!compareTypes(o.getOutputType(), theirs.getOutputType())) {
                return false;
            }

            List<DataType<?>> theirFaults = theirs.getFaultTypes();
            List<DataType<?>> faults = o.getFaultTypes();
            for (DataType theirFault : theirFaults) {
                boolean matches = false;
                for (DataType myFault : faults) {
                    if (compareTypes(theirFault, myFault)) {
                        matches = true;
                        break;
                    }
                }
                if (!matches) {
                    return false;
                }
            }
        }
        return true;


    }

    private boolean compareTypes(DataType mine, DataType theirs) {
        Type myType = mine.getPhysical();
        Type theirType = theirs.getPhysical();
        if (myType instanceof Class && theirType instanceof Class) {
            Class myClass = (Class) myType;
            Class theirClass = (Class) theirType;
            if (myClass.isPrimitive()){
                myClass = PRIMITIVE_TYPES.get(myClass);
            }
            if (theirClass.isPrimitive()){
                theirClass = PRIMITIVE_TYPES.get(theirClass);
            }
            if (!theirClass.isAssignableFrom(myClass)) {
                return false;
            }
        } else {
            return false;
        }
        return true;

    }

    /**
     * Adds all interfaces implemented/extended by the class, including those of its ancestors.
     *
     * @param interfaze  the class to introspect
     * @param interfaces the collection of interfaces to add to
     */
    private void addInterfaces(Class<?> interfaze, List<String> interfaces) {
        for (Class<?> superInterface : interfaze.getInterfaces()) {
            if (!interfaces.contains(superInterface.getName())) {
                interfaces.add(superInterface.getName());
                addInterfaces(superInterface, interfaces);
            }
        }
    }

    private class MethodSignature implements Serializable {

        private static final long serialVersionUID = 8945587852354777957L;
        String name;
        List<String> parameters;
        String returnType;

        public MethodSignature(Method method) {
            name = method.getName();
            returnType = method.getReturnType().getName();
            parameters = new ArrayList<String>();
            for (Class<?> param : method.getParameterTypes()) {
                parameters.add(param.getName());
            }
        }

        public boolean equals(Object object) {
            if (!(object instanceof MethodSignature)) {
                return false;
            }
            MethodSignature other = (MethodSignature) object;
            if (!name.equals(other.name)) {
                return false;
            }
            if (!returnType.equals(other.returnType)) {
                return false;
            }
            if (parameters.size() != other.parameters.size()) {
                return false;
            }
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < parameters.size(); i++) {
                if (!parameters.get(i).equals(other.parameters.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
