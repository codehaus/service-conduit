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
package org.sca4j.tests.function.callback.conversation;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.callback.common.CallbackData;

/**
 * Tests for stateless calbacks.
 *
 * @version $Rev: 2751 $ $Date: 2008-02-12 01:14:41 -0800 (Tue, 12 Feb 2008) $
 */
public class ConversationalCallbackTest extends TestCase {
    @Reference
    protected ConversationalClientService client;

    @Reference
    protected ConversationalCallbackClientEndsService conversationalCallbackClientEndsService;

    @Reference
    protected ConversationalClientService conversationalToCompositeClient;

    /**
     * Verfies a callback is routed back to the correct conversational client instance.
     *
     * @throws Throwable
     */
    public void txestConversationalCallback() throws Throwable {
        CallbackData data = new CallbackData(1);
        client.invoke(data);
        data.getLatch().await();
        if (data.isError()) {
            throw data.getException();
        }
        assertTrue(data.isCalledBack());
        assertEquals(3, client.getCount());
    }

    public void testCallbackEndsConversation() throws Throwable {
        conversationalCallbackClientEndsService.invoke();
        // the next call must create another conversation
        conversationalCallbackClientEndsService.invoke();
    }

    /**
     * Verifies a conversational client is called back when it invokes a composite-scoped component which in turn invokes another composite-scoped
     * component via a non-blocking operation. The fabric must route back to the orignal conversational client instance as the invocation sequence is
     * processed on different threads.
     *
     * @throws Throwable
     */
    public void txestConversationalToCompositeCallback() throws Throwable {
        CallbackData data = new CallbackData(1);
        conversationalToCompositeClient.invoke(data);
        data.getLatch().await();
        if (data.isError()) {
            throw data.getException();
        }
        assertTrue(data.isCalledBack());
        assertEquals(3, conversationalToCompositeClient.getCount());
    }

}
