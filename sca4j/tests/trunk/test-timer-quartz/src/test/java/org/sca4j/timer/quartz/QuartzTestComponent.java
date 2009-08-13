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
