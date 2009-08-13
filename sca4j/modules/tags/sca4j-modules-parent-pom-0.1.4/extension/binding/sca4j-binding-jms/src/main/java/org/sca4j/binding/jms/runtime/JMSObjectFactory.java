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
