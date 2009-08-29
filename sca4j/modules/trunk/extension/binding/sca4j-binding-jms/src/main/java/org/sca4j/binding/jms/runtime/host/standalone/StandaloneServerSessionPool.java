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
package org.sca4j.binding.jms.runtime.host.standalone;

import java.util.Stack;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ServerSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.WorkScheduler;

/**
 * Server session pool used by the standalone JMS server.
 * 
 * @version $Revision$ $Date$
 */
public class StandaloneServerSessionPool implements ServerSessionPool {

    // Available server sessions
    private Stack<ServerSession> serverSessions = new Stack<ServerSession>();
    private final JMSObjectFactory jmsObjectFactory;
    private final TransactionHandler transactionHandler;
    private final TransactionType transactionType;
    private int poolSize = 3; // default value
    private final WorkScheduler workScheduler;

    /**
     * Initializes the server sessions.
     * 
     * @param workScheduler
     * @param serverSessions Server sessions.
     */
    public StandaloneServerSessionPool(JMSObjectFactory jmsObjectFactory, TransactionHandler transactionHandler, MessageListener listener, TransactionType transactionType,
            WorkScheduler workScheduler, int receiverCount) {
        this.jmsObjectFactory = jmsObjectFactory;
        this.transactionHandler = transactionHandler;
        this.transactionType = transactionType;
        this.workScheduler = workScheduler;
        this.poolSize = receiverCount;
        initSessions(listener);
    }

    private void initSessions(MessageListener listener) throws SCA4JJmsException {
        for (int i = 0; i < poolSize; i++) {
            try {
                Session session = jmsObjectFactory.createSession();
                session.setMessageListener(listener);
                ServerSession serverSession = new StandaloneServerSession(session, this);
                serverSessions.add(serverSession);
            } catch (JMSException e) {
                throw new SCA4JJmsException("Error when initialize ServerSessionPool", e);
            }
        }
    }

    /**
     * Closes the underlying sessions.
     */
    public void stop() throws JMSException {
        ServerSession serverSession = null;
        while ((serverSession = getServerSession()) != null) {
            JmsHelper.closeQuietly(serverSession.getSession());
        }
    }

    /**
     * @see javax.jms.ServerSessionPool#getServerSession()
     */
    public ServerSession getServerSession() throws JMSException {
        synchronized (serverSessions) {
            while (serverSessions.isEmpty()) {
                try {
                    serverSessions.wait();
                } catch (InterruptedException e) {
                    throw new JMSException("Unable to get a server session");
                }
            }
            return serverSessions.pop();
        }
    }

    /**
     * Returns the session to the pool.
     * 
     * @param serverSession Server session to be returned.
     */
    protected void returnSession(ServerSession serverSession) {
        synchronized (serverSessions) {
            serverSessions.push(serverSession);
            serverSessions.notify();
        }
    }

    /**
     * Start a JMS Session asynchronously.
     * 
     * @param serverSessions
     */
    public void StartServerSession(final ServerSession serverSession) {
        workScheduler.scheduleWork(new DefaultPausableWork() {
            public void execute() {
                try {
                    Session session = serverSession.getSession();
                    if (transactionType == TransactionType.GLOBAL) {
                        transactionHandler.enlist(session);
                    }
                    session.run();
                    if (transactionType == TransactionType.LOCAL) {
                        session.commit();
                    }
                } catch (Exception jmse) {
                    throw new SCA4JJmsException("Error when start ServerSession", jmse);
                } finally {
                    returnSession(serverSession);
                }
            }
        });

    }

}
