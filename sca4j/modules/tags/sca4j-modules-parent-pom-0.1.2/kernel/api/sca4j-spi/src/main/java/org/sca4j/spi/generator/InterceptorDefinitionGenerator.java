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
package org.sca4j.spi.generator;

import org.w3c.dom.Element;

import org.sca4j.scdl.Operation;
import org.sca4j.spi.model.instance.LogicalBinding;
import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;

/**
 * Interface for the interceptor definition generator.
 *
 * @version $Revision$ $Date$
 */
public interface InterceptorDefinitionGenerator {

    /**
     * Generates an interceptor definition from the policy set extension. Implementations may return null if an interceptor should not be added to a
     * wire.
     *
     * @param policyDefinition Policy set definition.
     * @param operation        Operation against which the interceptor is generated.
     * @param logicalBinding   Logical binding on the service or reference.
     * @return Physical interceptor definition or null if an interceptor should not be added.
     * @throws GenerationException i an exception occurs during generation
     */
    PhysicalInterceptorDefinition generate(Element policyDefinition,
                                           Operation<?> operation,
                                           LogicalBinding<?> logicalBinding) throws GenerationException;

}
