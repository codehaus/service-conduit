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

import org.sca4j.binding.jms.runtime.wireformat.DataBinder;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;

public abstract class AbstractInterceptor implements Interceptor {
    
    private Interceptor next;
    
    protected DataBinder dataBinder = new DataBinder();

    @Override
    public final Interceptor getNext() {
        return next;
    }

    @Override
    public final void setNext(Interceptor next) {
        this.next = next;
    }
    
    protected void copyHeaders(WorkContext workContext, javax.jms.Message jmsMessage) throws JMSException {
        for (Map.Entry<String, Object> entry : workContext.getHeaders().entrySet()) {
            jmsMessage.setObjectProperty(entry.getKey(), entry.getValue());
        }
    }

}
