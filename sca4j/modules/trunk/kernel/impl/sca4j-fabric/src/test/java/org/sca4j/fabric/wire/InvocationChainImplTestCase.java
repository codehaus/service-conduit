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
