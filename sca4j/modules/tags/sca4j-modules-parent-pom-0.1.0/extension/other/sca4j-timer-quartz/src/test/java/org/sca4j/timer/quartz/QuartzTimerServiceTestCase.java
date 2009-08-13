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

import javax.transaction.TransactionManager;

import junit.framework.TestCase;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.WorkScheduler;

/**
 * @version $Revision$ $Date$
 */
public class QuartzTimerServiceTestCase extends TestCase {
    private QuartzTimerService timerService;
    private TransactionManager tm;

    public void testNonTransactionalScheduler() throws Exception {
        TestRunnable runnable = new TestRunnable(2);
        timerService.scheduleWithFixedDelay(runnable, 0, 10, TimeUnit.MILLISECONDS);
        runnable.await();
    }

    protected void setUp() throws Exception {
        super.setUp();
        // TODO mock transaction manager
        WorkScheduler workScheduler = new WorkScheduler() {
            public <T extends DefaultPausableWork> void scheduleWork(T work) {
                work.run();
            }
        };
        timerService = new QuartzTimerService(workScheduler, tm);
        timerService.setTransactional(false);
        timerService.init();
    }


    private class TestRunnable implements Runnable {
        private CountDownLatch latch;

        private TestRunnable(int num) {
            latch = new CountDownLatch(num);
        }

        public void run() {
            latch.countDown();
        }

        public void await() throws InterruptedException {
            latch.await();
        }

    }

}
