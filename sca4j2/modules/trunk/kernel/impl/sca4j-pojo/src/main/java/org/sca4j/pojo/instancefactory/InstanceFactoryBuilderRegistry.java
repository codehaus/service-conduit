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
package org.sca4j.pojo.instancefactory;

import org.sca4j.spi.component.InstanceFactoryProvider;
import org.sca4j.pojo.provision.InstanceFactoryDefinition;

/**
 * Registry for instance factory builders.
 *
 * @version $Revision: 5245 $ $Date: 2008-08-20 22:25:50 +0100 (Wed, 20 Aug 2008) $
 */
public interface InstanceFactoryBuilderRegistry {

    /**
     * Registers an instance factory provider builder.
     *
     * @param ifpdClass Instance factory provider definition class.
     * @param builder   Instance factory provider builder.
     */
    <IFPD extends InstanceFactoryDefinition> void register(Class<?> ifpdClass, InstanceFactoryBuilder<?, IFPD> builder);

    /**
     * Builds an instance factory provider from a definition.
     *
     * @param providerDefinition Provider definition.
     * @param cl                 Clasloader to use.
     * @param <T>                the type of instance the InstanceFactory creates
     * @return Instance factory provider.
     * @throws InstanceFactoryBuilderException
     *          if an error occurs building the factory
     */
    <T> InstanceFactoryProvider<T> build(InstanceFactoryDefinition providerDefinition, ClassLoader cl) throws InstanceFactoryBuilderException;
}
