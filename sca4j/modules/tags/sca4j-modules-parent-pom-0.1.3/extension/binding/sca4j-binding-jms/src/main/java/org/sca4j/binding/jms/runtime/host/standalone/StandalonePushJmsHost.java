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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.JMSRuntimeMonitor;
import org.sca4j.binding.jms.runtime.ResponseMessageListener;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.binding.jms.runtime.host.JmsHost;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.host.work.WorkScheduler;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * Service handler for JMS.
 *
 * @version $Revsion$ $Date: 2007-05-22 00:19:04 +0100 (Tue, 22 May 2007) $
 */
public class StandalonePushJmsHost implements JmsHost {

    private WorkScheduler workScheduler;
    private Connection connection;
    private JMSRuntimeMonitor monitor;
    private int receiverCount = 3;
    private Map<URI,JMSMessageListenerInvoker> jmsMessageListenerInvokers = new HashMap<URI,JMSMessageListenerInvoker>();

    /**
     * Injects the monitor.
     * @param monitor Monitor to be injected.
     */
    public StandalonePushJmsHost(@Monitor JMSRuntimeMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Injects the work scheduler.     *
     * @param workScheduler Work scheduler to be used.
     */
    @Reference
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    /**
     * Configurable property for default receiver count.
     * @param receiverCount Default receiver count.
     */
    @Property
    public void setReceiverCount(int receiverCount) {
        this.receiverCount = receiverCount;
    }

    /**
     * Stops the receiver threads.
     * @throws JMSException
     */
    @Destroy
    public void stop() throws JMSException {
        for (JMSMessageListenerInvoker invoker : jmsMessageListenerInvokers.values()) {
            invoker.stop();
        }
        JmsHelper.closeQuietly(connection);
        jmsMessageListenerInvokers.clear();
        monitor.jmsRuntimeStop();
    }

    public void registerResponseListener(JMSObjectFactory requestJMSObjectFactory, 
                                         JMSObjectFactory responseJMSObjectFactory,
                                         ResponseMessageListener messageListener, 
                                         TransactionType transactionType,
                                         TransactionHandler transactionHandler,
                                         ClassLoader cl,
                                         URI serviceUri) {
        JMSMessageListenerInvoker invoker = new JMSMessageListenerInvoker(
                requestJMSObjectFactory, responseJMSObjectFactory, messageListener, transactionType, transactionHandler, workScheduler);
        invoker.start(receiverCount);
        jmsMessageListenerInvokers.put(serviceUri,invoker);

    }
    
    public void unregisterListener(URI serviceUri){
    	JMSMessageListenerInvoker invoker = jmsMessageListenerInvokers.remove(serviceUri);
    	invoker.stop();  	
    }

}
