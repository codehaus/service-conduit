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
package org.sca4j.binding.jms.runtime.interceptor;

import java.util.Hashtable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;

import org.sca4j.binding.jms.common.JmsBindingMetadata;
import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.helper.JndiHelper;
import org.sca4j.binding.jms.runtime.wireformat.DataBinder;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

public abstract class AbstractInterceptor implements Interceptor {
    
    private Interceptor next;
    private JmsBindingMetadata metadata;
    protected final DataBinder dataBinder = new DataBinder();
    protected final JMSObjectFactory jmsFactory;
    protected final Class<?> inputType;
    

    public AbstractInterceptor(JMSObjectFactory jmsFactory, Wire wire, JmsBindingMetadata metadata) {
        try {
            PhysicalOperationDefinition pod = wire.getInvocationChains().entrySet().iterator().next().getKey().getTargetOperation();
            inputType = Class.forName(pod.getParameters().get(0));
            this.jmsFactory = jmsFactory;
            this.metadata = metadata;
        } catch (ClassNotFoundException e) {
            throw new SCA4JJmsException("Unable to load operation types", e);
        }
    }

    @Override
    public final Interceptor getNext() {
        return next;
    }

    @Override
    public final void setNext(Interceptor next) {
        this.next = next;
    }
    
    @SuppressWarnings("unchecked")
    protected void copyHeaders(WorkContext workContext, javax.jms.Message jmsMessage) throws JMSException {
        Map<String, Object> headers = workContext.getHeader(Map.class, "sca4j.jms.outbound");
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                jmsMessage.setObjectProperty(entry.getKey(), entry.getValue());
            }
        }
        for (JmsBindingMetadata.Property property : metadata.properties) {
            if ("JMSReplyTo".equals(property.name)) {
                Hashtable<String, String> env = new Hashtable<String, String>();
                if (metadata.jndiUrl != null && !"".equals(metadata.jndiUrl)) {
                    env.put(Context.PROVIDER_URL, metadata.jndiUrl);
                }
                if (metadata.initialContextFactory != null && !"".equals(metadata.initialContextFactory)) {
                    env.put(Context.INITIAL_CONTEXT_FACTORY, metadata.initialContextFactory);
                }
                Destination replyTo = JndiHelper.lookup(property.value.toString(), env);
                jmsMessage.setJMSReplyTo(replyTo);
                
            } else {
                jmsMessage.setObjectProperty(property.name, property.value);
            }
        }
    }

}
