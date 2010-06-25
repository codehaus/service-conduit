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
package org.sca4j.timer.quartz.runtime;

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
