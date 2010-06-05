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
package org.sca4j.binding.jms.runtime;

import java.net.URI;
import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.Context;
import javax.transaction.TransactionManager;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.provision.JmsWireSourceDefinition;
import org.sca4j.binding.jms.runtime.helper.JndiHelper;
import org.sca4j.binding.jms.runtime.host.JmsHost;
import org.sca4j.spi.ObjectFactory;
import org.sca4j.spi.builder.WiringException;
import org.sca4j.spi.builder.component.SourceWireAttacher;
import org.sca4j.spi.model.physical.PhysicalWireTargetDefinition;
import org.sca4j.spi.wire.Wire;

/**
 * Attaches the target end of a wire (a service) to a JMS queue.
 *
 * @version $Revision: 5363 $ $Date: 2008-09-09 01:39:36 +0100 (Tue, 09 Sep
 *          2008) $
 */
public class JmsSourceWireAttacher implements SourceWireAttacher<JmsWireSourceDefinition> {

    @Reference public JmsHost jmsHost;
    @Reference (required = false) public TransactionManager transactionManager;

    public void attachToSource(JmsWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {

        URI serviceUri = target.getUri();

        JmsBindingMetadata metadata = source.getMetadata();
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.PROVIDER_URL, metadata.jndiUrl);
        env.put(Context.INITIAL_CONTEXT_FACTORY, metadata.initialContextFactory);
        TransactionType transactionType = source.getTransactionType();

        String connectionFactoryName = metadata.connectionFactoryName;
        String destinationName = metadata.destinationName;
        String responseDestinationName = metadata.responseDestinationName;
        JMSObjectFactory jmsFactory = buildObjectFactory(connectionFactoryName, destinationName, responseDestinationName, env);

        jmsHost.register(jmsFactory, transactionType, wire, metadata, serviceUri);

    }

    public void detachFromSource(JmsWireSourceDefinition source, PhysicalWireTargetDefinition target, Wire wire) throws WiringException {
    }

    public void attachObjectFactory(JmsWireSourceDefinition source, ObjectFactory<?> objectFactory, PhysicalWireTargetDefinition definition) throws WiringException {
        throw new UnsupportedOperationException();
    }

    private JMSObjectFactory buildObjectFactory(String connectionFactoryName, String destinationName, String responseDestinationName, Hashtable<String, String> env) {
        Destination responseDestination = null;
        ConnectionFactory connectionFactory = JndiHelper.lookup(connectionFactoryName, env);
        Destination destination = JndiHelper.lookup(destinationName, env);
        if(responseDestinationName != null) { responseDestination = JndiHelper.lookup(responseDestinationName, env);}
        return new JMSObjectFactory(connectionFactory, destination, responseDestination);
    }

}
