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
package org.sca4j.binding.hessian.runtime;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.hessian.provision.HessianWireTargetDefinition;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.builder.component.WireAttachException;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.InvocationChain;
import org.sca4j.spi.wire.Wire;

import com.caucho.hessian.io.SerializerFactory;

/**
 * Wire attacher for Hessian binding.
 *
 * @version $Revision: 3137 $ $Date: 2008-03-17 18:31:06 +0000 (Mon, 17 Mar 2008) $
 */
@EagerInit
public class HessianTargetWireAttacher implements TargetWireAttacher<HessianWireTargetDefinition> {
    private final HessianWireAttacherMonitor monitor;
    private final SerializerFactory serializerFactory;

    public HessianTargetWireAttacher(@Monitor HessianWireAttacherMonitor monitor) {
        this.monitor = monitor;
        this.serializerFactory = new SerializerFactory();
    }

    @Init
    public void start() {
        monitor.extensionStarted();
    }

    @Destroy
    public void stop() {
        this.monitor.extensionStopped();
    }

    public void attachToTarget(PhysicalWireSourceDefinition sourceDefinition,
                               HessianWireTargetDefinition targetDefinition,
                               Wire wire) throws WiringException {

        URI id = targetDefinition.getClassLoaderId();
        ClassLoader loader = getClass().getClassLoader();
        URI uri = targetDefinition.getUri();

        try {
            for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getInvocationChains().entrySet()) {
                PhysicalOperationDefinition op = entry.getKey();
                InvocationChain chain = entry.getValue();
                chain.addInterceptor(new HessianTargetInterceptor(uri.toURL(), op.getName(), loader, serializerFactory));
            }
        } catch (MalformedURLException ex) {
            throw new WireAttachException("Invalid URI", sourceDefinition.getUri(), uri, ex);
        }

    }

    public ObjectFactory<?> createObjectFactory(HessianWireTargetDefinition target) throws WiringException {
        throw new AssertionError();
    }
}
