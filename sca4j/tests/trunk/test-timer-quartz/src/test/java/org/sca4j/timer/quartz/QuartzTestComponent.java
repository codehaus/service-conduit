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
package org.sca4j.timer.quartz;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import junit.framework.TestCase;

import org.sca4j.api.annotation.Resource;
import org.sca4j.timer.spi.TimerService;

/**
 * @version $Revision$ $Date$
 */
public class QuartzTestComponent extends TestCase {

    @Resource(mappedName = "TransactionalTimerService")
    protected TimerService trxTimerService;
    @Resource(mappedName = "NonTransactionalTimerService")
    protected TimerService timerService;

    @Resource(mappedName = "TransactionManager")
    protected TransactionManager tm;

    public void testTransactionalScheduleInterval() throws Exception {
        TrxTestRunnable runnable = new TrxTestRunnable(2);  // test multiple firings
        trxTimerService.scheduleWithFixedDelay(runnable, 0, 10, TimeUnit.MILLISECONDS);
        runnable.await();
        assertTrue(runnable.isTrxStarted());
    }

    public void testNonTransactionalScheduleInterval() throws Exception {
        NoTrxTestRunnable runnable = new NoTrxTestRunnable(2);  // test multiple firings
        timerService.scheduleWithFixedDelay(runnable, 0, 10, TimeUnit.MILLISECONDS);
        runnable.await();
        assertTrue(runnable.isNoTrx());
    }

    public void testTransactionalScheduleWithDelay() throws Exception {
        TrxTestRunnable runnable = new TrxTestRunnable(1); // fires once
        trxTimerService.schedule(runnable, 10, TimeUnit.MILLISECONDS);
        runnable.await();
        assertTrue(runnable.isTrxStarted());
    }

    public void testNonTransactionalScheduleWithDelay() throws Exception {
        NoTrxTestRunnable runnable = new NoTrxTestRunnable(1);   // fires once
        timerService.schedule(runnable, 10, TimeUnit.MILLISECONDS);
        runnable.await();
        assertTrue(runnable.isNoTrx());
    }

    private class TrxTestRunnable implements Runnable {
        private CountDownLatch latch;
        private boolean trxStarted;

        private TrxTestRunnable(int num) {
            latch = new CountDownLatch(num);
        }

        public void run() {
            try {
                latch.countDown();
                if (tm.getStatus() == Status.STATUS_ACTIVE) {
                    trxStarted = true;
                }
            } catch (SystemException e) {
                // this will cause the test to fail by not setting noTrx
            }
        }

        public boolean isTrxStarted() {
            return trxStarted;
        }

        public void await() throws InterruptedException {
            latch.await();
        }
    }


    private class NoTrxTestRunnable implements Runnable {
        private CountDownLatch latch;
        private boolean noTrx;

        private NoTrxTestRunnable(int num) {
            latch = new CountDownLatch(num);
        }

        public void run() {
            try {
                latch.countDown();
                if (tm.getStatus() == Status.STATUS_NO_TRANSACTION) {
                    noTrx = true;
                }
            } catch (SystemException e) {
                // this will cause the test to fail
            }
        }

        public boolean isNoTrx() {
            return noTrx;
        }

        public void await() throws InterruptedException {
            latch.await();
        }
    }

}
