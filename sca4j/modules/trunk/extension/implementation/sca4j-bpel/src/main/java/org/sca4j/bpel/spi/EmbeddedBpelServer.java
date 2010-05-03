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
package org.sca4j.bpel.spi;

import javax.xml.namespace.QName;

import org.sca4j.bpel.provision.BpelPhysicalComponentDefinition;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;

/**
 * SPI for talking to an embedded BPEL server.
 * 
 * @author meerajk
 *
 */
public interface EmbeddedBpelServer {
    
    /**
     * @param processName
     * @param referenceName
     * @param invoker
     */
    void addOutboundEndpoint(QName processName, QName referenceName, Interceptor invoker);
    
    /**
     * @param physicalComponentDefinition
     */
    void registerProcess(BpelPhysicalComponentDefinition physicalComponentDefinition);

    /**
     * @param targetOperationDefinition Operation definition.
     * @param processName Process to be executed.
     * @param message Incoming message.
     * @return Return message.
     */
    Message invokeService(PhysicalOperationDefinition targetOperationDefinition, QName processName, Message message);

}
