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
 */
package org.sca4j.binding.jms.runtime.host.standalone;

import static javax.transaction.xa.XAResource.TMFAIL;
import static javax.transaction.xa.XAResource.TMSUCCESS;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.binding.jms.runtime.tx.JmsTransactionHandler;
import org.sca4j.binding.jms.runtime.tx.JtaTransactionHandler;
import org.sca4j.binding.jms.runtime.tx.TransactionHandler;
import org.sca4j.host.runtime.RuntimeLifecycle;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;

/**
 * A thread pull message from destination and invoke Message listener.
 *
 * @version $Revision$ $Date$
 */
public class TwoWayConsumer extends ConsumerWorker {
    
    private Class<?> outputType;
    
    public TwoWayConsumer(ConsumerWorkerTemplate template, RuntimeLifecycle runtimeLifecycle) {
        this(template, runtimeLifecycle, true);
    }

    public TwoWayConsumer(ConsumerWorkerTemplate template, RuntimeLifecycle runtimeLifecycle, boolean isDeamon) {
        super(template, runtimeLifecycle, isDeamon);
        try {
            PhysicalOperationDefinition pod = template.wire.getInvocationChains().entrySet().iterator().next().getKey().getTargetOperation();
            this.invocationChain = template.wire.getInvocationChains().entrySet().iterator().next().getValue();
            String outputTypeName = pod.getReturnType();
            if (outputTypeName != null && outputTypeName != "void") {
                outputType = Class.forName(outputTypeName);
            }
        } catch (ClassNotFoundException e) {
            throw new SCA4JJmsException("Unable to load operation types", e);
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void execute() {

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;
        MessageProducer producer = null;
        TransactionHandler transactionHandler = null;

        try {
            
            if (runtimeLifecycle.isShutdown()) {
                return;
            }

            if (exception) {
                exception = false;
                Thread.sleep(template.exceptionTimeout);
            }

            connection = template.jmsFactory.getConnection();
            session = template.jmsFactory.getSession(connection, template.transactionType);
            connection.start();

            if (template.transactionType == TransactionType.GLOBAL) {
                transactionHandler = new JtaTransactionHandler(template.transactionManager);
            } else {
                transactionHandler = new JmsTransactionHandler(session);
            }

            transactionHandler.begin();
            transactionHandler.enlist(session);
            if (template.metadata.selector != null) {
                consumer = session.createConsumer(template.jmsFactory.getDestination(), template.metadata.selector);
            } else {
                consumer = session.createConsumer(template.jmsFactory.getDestination());
            }
            Message jmsRequest = consumer.receive(template.pollingInterval);

            if (jmsRequest != null) {
                Object payload = dataBinder.unmarshal(jmsRequest, inputType);
                WorkContext workContext = new WorkContext();
                copyHeaders(jmsRequest, workContext);
                org.sca4j.spi.invocation.Message sca4jRequest = new MessageImpl(new Object[] { payload }, false, workContext);
                org.sca4j.spi.invocation.Message sca4jResponse = invocationChain.getHeadInterceptor().invoke(sca4jRequest);
                Message jmsResponse = dataBinder.marshal(sca4jResponse.getBody(), outputType, session);
                switch (template.metadata.correlation) {
                case messageID:
                    jmsResponse.setJMSCorrelationID(jmsRequest.getJMSMessageID());
                    break;
                case correlationID:
                    jmsResponse.setJMSCorrelationID(jmsRequest.getJMSCorrelationID());
                    break;
                }
                producer = session.createProducer(template.jmsFactory.getResponseDestination());
                producer.send(jmsResponse);
            }

            transactionHandler.delist(session, TMSUCCESS);
            transactionHandler.commit();

        } catch (Exception ex) {
            reportException(ex);
            try {
                if (transactionHandler != null) {
                    transactionHandler.delist(session, TMFAIL);
                    transactionHandler.rollback();
                }
            } catch (Exception e1) {
                reportException(e1);
            }
        } finally {
            JmsHelper.closeQuietly(producer);
            JmsHelper.closeQuietly(consumer);
            JmsHelper.closeQuietly(session);
            JmsHelper.closeQuietly(connection);
        }

    }

}
