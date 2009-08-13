/*
 * SCA4J
 * Copyright (c) 2008-2012 Service Symphony Limited
 *
 * This proprietary software may be used only in connection with the SCA4J license
 * (the ?License?), a copy of which is included in the software or may be obtained 
 * at: http://www.servicesymphony.com/licenses/license.html.
 *
 * Software distributed under the License is distributed on an as is basis, without 
 * warranties or conditions of any kind.  See the License for the specific language 
 * governing permissions and limitations of use of the software. This software is 
 * distributed in conjunction with other software licensed under different terms. 
 * See the separate licenses for those programs included in the distribution for the 
 * permitted and restricted uses of such software.
 *
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.binding.tcp.runtime.handler;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.sca4j.binding.tcp.runtime.monitor.TCPBindingMonitor;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.wire.Wire;

/**
 * Handler to TCP messages delivered.
 * 
 * @version $Revision$ $Date$
 */
public class TCPHandler extends IoHandlerAdapter {
    private Wire wire;
    private TCPBindingMonitor monitor;

    /**
     * Inject wire on TCP Handler
     * 
     * @param wire {@link Wire}
     * @param monitor
     */
    public TCPHandler(Wire wire, TCPBindingMonitor monitor) {
        this.wire = wire;
        this.monitor = monitor;
    }

    /**
     * {@inheritDoc}
     */
    public void messageReceived(IoSession session, Object message) throws Exception {
        Interceptor interceptor = wire.getInvocationChains().values().iterator().next().getHeadInterceptor();
        WorkContext workContext = new WorkContext();
        Message input = new MessageImpl(new Object[] { message }, false, workContext);
        Message msg = interceptor.invoke(input);

        // TODO: Work out if service is of request/response type, and then write
        // the response back.
        if (!msg.isFault() && msg.getBody() != null) {
            session.write(msg.getBody());
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        monitor.onException("Exception caught in TCP binding:TCP handler", cause);
    }

}
