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

import java.util.Map;

import javax.jms.JMSException;

import org.sca4j.binding.jms.common.SCA4JJmsException;
import org.sca4j.binding.jms.runtime.JMSObjectFactory;
import org.sca4j.binding.jms.runtime.wireformat.DataBinder;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

public abstract class AbstractInterceptor implements Interceptor {
    
    private Interceptor next;
    
    protected final DataBinder dataBinder = new DataBinder();
    protected final JMSObjectFactory jmsFactory;
    protected final Class<?> inputType;
    

    public AbstractInterceptor(JMSObjectFactory jmsFactory, Wire wire) {
        try {
            PhysicalOperationDefinition pod = wire.getInvocationChains().entrySet().iterator().next().getKey().getTargetOperation();
            inputType = Class.forName(pod.getParameters().get(0));
            this.jmsFactory = jmsFactory;
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
    }

}
