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
package org.sca4j.scdl;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a service offered by a component
 *
 * @version $Rev: 5224 $ $Date: 2008-08-19 19:07:18 +0100 (Tue, 19 Aug 2008) $
 */
public class ServiceDefinition extends AbstractPolicyAware {
    private static final long serialVersionUID = -3331868180749278028L;
    private String name;
    private ServiceContract serviceContract;
    private boolean management;
    private final List<BindingDefinition> bindings = new ArrayList<BindingDefinition>();
    private final List<BindingDefinition> callbackBindings = new ArrayList<BindingDefinition>();
    private final List<OperationDefinition> operations = new ArrayList<OperationDefinition>();

    public ServiceDefinition(String name) {
        this(name, null);
    }

    public ServiceDefinition(String name, ServiceContract serviceContract) {
        this.name = name;
        this.serviceContract = serviceContract;
    }

    /**
     * Return the name of this service definition.
     *
     * @return the name of this service definition
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if this is a management service.
     * <p/>
     * A management service is intended for administration of a component rather than normal interactions with it. As such, it is not considered a
     * valid target for wiring (including autowire).
     *
     * @return true if this is a management service
     */
    public boolean isManagement() {
        return management;
    }

    /**
     * Sets whether this is a management service
     *
     * @param management true if this is a management service
     */
    public void setManagement(boolean management) {
        this.management = management;
    }

    /**
     * Returns the service contract
     *
     * @return the service contract
     */
    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    /**
     * Sets the service contract
     *
     * @param contract the service contract
     */
    public void setServiceContract(ServiceContract contract) {
        this.serviceContract = contract;
    }

    /**
     * Returns the bindings configured for the service
     *
     * @return the bindings configured for the service
     */
    public List<BindingDefinition> getBindings() {
        return bindings;
    }

    /**
     * Configures the service with a binding
     *
     * @param binding the binding
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
