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
package org.sca4j.fabric.wire;

import org.sca4j.spi.model.physical.PhysicalOperationDefinition;
import org.sca4j.spi.wire.Interceptor;
import org.sca4j.spi.invocation.Message;
import org.sca4j.spi.wire.InvocationChain;

import junit.framework.TestCase;

/**
 * @version $Rev: 3021 $ $Date: 2008-03-04 03:28:04 +0000 (Tue, 04 Mar 2008) $
 */
public class InvocationChainImplTestCase extends TestCase {

    public void testInsertAtPos() throws Exception {
        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        InvocationChain chain = new InvocationChainImpl(operation);
        Interceptor inter3 = new MockInterceptor();
        Interceptor inter2 = new MockInterceptor();
        Interceptor inter1 = new MockInterceptor();
        chain.addInterceptor(inter3);
        chain.addInterceptor(0, inter1);
        chain.addInterceptor(1, inter2);
        Interceptor head = chain.getHeadInterceptor();
        assertEquals(inter1, head);
        assertEquals(inter2, head.getNext());
        assertEquals(inter3, head.getNext().getNext());
    }

    public void testInsertAtEnd() throws Exception {
        PhysicalOperationDefinition operation = new PhysicalOperationDefinition();
        InvocationChain chain = new InvocationChainImpl(operation);
        Interceptor inter2 = new MockInterceptor();
        Interceptor inter1 = new MockInterceptor();
        chain.addInterceptor(0, inter1);
        chain.addInterceptor(1, inter2);
        Interceptor head = chain.getHeadInterceptor();
        assertEquals(inter1, head);
        assertEquals(inter2, head.getNext());
        assertEquals(inter2, chain.getTailInterceptor());

    }

    private class MockInterceptor implements Interceptor {

        private Interceptor next;

        public Message invoke(Message msg) {
            return null;
        }

        public void setNext(Interceptor next) {
            this.next = next;
        }

        public Interceptor getNext() {
            return next;
        }

        public boolean isOptimizable() {
            return false;
        }
    }


}
