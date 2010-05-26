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

import javax.xml.namespace.QName;

import org.sca4j.bpel.spi.EmbeddedBpelServer;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;

/**
 * Receives an invocation into a BPEL process.
 * 
 * @author meerajk
 *
 */
public class BpelInvocationReceiver implements Interceptor {

    private PhysicalOperationDefinition targetOperationDefinition;
    private EmbeddedBpelServer embeddedBpelServer;
    private QName portTypeName;
    
    public BpelInvocationReceiver(PhysicalOperationDefinition targetOperationDefinition, EmbeddedBpelServer embeddedBpelServer, QName portTypeName) {
        this.targetOperationDefinition = targetOperationDefinition;
        this.embeddedBpelServer = embeddedBpelServer;
        this.portTypeName = portTypeName;
    }

    @Override
    public Interceptor getNext() {
        return null;
    }

    @Override
    public Message invoke(Message message) {
        return embeddedBpelServer.invokeService(targetOperationDefinition, portTypeName, message);
    }

    @Override
    public void setNext(Interceptor next) {
    }

}
