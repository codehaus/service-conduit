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
