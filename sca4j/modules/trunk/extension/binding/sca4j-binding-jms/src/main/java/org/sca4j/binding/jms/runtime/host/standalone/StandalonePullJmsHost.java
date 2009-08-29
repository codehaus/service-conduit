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
package org.sca4j.binding.jms.runtime.host.standalone;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;

import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.jms.common.SCA4JJmsException;
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
public class StandalonePullJmsHost implements JmsHost, StandalonePullJmsHostMBean {

    private WorkScheduler workScheduler;
    private long readTimeout = 30000L;
    private JMSRuntimeMonitor monitor;
    private int receiverCount = 3;
    private Map<URI, List<ConsumerWorker>> consumerWorkerMap = new HashMap<URI, List<ConsumerWorker>>();
    private Map<URI, Connection> connectionMap = new HashMap<URI, Connection>();
    private Map<URI, ConsumerWorkerTemplate> templateMap = new HashMap<URI, ConsumerWorkerTemplate>();

    /**
     * Injects the monitor.
     * 
     * @param monitor Monitor used for logging.
     */
    public StandalonePullJmsHost(@Monitor JMSRuntimeMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Configurable property for default receiver count.
     * 
     * @param receiverCount Default receiver count.
     */
    @Property
    public void setReceiverCount(int receiverCount) {
        this.receiverCount = receiverCount;
    }

    /**
     * Sets the read timeout.
     * 
     * @param readTimeout Read timeout for blocking receive.
     */
    @Property
    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Injects the work scheduler.
     * 
     * @param workScheduler Work scheduler to be used.
     */
    @Reference
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    /**
     * Stops the receiver threads.
     * 
     * @throws JMSException
     */
    @Destroy
    public void stop() throws JMSException {

        for (List<ConsumerWorker> consumerWorkers : consumerWorkerMap.values()) {
            for (ConsumerWorker worker : consumerWorkers) {
                worker.stop();
            }
        }
        for (Connection connection : connectionMap.values()) {
            JmsHelper.closeQuietly(connection);
        }
        monitor.jmsRuntimeStop();

    }

    public void unregisterListener(URI serviceUri) {
        List<ConsumerWorker> workers = consumerWorkerMap.remove(serviceUri);
        for (ConsumerWorker consumerWorker : workers) {
            consumerWorker.stop();
        }
        JmsHelper.closeQuietly(connectionMap.remove(serviceUri));
        templateMap.remove(serviceUri);
    }

    public void registerResponseListener(JMSObjectFactory requestJMSObjectFactory, JMSObjectFactory responseJMSObjectFactory, ResponseMessageListener messageListener,
            TransactionType transactionType, TransactionHandler transactionHandler, ClassLoader cl, URI serviceUri) {

        try {

            Connection connection = requestJMSObjectFactory.getConnection();
            List<ConsumerWorker> consumerWorkers = new ArrayList<ConsumerWorker>();

            ConsumerWorkerTemplate template = new ConsumerWorkerTemplate(transactionHandler, transactionType, messageListener, responseJMSObjectFactory, requestJMSObjectFactory,
                    readTimeout, cl, monitor);
            templateMap.put(serviceUri, template);

            for (int i = 0; i < receiverCount; i++) {
                ConsumerWorker work = new ConsumerWorker(template);
                workScheduler.scheduleWork(work);
                consumerWorkers.add(work);
            }

            connection.start();
            connectionMap.put(serviceUri, connection);
            consumerWorkerMap.put(serviceUri, consumerWorkers);

        } catch (JMSException ex) {
            throw new SCA4JJmsException("Unable to activate service", ex);
        }

        monitor.registerListener(requestJMSObjectFactory.getDestination());

    }

    // ---------------------------------- Start of management operations
    // -------------------

    public int getReceiverCount(String service) {

        URI serviceUri = URI.create(service);
        List<ConsumerWorker> consumerWorkers = consumerWorkerMap.get(serviceUri);
        if (consumerWorkers == null) {
            throw new IllegalArgumentException("Unknown service:" + service);
        }
        return consumerWorkers.size();

    }

    public void setReceiverCount(String service, int receiverCount) {

        URI serviceUri = URI.create(service);
        List<ConsumerWorker> consumerWorkers = consumerWorkerMap.get(serviceUri);
        if (consumerWorkers == null) {
            throw new IllegalArgumentException("Unknown service:" + service);
        }

        if (receiverCount == consumerWorkers.size()) {
            return;
        }

        ConsumerWorkerTemplate template = templateMap.get(serviceUri);
        for (ConsumerWorker consumerWorker : consumerWorkers) {
            consumerWorker.stop();
        }
        consumerWorkers.clear();

        for (int i = 0; i < receiverCount; i++) {
            ConsumerWorker consumerWorker = new ConsumerWorker(template);
            workScheduler.scheduleWork(consumerWorker);
            consumerWorkers.add(consumerWorker);
        }

    }

    public List<String> getReceivers() {

        List<String> receivers = new ArrayList<String>();
        for (URI destination : connectionMap.keySet()) {
            receivers.add(destination.toString());
        }
        return receivers;

    }

    // ---------------------------------- End of management operations
    // -------------------

}
