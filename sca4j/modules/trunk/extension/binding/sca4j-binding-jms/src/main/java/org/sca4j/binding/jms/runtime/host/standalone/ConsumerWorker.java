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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.transaction.xa.XAResource;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.common.TransactionType;
import org.sca4j.binding.jms.runtime.helper.JmsHelper;
import org.sca4j.binding.jms.runtime.wireformat.DataBinder;
import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;

/**
 * A thread pull message from destination and invoke Message listener.
 * 
 * @version $Revision$ $Date$
 * 
 * TODO Do rollback properly on local transactions.
 */
public class ConsumerWorker extends DefaultPausableWork {

    private ConsumerWorkerTemplate template;
    private boolean exception;
    private InvocationChain invocationChain;
    private boolean twoWay;
    private Class<?> inputType;
    private Class<?> outputType;
    
    private DataBinder dataBinder = new DataBinder();

    /**
     * @param template
     * @throws ClassNotFoundException 
     */
    public ConsumerWorker(ConsumerWorkerTemplate template) {
        super(true);
        try {
            this.template = template;
            PhysicalOperationDefinition pod = template.wire.getInvocationChains().entrySet().iterator().next().getKey().getTargetOperation();
            this.invocationChain = template.wire.getInvocationChains().entrySet().iterator().next().getValue();
            inputType = Class.forName(pod.getParameters().get(0));
            String outputTypeName = pod.getReturnType();
            if (outputTypeName != null) {
                outputType = Class.forName(outputTypeName);
            }
            twoWay = outputType != null;
        } catch (ClassNotFoundException e) {
            throw new SCA4JJmsException("Unable to load operation types", e);
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void execute() {
        
        Connection requestConnection = null;
        Session requestSession = null;
        MessageConsumer requestConsumer = null;

        Connection responseConnection = null;
        Session responseSession = null;
        MessageProducer responseProducer = null;
        
        try {
            
            if (exception) {
                exception = false;
                Thread.sleep(template.exceptionTimeout);
            }
            
            requestConnection =  template.requestFactory.getConnection();
            requestSession =  template.requestFactory.getSession(requestConnection, template.transactionType);
            requestConnection.start();
            
            if (template.transactionType == TransactionType.GLOBAL) {
                template.transactionHandler.begin();
                template.transactionHandler.enlist(requestSession);
            }
            
            requestConsumer = requestSession.createConsumer(template.requestFactory.getDestination());
            Message jmsRequest = requestConsumer.receive(template.pollingInterval);
            if (jmsRequest != null) {
                
                Object payload = dataBinder.unmarshal(jmsRequest, inputType);
                org.sca4j.spi.invocation.Message sca4jRequest = new MessageImpl(payload, false, new WorkContext());
                org.sca4j.spi.invocation.Message sca4jResponse = invocationChain.getHeadInterceptor().invoke(sca4jRequest);
                if (twoWay) {
                    responseConnection = template.responseFactory.getConnection();
                    responseSession = template.responseFactory.getSession(responseConnection, template.transactionType);
                    if (template.transactionType == TransactionType.GLOBAL) {
                        template.transactionHandler.enlist(responseSession);
                    }
                    Message jmsResponse = dataBinder.marshal(sca4jResponse.getBody(), outputType, responseSession);
                    switch (template.metadata.correlationScheme) {
                        case messageID: 
                            jmsResponse.setJMSCorrelationID(jmsRequest.getJMSCorrelationID());
                            break;
                        case correlationID: 
                            jmsResponse.setJMSCorrelationID(jmsRequest.getJMSMessageID());
                            break;
                    }
                    responseProducer = responseSession.createProducer(template.responseFactory.getDestination());
                    responseProducer.send(jmsResponse);
                }
                
                if (template.transactionType == TransactionType.GLOBAL) {
                    template.transactionHandler.commit();
                    template.transactionHandler.delist(requestSession, XAResource.TMSUCCESS);
                    if (responseSession != null) {
                        template.transactionHandler.delist(responseSession, XAResource.TMSUCCESS);
                    }
                } else {
                    requestSession.commit();
                    if (responseSession != null) {
                        responseSession.commit();
                    }
                }
                
            }
            
        } catch (Exception ex) {  
            reportException(ex);          
            if (template.transactionType == TransactionType.GLOBAL) {
                template.transactionHandler.rollback();
                template.transactionHandler.delist(requestSession, XAResource.TMFAIL);
                if (responseSession != null) {
                    template.transactionHandler.delist(responseSession, XAResource.TMFAIL);
                }
            } else {
                try {
                    if (requestSession != null) {
                        requestSession.rollback();
                    }
                } catch (JMSException ne) {
                    reportException(ne);
                }
            }
        } finally {
            JmsHelper.closeQuietly(responseProducer);
            JmsHelper.closeQuietly(responseSession);
            JmsHelper.closeQuietly(responseConnection);

            JmsHelper.closeQuietly(requestConsumer);
            JmsHelper.closeQuietly(requestSession);
            JmsHelper.closeQuietly(requestConnection);
        }

    }

    /*
     * Report an exception.
     */
    private void reportException(Exception e) {
        template.monitor.jmsListenerError(template.requestFactory.getDestinationName(), e);
        exception = true;
    }

}
