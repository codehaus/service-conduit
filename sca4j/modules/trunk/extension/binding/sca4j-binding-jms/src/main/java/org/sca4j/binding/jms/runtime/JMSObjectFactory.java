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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.sca4j.binding.jms.runtime.helper.JmsHelper;

/**
 * This is a factory class handling cache, recover. etc for a
 * connectionFactory/destination pair related JMS objects.
 */
public class JMSObjectFactory {
    /**
     * Cache level
     */
    private int cache_level = 1;
    /**
     * JMS connection shared by session
     */
    private Connection sharedConnection;
    /**
     * JMS Session shared by consumer
     */
    private Session sharedSession;
    /**
     * JMS Connection Factory
     */
    private final ConnectionFactory connectionFactory;
    /**
     * JMS destination
     */
    private final Destination destination;

    private static final int CACHE_CONNECTION = 1;

    private static final int CACHE_SESSION = 2;

    public JMSObjectFactory(ConnectionFactory connectionFactory, Destination destination, int cacheLevel) {
        this.connectionFactory = connectionFactory;
        this.destination = destination;
        this.cache_level = cacheLevel;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Connection getConnection() throws JMSException {
        if (sharedConnection == null) {
            sharedConnection = createConnection();
        }
        // TODO check connection
        return sharedConnection;
    }

    private Connection createConnection() throws JMSException {
        return getConnectionFactory().createConnection();
    }

    public Destination getDestination() {
        return destination;
    }

    public Session getSession() throws JMSException {
        if (sharedSession == null) {
            sharedSession = createSession();
        }
        // TODO check session
        return sharedSession;
    }

    public Session createSession() throws JMSException {
        return getConnection().createSession(true, Session.SESSION_TRANSACTED);
    }

    /**
     * Destroy object corresponding to cache_level
     */
    public void recycle() {
        if (cache_level < CACHE_CONNECTION) {
            if (sharedConnection != null) {
                JmsHelper.closeQuietly(sharedConnection);
                sharedConnection = null;
            }
        }
        if (cache_level < CACHE_SESSION) {
            if (sharedSession != null) {
                // already closed by connection.
                sharedSession = null;
            }
        }
    }

    /**
     * Close any disposable resources.
     */
    public void close() {
        if (sharedConnection != null) {
            JmsHelper.closeQuietly(sharedConnection);
        }
    }

}
