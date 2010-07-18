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

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
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
public class TCPHandler implements IoHandler {
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
            session.close(true);
        }
    }

    public void exceptionCaught(org.apache.mina.core.session.IoSession arg0, Throwable arg1) throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void messageSent(org.apache.mina.core.session.IoSession arg0, Object arg1) throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void sessionClosed(org.apache.mina.core.session.IoSession arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void sessionCreated(org.apache.mina.core.session.IoSession arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void sessionIdle(org.apache.mina.core.session.IoSession arg0, IdleStatus arg1) throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void sessionOpened(org.apache.mina.core.session.IoSession arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }

}
