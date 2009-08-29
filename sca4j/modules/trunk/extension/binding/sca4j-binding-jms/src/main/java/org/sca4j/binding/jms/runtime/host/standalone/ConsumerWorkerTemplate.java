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

import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.JMSRuntimeMonitor;
import org.sca4j.binding.jms.runtime.ResponseMessageListener;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;

/**
 * A thread pull message from destination and invoke Message listener.
 * 
 * @version $Revision$ $Date$
 */
public class ConsumerWorkerTemplate {

    private final TransactionHandler transactionHandler;
    private final ResponseMessageListener listener;
    private final long readTimeout;
    private final TransactionType transactionType;
    private final ClassLoader cl;
    private final JMSObjectFactory responseJMSObjectFactory;
    private final JMSObjectFactory requestJMSObjectFactory;
    private JMSRuntimeMonitor monitor;

    /**
     * @param session
     * @param transactionHandler
     * @param transactionType
     * @param consumer
     * @param listener
     * @param responseJMSObjectFactory
     * @param readTimeout
     * @param cl
     * @param monitor
     */
    public ConsumerWorkerTemplate(TransactionHandler transactionHandler, TransactionType transactionType, ResponseMessageListener listener,
            JMSObjectFactory responseJMSObjectFactory, JMSObjectFactory requestJMSObjectFactory, long readTimeout, ClassLoader cl, JMSRuntimeMonitor monitor) {
        this.transactionHandler = transactionHandler;
        this.transactionType = transactionType;
        this.listener = listener;
        this.responseJMSObjectFactory = responseJMSObjectFactory;
        this.requestJMSObjectFactory = requestJMSObjectFactory;
        this.readTimeout = readTimeout;
        this.cl = cl;
        this.monitor = monitor;
    }

    public TransactionHandler getTransactionHandler() {
        return transactionHandler;
    }

    public ResponseMessageListener getListener() {
        return listener;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public ClassLoader getCl() {
        return cl;
    }

    public JMSObjectFactory getResponseJMSObjectFactory() {
        return responseJMSObjectFactory;
    }

    public JMSRuntimeMonitor getMonitor() {
        return monitor;
    }

    public JMSObjectFactory getRequestJMSObjectFactory() {
        return requestJMSObjectFactory;
    }

}
