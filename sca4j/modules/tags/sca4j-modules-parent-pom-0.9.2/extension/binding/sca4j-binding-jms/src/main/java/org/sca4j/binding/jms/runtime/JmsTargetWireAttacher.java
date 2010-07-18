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
package org.sca4j.binding.jms.runtime;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.transaction.TransactionManager;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.provision.JmsWireTargetDefinition;
import org.sca4j.binding.jms.runtime.helper.JndiHelper;
import org.sca4j.binding.jms.runtime.interceptor.OneWayGlobalInterceptor;
import org.sca4j.binding.jms.runtime.interceptor.OneWayLocalInterceptor;
import org.sca4j.binding.jms.runtime.interceptor.TwoWayGlobalInterceptor;
import org.sca4j.binding.jms.runtime.interceptor.TwoWayLocalInterceptor;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.TargetWireAttacher;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.model.physical.PhysicalWireSourceDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches the reference end of a wire to a JMS queue.
 *
 * @version $Revision: 5322 $ $Date: 2008-09-02 20:15:34 +0100 (Tue, 02 Sep
 *          2008) $
 */
public class JmsTargetWireAttacher implements TargetWireAttacher<JmsWireTargetDefinition> {

    @Reference(required = false) public TransactionManager transactionManager;

    public void attachToTarget(PhysicalWireSourceDefinition sourceDefinition, JmsWireTargetDefinition targetDefinition, Wire wire) throws WiringException {

        JmsBindingMetadata metadata = targetDefinition.getMetadata();

        Hashtable<String, String> env = new Hashtable<String, String>();
        if (metadata.jndiUrl != null && !"".equals(metadata.jndiUrl)) {
            env.put(Context.PROVIDER_URL, metadata.jndiUrl);
        }
        if (metadata.initialContextFactory != null && !"".equals(metadata.initialContextFactory)) {
            env.put(Context.INITIAL_CONTEXT_FACTORY, metadata.initialContextFactory);
        }
        TransactionType transactionType = targetDefinition.getTransactionType();

        String connectionFactoryName = metadata.connectionFactoryName;
        String destinationName = metadata.destinationName;
        String responseDestinationName = metadata.responseDestinationName;
        JMSObjectFactory jmsFactory = buildObjectFactory(connectionFactoryName, destinationName, responseDestinationName, env);

        Interceptor interceptor = null;

        boolean twoWay = isTwoWay(wire);
        if (twoWay) {
            if (transactionType == TransactionType.GLOBAL) {
                interceptor = new TwoWayGlobalInterceptor(jmsFactory, transactionManager, metadata.correlation, wire);
            } else {
                interceptor = new TwoWayLocalInterceptor(jmsFactory, metadata.correlation, wire);
            }
        } else {
            if (transactionType == TransactionType.GLOBAL) {
                interceptor = new OneWayGlobalInterceptor(jmsFactory, transactionManager, wire);
            } else {
                interceptor = new OneWayLocalInterceptor(jmsFactory, wire);
            }
        }
        wire.getInvocationChains().entrySet().iterator().next().getValue().addInterceptor(interceptor);

    }

    public ObjectFactory<?> createObjectFactory(JmsWireTargetDefinition target) throws WiringException {
        throw new UnsupportedOperationException();
    }

    private JMSObjectFactory buildObjectFactory(String connectionFactoryName, String destinationName, String responseDestinationName, Hashtable<String, String> env) {
        ConnectionFactory connectionFactory = JndiHelper.lookup(connectionFactoryName, env);
        Destination destination = JndiHelper.lookup(destinationName, env);
        Destination responseDestination = JndiHelper.lookup(responseDestinationName, env);
        return new JMSObjectFactory(connectionFactory, destination, responseDestination);
    }

    private boolean isTwoWay(Wire wire) {
        PhysicalOperationDefinition operationDef = wire.getInvocationChains().entrySet().iterator().next().getKey().getSourceOperation();
        String returnType = operationDef.getReturnType();

        return returnType != null && returnType != "void";
    }

}
