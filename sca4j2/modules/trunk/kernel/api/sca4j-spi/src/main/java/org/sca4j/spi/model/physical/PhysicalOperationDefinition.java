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
package org.sca4j.spi.model.physical;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents an operation.
 *
 * @version $Revision: 3715 $ $Date: 2008-04-24 18:54:02 +0100 (Thu, 24 Apr 2008) $
 *          <p/>
 *          TODO Discuss with Jeremy/Jim on how to model MEPs, INOUT parameters, faults etc
 */
public class PhysicalOperationDefinition  {

    // Parameters
    private List<String> parameterTypes = new LinkedList<String>();

    // Return
    private String returnType;

    // Name of the operation
    private String name;

    // Callback
    private boolean callback;

    private boolean endsConversation;

    // Interceptors defined against the operation
    private Set<PhysicalInterceptorDefinition> interceptors = new HashSet<PhysicalInterceptorDefinition>();

    /**
     * Returns the fully qualified parameter types for this operation.
     *
     * @return Parameter types.
     */
    public List<String> getParameters() {
        return parameterTypes;
    }

    /**
     * Add the fully qualified parameter type to the operation.
     *
     * @param parameter Parameter type to be added.
     */
    public void addParameter(String parameter) {
        parameterTypes.add(parameter);
    }

    /**
     * Gets the fuly qualified return type for this operation.
     *
     * @return Return type for this operation.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the fully qualified return type for this operation.
     *
     * @param returnType Return type for this operation.
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Returns the interceptor definitions available for this operation.
     *
     * @return Inteceptor definitions for this operation.
     */
    public Set<PhysicalInterceptorDefinition> getInterceptors() {
        return interceptors;
    }

    /**
     * Sets the interceptor definitions available for this operations.
     *
     * @param interceptors the interceptor definitions available for this operations
     */
    public void setInterceptors(Set<PhysicalInterceptorDefinition> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * Adds an interceptor definition to the operation.
     *
     * @param interceptor Interceptor definition to be added.
     */
    public void addInterceptor(PhysicalInterceptorDefinition interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Gets the name of the operation.
     *
     * @return Operation name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the operation.
     *
     * @param name Operation name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks whether the operation is a callback.
     *
     * @return True if this is a callback.
     */
    public boolean isCallback() {
        return callback;
    }

    /**
     * Sets whether this is a callback operation or not.
     *
     * @param callback True if this is a callback.
     */
    public void setCallback(boolean callback) {
        this.callback = callback;
    }

    /**
     * Returns true if the operation ends a conversation.
     *
     * @return true if the operation ends a conversation
     */
    public boolean isEndsConversation() {
        return endsConversation;
    }

    /**
     * Sets if the operation ends a conversation.
     *
     * @param endsConversation true if the operation ends a conversation
     */
    public void setEndsConversation(boolean endsConversation) {
        this.endsConversation = endsConversation;
    }
}
