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
package org.sca4j.scdl;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a component reference
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class ReferenceDefinition extends AbstractPolicyAware {
    private static final long serialVersionUID = 4641581818938572132L;
    private final String name;
    private ServiceContract<?> serviceContract;
    private Multiplicity multiplicity;
    private final List<BindingDefinition> bindings = new ArrayList<BindingDefinition>();
    private final List<BindingDefinition> callbackBindings = new ArrayList<BindingDefinition>();
    private final List<OperationDefinition> operations = new ArrayList<OperationDefinition>();

    /**
     * Constructor.
     *
     * @param name            the refeence name
     * @param serviceContract the service contract required by this reference
     */
    public ReferenceDefinition(String name, ServiceContract<?> serviceContract) {
        this(name, serviceContract, Multiplicity.ONE_ONE);
    }

    /**
     * Constructor.
     *
     * @param name            the refeence name
     * @param serviceContract the service contract required by this reference
     * @param multiplicity    the reference multiplicity
     */
    public ReferenceDefinition(String name, ServiceContract<?> serviceContract, Multiplicity multiplicity) {
        this.name = name;
        this.serviceContract = serviceContract;
        this.multiplicity = multiplicity;
    }

    /**
     * Returns the reference name.
     *
     * @return the reference name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the service contract required by this reference.
     *
     * @return the service contract required by this reference
     */
    public ServiceContract<?> getServiceContract() {
        return serviceContract;
    }

    /**
     * Sets the service contract required by this reference.
     *
     * @param serviceContract the service contract required by this reference
     */
    public void setServiceContract(ServiceContract<?> serviceContract) {
        this.serviceContract = serviceContract;
    }

    /**
     * Returns the reference multiplicity.
     *
     * @return the reference multiplicity
     */
    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    /**
     * Sets the reference multiplicity.
     *
     * @param multiplicity the reference multiplicity
     */
    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    /**
     * Returns true if the reference is required
     *
     * @return true if the reference is required
     */
    public boolean isRequired() {
        return multiplicity == Multiplicity.ONE_ONE || multiplicity == Multiplicity.ONE_N;
    }

    /**
     * @return List of bindings defined against the reference.
     */
    public List<BindingDefinition> getBindings() {
        return bindings;
    }

    /**
     * @param binding Binding to be added.
     */
    public void addBinding(BindingDefinition binding) {
        this.bindings.add(binding);
    }

    /**
     * @return List of callback bindings defined against the reference.
     */
    public List<BindingDefinition> getCallbackBindings() {
        return callbackBindings;
    }

    /**
     * @param binding callback binding to be added.
     */
    public void addCallbackBinding(BindingDefinition binding) {
        this.callbackBindings.add(binding);
    }

    /**
     * @return Get the list of operations defined against the reference.
     */
    public List<OperationDefinition> getOperations() {
        return operations;
    }

    /**
     * @param operation Operation definition to be added.
     */
    public void addOperation(OperationDefinition operation) {
        operations.add(operation);
    }

    public void validate(ValidationContext context) {
        super.validate(context);
        for (BindingDefinition binding : bindings) {
            binding.validate(context);
        }
        for (BindingDefinition callbackBinding : callbackBindings) {
            callbackBinding.validate(context);
        }
        for (OperationDefinition operation : operations) {
            operation.validate(context);
        }
    }
}
