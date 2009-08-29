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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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

package org.sca4j.binding.jms.runtime.lookup.destination;

import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.jms.TopicConnection;

import org.sca4j.binding.jms.common.DestinationDefinition;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;

/**
 * The destination is never looked up, it is always created.
 * 
 */
public class AlwaysDestinationStrategy implements DestinationStrategy {

    /**
     * @see org.sca4j.binding.jms.runtime.lookup.destination.DestinationStrategy#getDestination(org.sca4j.binding.jms.common.DestinationDefinition,
     *      javax.jms.ConnectionFactory, java.util.Hashtable)
     */
    public Destination getDestination(DestinationDefinition definition, ConnectionFactory cf, Hashtable<String, String> env) {

        Connection connection = null;

        try {

            String name = definition.getName();
            connection = cf.createConnection();

            switch (definition.getDestinationType()) {
            case queue:
                QueueConnection qc = (QueueConnection) connection;
                return qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE).createQueue(name);
            case topic:
                TopicConnection tc = (TopicConnection) connection;
                return tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE).createQueue(name);
            default:
                throw new IllegalArgumentException("Unknown destination type");
            }

        } catch (JMSException ex) {
            throw new SCA4JJmsException("Unable to create destination", ex);
        } finally {
            JmsHelper.closeQuietly(connection);
        }

    }

}
