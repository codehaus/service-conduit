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
package org.sca4j.binding.oracle.aq.runtime;

import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;

public class OperationMetadata {
    
    private Class<?> inputType;
    private Class<?> outputType;
    private String name;
    private InvocationChain invocationChain;
    
    public OperationMetadata(PhysicalOperationDefinition operation, InvocationChain invocationChain) throws ClassNotFoundException {
        this.invocationChain = invocationChain;
        this.name = operation.getName();
        inputType = Class.forName(operation.getParameters().get(0));
        if (operation.getReturnType() != null) {
            outputType = Class.forName(operation.getReturnType());
        }
    }

    public Class<?> getInputType() {
        return inputType;
    }

    public Class<?> getOutputType() {
        return outputType;
    }

    public String getName() {
        return name;
    }

    public InvocationChain getInvocationChain() {
        return invocationChain;
    }
    
    public boolean isTwoWay() {
        return outputType != null && !Void.class.equals(outputType);
    }

}
