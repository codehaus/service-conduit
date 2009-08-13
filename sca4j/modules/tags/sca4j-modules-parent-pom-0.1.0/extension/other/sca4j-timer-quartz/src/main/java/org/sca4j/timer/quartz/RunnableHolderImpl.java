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

import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

/**
 * Default implementation of a RunnableHolder.
 *
 * @version $Revision$ $Date$
 */
public class RunnableHolderImpl<T> extends FutureTask<T> implements RunnableHolder<T> {
    private String id;
    private QuartzTimerService timerService;

    public RunnableHolderImpl(String id, Runnable runnable, QuartzTimerService timerService) {
        super(runnable, null);
        this.id = id;
        this.timerService = timerService;
    }

    public String getId() {
        return id;
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        boolean result = runAndReset();
        if (!result) {
            try {
                get();
            } catch (ExecutionException e) {
                // unwrap the exception
                JobExecutionException jex = new JobExecutionException(e.getCause());
                jex.setUnscheduleAllTriggers(true);  // unschedule the job
                throw jex;
            } catch (InterruptedException e) {
                JobExecutionException jex = new JobExecutionException(e);
                jex.setUnscheduleAllTriggers(true);  // unschedule the job
                throw jex;
            }
        }
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            boolean val = super.cancel(mayInterruptIfRunning);
            // cancel against the timer service
            timerService.cancel(id);
            return val;
        } catch (SchedulerException e) {
            e.printStackTrace(System.err);
            return false;
        }
    }

    public long getDelay(TimeUnit unit) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public int compareTo(Delayed o) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
