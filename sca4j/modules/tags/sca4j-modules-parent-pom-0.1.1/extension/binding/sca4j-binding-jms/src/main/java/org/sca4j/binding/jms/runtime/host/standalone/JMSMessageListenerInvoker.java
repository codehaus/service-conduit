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

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ServerSessionPool;
import javax.jms.Session;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.ResponseMessageListener;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.host.work.WorkScheduler;
/**
 *
 * A container class used to support MessageListener with ServerSessionPool.
 *
 */
public class JMSMessageListenerInvoker implements MessageListener {
    /** Request JMS object factory*/
    private JMSObjectFactory requestJMSObjectFactory = null;
    /** Response JMS object factory*/
    private JMSObjectFactory responseJMSObjectFactory;
    /** ResponseMessageListenerImpl invoked by this invoker */
    private ResponseMessageListener messageListener = null;
    /** Transaction Type */
    private TransactionType transactionType;
    /** Transaction Handler*/
    private TransactionHandler transactionHandler;
    /** WorkScheduler passed to serverSessionPool */
    private WorkScheduler workScheduler;

    public JMSMessageListenerInvoker(JMSObjectFactory requestJMSObjectFactory,
            JMSObjectFactory responseJMSObjectFactory,
            ResponseMessageListener messageListener,
            TransactionType transactionType,
            TransactionHandler transactionHandler, WorkScheduler workScheduler) {
        this.requestJMSObjectFactory = requestJMSObjectFactory;
        this.responseJMSObjectFactory = responseJMSObjectFactory;
        this.messageListener = messageListener;
        this.transactionType = transactionType;
        this.transactionHandler = transactionHandler;
        this.workScheduler = workScheduler;
    }

    public void start(int receiverCount) {
        ServerSessionPool serverSessionPool = createServerSessionPool(receiverCount);
        try {
            Connection connection = requestJMSObjectFactory.getConnection();
            connection.createConnectionConsumer(requestJMSObjectFactory
                    .getDestination(), null, serverSessionPool, 1);
            connection.start();
        } catch (JMSException e) {
            throw new SCA4JJmsException("Error when register Listener",e);

        }
    }

    private StandaloneServerSessionPool createServerSessionPool(int receiverCount) {
        return new StandaloneServerSessionPool(requestJMSObjectFactory,
                transactionHandler, this, transactionType,workScheduler,receiverCount);
    }

    public void stop() {
        requestJMSObjectFactory.close();
        responseJMSObjectFactory.close();
    }

    public void onMessage(Message message) {
        try {
            Session responseSession = responseJMSObjectFactory.createSession();
            if (transactionType == TransactionType.GLOBAL) {
                transactionHandler.enlist(responseSession);
            }
            Destination responseDestination = responseJMSObjectFactory
                    .getDestination();
            messageListener.onMessage(message, responseSession,
                    responseDestination);
            if (transactionType == TransactionType.GLOBAL) {
                transactionHandler.commit();
            }else if(transactionType == TransactionType.LOCAL){
                responseSession.commit();
            }
            responseJMSObjectFactory.recycle();
        } catch (JMSException e) {
            throw new SCA4JJmsException("Error when invoking Listener",e);
        } catch (RuntimeException e) {
            try{
                if (transactionType == TransactionType.GLOBAL) {
                    transactionHandler.rollback();
                }
            }catch(Exception ne){
                //ignore
            }
            throw e;
        }
    }


}
