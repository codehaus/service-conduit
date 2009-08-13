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

import javax.jms.JMSException;
import javax.jms.ServerSession;
import javax.jms.Session;

/**
 * Server session used in standalone JMS host.
 * 
 * @version $Revision$ $Date$
 */
public class StandaloneServerSession implements ServerSession {

    private StandaloneServerSessionPool serverSessionPool;
    private Session session;

    /**
     * Initializes the server session.
     * 
     * @param session Underlying JMS session.
     * @param serverSessionPool Server session pool.
     * @param transactionHandler Transaction handler (XA or Local)
     */
    public StandaloneServerSession(Session session, StandaloneServerSessionPool serverSessionPool) {
        this.session = session;
        this.serverSessionPool = serverSessionPool;
    }

    /**
     * @see javax.jms.ServerSession#getSession()
     */
    public Session getSession() throws JMSException {
        return session;
    }

    /**
     * @see javax.jms.ServerSession#start()
     */
    public void start() throws JMSException {
        serverSessionPool.StartServerSession(this);
    }

}
