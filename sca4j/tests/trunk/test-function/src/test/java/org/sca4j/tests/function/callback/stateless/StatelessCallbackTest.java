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
 * ---- Original Codehaus Header ----
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
 * ---- Original Apache Header ----
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
package org.sca4j.tests.function.callback.stateless;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

import org.sca4j.tests.function.callback.common.CallbackData;

/**
 * Tests for stateless callbacks.
 */
public class StatelessCallbackTest extends TestCase {
    @Reference
    protected ClientService client1;

    /**
     * Verifies the case where two clients are wired to the same target service.
     *
     * @throws Exception
     */
    public void testSimpleCallback() throws Exception {
        CallbackData data = new CallbackData(1);
        client1.invoke(data);
        data.getLatch().await();
        assertTrue(data.isCalledBack());
        // test that the other client was not issued a callback
        // assertFalse(client2.isCallback());
    }

    /**
     * Verifies a callback from a forward invocation through a ServiceReference
     *
     * @throws Exception
     */
    public void testServiceReferenceCallback() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        client1.invokeServiceReferenceCallback(latch);
//        latch.await();
//        assertTrue(client1.isCallback());
//        // test that the other client was not issued a callback
//        assertFalse(client2.isCallback());
    }

    /**
     * Verifies callbacks are routed for a sequence of two forward invocations:
     * <pre>
     * CallbackClient--->ForwardService--->EndService
     * </pre>
     *
     * @throws Exception
     */
    public void testMultipleHopCallback() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        client1.invokeMultipleHops(latch);
//        latch.await();
//        assertTrue(client1.isCallback());
//        assertFalse(client2.isCallback());
    }

    /**
     * Verifies a callback is routed through a CallableReference passed to another service.
     *
     * @throws Exception
     */
    public void testNoCallbackServiceReference() throws Exception {
//        CountDownLatch latch = new CountDownLatch(2);
//        client1.invokeNoCallbackServiceReference(latch);
//        latch.await();
//        assertTrue(client1.isCallback());
//        assertFalse(client2.isCallback());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
