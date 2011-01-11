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
 */
package org.sca4j.binding.jms.runtime.host.standalone;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.runtime.wireformat.DataBinder;
import org.sca4j.host.runtime.RuntimeLifecycle;
import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.InvocationChain;

/**
 * A thread pull message from destination and invoke Message listener.
 *
 * @version $Revision$ $Date$
 */
public abstract class ConsumerWorker extends DefaultPausableWork {

    protected ConsumerWorkerTemplate template;
    protected boolean exception;
    
    /** Flag to indicate if there may be more messages available to consume in next run. 
     * This flag is optimistically set to true if there are messages consumed in the current run.
     */
    protected boolean moreMessages = true;
    protected DataBinder dataBinder = new DataBinder();
    protected RuntimeLifecycle runtimeLifecycle;
    protected InvocationChain invocationChain;
    protected Class<?> inputType;
    
    protected ConsumerWorker(ConsumerWorkerTemplate template, RuntimeLifecycle runtimeLifecycle) {
        this(template, runtimeLifecycle, true);
    }

    /**
     * @param template
     * @throws ClassNotFoundException
     */
    protected ConsumerWorker(ConsumerWorkerTemplate template, RuntimeLifecycle runtimeLifecycle, boolean isDeamon) {
        super(isDeamon);
        try {
            this.template = template;
            PhysicalOperationDefinition pod = template.wire.getInvocationChains().entrySet().iterator().next().getKey().getTargetOperation();
            this.invocationChain = template.wire.getInvocationChains().entrySet().iterator().next().getValue();
            inputType = Class.forName(pod.getParameters().get(0));
            this.runtimeLifecycle = runtimeLifecycle;
        } catch (ClassNotFoundException e) {
            throw new SCA4JJmsException("Unable to load operation types", e);
        }
    }

    protected void copyHeaders(Message jmsRequest, WorkContext workContext) throws JMSException {
        
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("JMSCorrelationId", jmsRequest.getJMSCorrelationID());
        headers.put("JMSMessageId", jmsRequest.getJMSMessageID());
        headers.put("JMSRedelivered", jmsRequest.getJMSRedelivered());
        headers.put("JMSType", jmsRequest.getJMSType());
        headers.put("JMSReplyTo", jmsRequest.getJMSReplyTo());
        Enumeration<?> propertyNames = jmsRequest.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            String propertyName = propertyNames.nextElement().toString();
            headers.put(propertyName, jmsRequest.getObjectProperty(propertyName));
        }
        
        workContext.addHeader("sca4j.jms.inbound", headers);
        
    }

    /*
     * Report an exception.
     */
    protected void reportException(Exception e) {
        if (!runtimeLifecycle.isShutdown()) {
            template.monitor.jmsListenerError(template.jmsFactory.getDestination().toString(), e);
            exception = true;
        }
    }
    
    /**
     * Indicates if there may be messages to consume in the next run.
     * @return flag to indicate there may be messages to consume.
     */
    public boolean isMoreMessages() {
        return moreMessages;
    }

}
