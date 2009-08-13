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
    private int poolSize = 3; //default value
    private final WorkScheduler workScheduler;

    /**
     * Initializes the server sessions.
     * @param workScheduler
     * @param serverSessions Server sessions.
     */
    public StandaloneServerSessionPool(JMSObjectFactory jmsObjectFactory,
            TransactionHandler transactionHandler,
            MessageListener listener,
            TransactionType transactionType,
            WorkScheduler workScheduler,
            int receiverCount) {
        this.jmsObjectFactory = jmsObjectFactory;
        this.transactionHandler = transactionHandler;
        this.transactionType = transactionType;
        this.workScheduler = workScheduler;
        this.poolSize =receiverCount;
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
                throw new SCA4JJmsException("Error when initialize ServerSessionPool",e);
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
     * @param serverSessions
     */
    public void StartServerSession(final ServerSession serverSession){
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
                    throw new SCA4JJmsException("Error when start ServerSession",jmse);
                } finally {
                    returnSession(serverSession);
                }
            }
        });

    }

}
