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

import javax.transaction.TransactionManager;

import org.oasisopen.sca.annotation.Destroy;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.JMSRuntimeMonitor;
import org.sca4j.binding.jms.runtime.host.JmsHost;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.spi.wire.Wire;

/**
 * Service handler for JMS.
 * 
 * @version $Revsion$ $Date: 2007-05-22 00:19:04 +0100 (Tue, 22 May 2007) $
 */
public class StandalonePullJmsHost implements JmsHost {

    @Reference public WorkScheduler workScheduler;
    @Monitor public JMSRuntimeMonitor monitor;
    @Reference public TransactionManager transactionManager;
    
    private Map<URI, List<ConsumerWorker>> consumerWorkerMap = new HashMap<URI, List<ConsumerWorker>>();

    public void register(JMSObjectFactory jmsFactory, TransactionType transactionType, Wire wire, JmsBindingMetadata metadata, URI serviceUri) {

        List<ConsumerWorker> consumerWorkers = new ArrayList<ConsumerWorker>();

        ConsumerWorkerTemplate template = new ConsumerWorkerTemplate();
        template.transactionManager = transactionManager;
        template.transactionType = transactionType;
        template.jmsFactory = jmsFactory;; 
        template.pollingInterval = metadata.pollingInterval;
        template.exceptionTimeout = metadata.exceptionTimeout;
        template.monitor = monitor;
        template.metadata = metadata;
        template.wire = wire;

        for (int i = 0; i < metadata.consumerCount; i++) {
            ConsumerWorker work = new ConsumerWorker(template);
            workScheduler.scheduleWork(work);
            consumerWorkers.add(work);
        }

        consumerWorkerMap.put(serviceUri, consumerWorkers);

    }
    
    /**
     * Stops all the consumers.
     */
    @Destroy
    public void destroy() {
        for (List<ConsumerWorker> consumerWorkers : consumerWorkerMap.values()) {
            for (ConsumerWorker consumerWorker : consumerWorkers) {
                consumerWorker.stop();
            }
        }
    }

}
