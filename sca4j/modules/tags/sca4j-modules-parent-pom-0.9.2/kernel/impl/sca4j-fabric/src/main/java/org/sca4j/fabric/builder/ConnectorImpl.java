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
package org.sca4j.fabric.builder;

import java.util.Map;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.fabric.wire.InvocationChainImpl;
import org.sca4j.fabric.wire.WireImpl;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.BuilderException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.builder.interceptor.InterceptorBuilder;
import org.sca4j.spi.model.physical.PhysicalInterceptorDefinition;
import org.sca4j.spi.model.physical.PhysicalOperationPair;
import org.sca4j.spi.model.physical.PhysicalWireDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

/**
 * The default connector implmentation.
 *
 * @version $$Rev: 5254 $$ $$Date: 2008-08-23 03:36:39 +0100 (Sat, 23 Aug 2008) $$
 */
public class ConnectorImpl implements Connector {
    private Map<Class<? extends PhysicalInterceptorDefinition>, InterceptorBuilder<?, ?>> interceptorBuilders;
    private Map<Class<? extends PhysicalWireSourceDefinition>, SourceWireAttacher<? extends PhysicalWireSourceDefinition>> sourceAttachers;
    private Map<Class<? extends PhysicalWireTargetDefinition>, TargetWireAttacher<? extends PhysicalWireTargetDefinition>> targetAttachers;

    public ConnectorImpl() {
    }

    @Reference
    public void setInterceptorBuilders(Map<Class<? extends PhysicalInterceptorDefinition>, InterceptorBuilder<?, ?>> interceptorBuilders) {
        this.interceptorBuilders = interceptorBuilders;
    }

    @Reference(required = false)
    public void setSourceAttachers(Map<Class<? extends PhysicalWireSourceDefinition>, SourceWireAttacher<? extends PhysicalWireSourceDefinition>> sourceAttachers) {
        this.sourceAttachers = sourceAttachers;
    }

    @Reference
    public void setTargetAttachers(Map<Class<? extends PhysicalWireTargetDefinition>, TargetWireAttacher<? extends PhysicalWireTargetDefinition>> targetAttachers) {
        this.targetAttachers = targetAttachers;
    }

    public void connect(PhysicalWireDefinition definition) throws BuilderException {
        PhysicalWireSourceDefinition sourceDefinition = definition.getSource();
        SourceWireAttacher<PhysicalWireSourceDefinition> sourceAttacher = getAttacher(sourceDefinition);
        if (sourceAttacher == null) {
            throw new AttacherNotFoundException("Source attacher not found for type: " + sourceDefinition.getClass());
        }
        PhysicalWireTargetDefinition targetDefinition = definition.getTarget();
        TargetWireAttacher<PhysicalWireTargetDefinition> targetAttacher = getAttacher(targetDefinition);
        if (targetAttacher == null) {
            throw new AttacherNotFoundException("Target attacher not found for type: " + targetDefinition.getClass());
        }

        if (definition.isOptimizable()) {
            ObjectFactory<?> objectFactory = targetAttacher.createObjectFactory(targetDefinition);
            sourceAttacher.attachObjectFactory(sourceDefinition, objectFactory, targetDefinition);
        } else {
            Wire wire = createWire(definition);
            sourceAttacher.attachToSource(sourceDefinition, targetDefinition, wire);
            targetAttacher.attachToTarget(sourceDefinition, targetDefinition, wire);
        }
    }

    public void disconnect(PhysicalWireDefinition definition) throws BuilderException {
        PhysicalWireSourceDefinition sourceDefinition = definition.getSource();
        SourceWireAttacher<PhysicalWireSourceDefinition> sourceAttacher =
                getAttacher(sourceDefinition);

        PhysicalWireTargetDefinition targetDefinition = definition.getTarget();

        if (definition.isOptimizable()) {
            sourceAttacher.attachObjectFactory(sourceDefinition, null, targetDefinition);
        } else {
            Wire wire = createWire(definition);
            sourceAttacher.detachFromSource(sourceDefinition, targetDefinition, wire);
        }
    }

    Wire createWire(PhysicalWireDefinition definition) throws BuilderException {
        Wire wire = new WireImpl();
        for (PhysicalOperationPair operation : definition.getOperations()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            for (PhysicalInterceptorDefinition interceptorDefinition : operation.getInterceptors()) {
                InterceptorBuilder<? super PhysicalInterceptorDefinition, ?> builder = getBuilder(interceptorDefinition);
                Interceptor interceptor = builder.build(interceptorDefinition);
                chain.addInterceptor(interceptor);
            }
            wire.addInvocationChain(operation, chain);
        }
        return wire;
    }


    @SuppressWarnings("unchecked")
    protected <PID extends PhysicalInterceptorDefinition> InterceptorBuilder<PID, ?> getBuilder(PID definition) {
        return (InterceptorBuilder<PID, ?>) interceptorBuilders.get(definition.getClass());

    }

    @SuppressWarnings("unchecked")
    protected <PWSD extends PhysicalWireSourceDefinition> SourceWireAttacher<PWSD> getAttacher(PWSD source) {
        return (SourceWireAttacher<PWSD>) sourceAttachers.get(source.getClass());
    }

    @SuppressWarnings("unchecked")
    protected <PWSD extends PhysicalWireTargetDefinition> TargetWireAttacher<PWSD> getAttacher(PWSD target) {
        return (TargetWireAttacher<PWSD>) targetAttachers.get(target.getClass());
    }
}
