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
package org.sca4j.bpel.runtime;

import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;

public class BpelInvocationReceiver implements Interceptor {

    private PhysicalOperationDefinition targetOperationDefinition;
    
    public BpelInvocationReceiver(PhysicalOperationDefinition targetOperationDefinition) {
        this.targetOperationDefinition = targetOperationDefinition;
    }

    @Override
    public Interceptor getNext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Message invoke(Message msg) {
        Object[] payload = (Object[]) msg.getBody();
        System.err.println("Invoked operation " + targetOperationDefinition.getName() + " with parameters " + payload[0]);
        // TODO interface into the BPEL engine
        return new MessageImpl();
    }

    @Override
    public void setNext(Interceptor next) {
        // TODO Auto-generated method stub

    }

}
