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
package org.sca4j.fabric.async;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import org.easymock.IAnswer;

import org.sca4j.host.work.WorkScheduler;
import org.sca4j.spi.invocation.CallFrame;
import org.sca4j.spi.invocation.WorkContext;
import org.sca4j.spi.invocation.MessageImpl;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.wire.Interceptor;

/**
 * @version $Rev: 5150 $ $Date: 2008-08-03 13:04:06 +0100 (Sun, 03 Aug 2008) $
 */
public class NonBlockingInterceptorTestCase extends TestCase {
    private Interceptor next;
    private NonBlockingInterceptor interceptor;
    private WorkScheduler workScheduler;
    private WorkContext workContext;

    public void testInvoke() throws Exception {
        final Message message = new MessageImpl();
        message.setWorkContext(workContext);
        workScheduler.scheduleWork(isA(AsyncRequest.class));
        expectLastCall().andStubAnswer(new IAnswer<Object>() {
            public Object answer() throws Throwable {
                AsyncRequest request =
                        (AsyncRequest) getCurrentArguments()[0];
                request.run();
                assertSame(next, request.getNext());
                assertSame(message, request.getMessage());
                WorkContext newWorkContext = message.getWorkContext();
                assertNotSame(workContext, newWorkContext);
                return null;
            }
        });
        replay(workScheduler);
        assertSame(NonBlockingInterceptor.RESPONSE, interceptor.invoke(message));

    }

    public void testNextInterceptor() {
        assertSame(next, interceptor.getNext());
    }

    protected void setUp() throws Exception {
        super.setUp();
        workContext = new WorkContext();
        CallFrame frame = new CallFrame();
        workContext.addCallFrame(frame);

        workScheduler = EasyMock.createMock(WorkScheduler.class);
        next = EasyMock.createMock(Interceptor.class);
        interceptor = new NonBlockingInterceptor(workScheduler);
        interceptor.setNext(next);
    }
}
