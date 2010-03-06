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
package org.sca4j.timer.quartz;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.transaction.TransactionManager;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerConfigException;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.core.JobRunShellFactory;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.core.SchedulingContext;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdScheduler;
import org.quartz.simpl.RAMJobStore;
import org.quartz.spi.JobFactory;
import org.quartz.spi.JobStore;
import org.quartz.spi.ThreadPool;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.timer.spi.TimerService;

/**
 * Implementation of the TimerService that is backed by Quartz.
 *
 * @version $Revision$ $Date$
 */
public class QuartzTimerService implements TimerService {
    public static final String GROUP = "default";

    private final WorkScheduler workScheduler;
    private TransactionManager tm;
    private RunnableJobFactory jobFactory;
    private Scheduler scheduler;
    private long waitTime = -1;  // default Quartz value
    private boolean transactional = true;
    private String schedulerName = "SCA4JScheduler";
    private long counter;

    public QuartzTimerService(@Reference WorkScheduler workScheduler, @Reference TransactionManager tm) {
        this.workScheduler = workScheduler;
        this.tm = tm;
    }

    @Init
    public void init() throws SchedulerException {
        JobStore store = new RAMJobStore();
        F3ThreadPool pool = new F3ThreadPool();
        jobFactory = new RunnableJobFactoryImpl();
        JobRunShellFactory shellFactory;
        if (transactional) {
            shellFactory = new TrxJobRunShellFactory(tm);
        } else {
            shellFactory = new SCA4JJobRunShellFactory();
        }
        scheduler = createScheduler(schedulerName, "default", store, pool, shellFactory, jobFactory);
        RunnableCleanupListener listener = new RunnableCleanupListener(jobFactory);
        scheduler.addSchedulerListener(listener);
        scheduler.start();
    }

    @Destroy
    public void destroy() throws SchedulerException {
        if (scheduler != null) {
            scheduler.shutdown(false);
        }
    }

    @Property
    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    @Property
    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    @Property
    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public ScheduledFuture<?> schedule(Runnable command, String expression) throws ParseException {
        CronTrigger trigger = new CronTrigger();
        trigger.setCronExpression(expression);
        String id = createId();
        trigger.setName(id);
        return schedule(id, command, trigger);
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
	long timeInMillis = TimeUnit.MILLISECONDS.convert(delay, unit);
        String id = createId();
        Trigger trigger = new SimpleTrigger(id, GROUP, new Date(System.currentTimeMillis() + timeInMillis));
        return schedule(id, command, trigger);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
	long timeInMillis = TimeUnit.MILLISECONDS.convert(delay, unit);
	long delayInMillis = TimeUnit.MILLISECONDS.convert(initialDelay, unit);
        String id = createId();
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setName(id);
        trigger.setRepeatInterval(timeInMillis);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setStartTime(new Date(System.currentTimeMillis() + delayInMillis));
        return schedule(id, command, trigger);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void cancel(String id) throws SchedulerException {
        jobFactory.remove(id);
        scheduler.unscheduleJob(id, GROUP);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void shutdown() {
        throw new UnsupportedOperationException("Explicit shutdown not supported");
    }

    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException("Explicit shutdown not supported");

    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Explicit shutdown not supported");
    }

    public <T> Future<T> submit(Callable<T> task) {
        throw new UnsupportedOperationException("Not implemented");

    }

    public <T> Future<T> submit(Runnable task, T result) {
        throw new UnsupportedOperationException("Not implemented");

    }

    public Future<?> submit(Runnable task) {
        throw new UnsupportedOperationException("Not implemented");

    }

    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("Not implemented");

    }

    public <T> List<Future<T>> invokeAll(Collection<Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not implemented");

    }

    public <T> T invokeAny(Collection<Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("Not implemented");

    }

    public <T> T invokeAny(Collection<Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        throw new UnsupportedOperationException("Not implemented");

    }

    public void execute(final Runnable runnable) {
        workScheduler.scheduleWork(new DefaultPausableWork() {
        	public void execute() {
        		runnable.run();
        	}
        });
    }

    private Scheduler createScheduler(String name, String id, JobStore store, ThreadPool pool, JobRunShellFactory shellFactory, JobFactory jobFactory)
            throws SchedulerException {
        SchedulingContext context = new SchedulingContext();
        context.setInstanceId(id);

        QuartzSchedulerResources resources = new QuartzSchedulerResources();
        resources.setName(name);
        resources.setInstanceId(id);
        resources.setJobRunShellFactory(shellFactory);
        resources.setThreadPool(pool);
        resources.setJobStore(store);

        QuartzScheduler quartzScheduler = new QuartzScheduler(resources, context, waitTime, -1);
        quartzScheduler.setJobFactory(jobFactory);
        store.initialize(null, quartzScheduler.getSchedulerSignaler());
        Scheduler scheduler = new StdScheduler(quartzScheduler, context);
        shellFactory.initialize(scheduler, context);
        SchedulerRepository repository = SchedulerRepository.getInstance();
        quartzScheduler.addNoGCObject(repository); // prevents the repository from being garbage collected
        repository.bind(scheduler);    // no need to remove since it is handled in the scheduler shutdown method
        return scheduler;
    }

    private String createId() {
        long id = ++counter;
        return (String.valueOf(id));
    }

    private ScheduledFuture<?> schedule(String id, Runnable command, Trigger trigger) throws RejectedExecutionException {
        JobDetail detail = new JobDetail();
        detail.setName(id);
        detail.setGroup(GROUP);
        detail.setJobClass(Job.class);  // required by Quartz
        RunnableHolder holder = new RunnableHolderImpl(id, command, this);
        jobFactory.register(holder);
        try {
            scheduler.scheduleJob(detail, trigger);
        } catch (SchedulerException e) {
            throw new RejectedExecutionException(e);
        }
        return holder;
    }

    /**
     * Wrapper for the system WorkScheduler.
     */
    private class F3ThreadPool implements ThreadPool {

        public boolean runInThread(final Runnable runnable) {
            workScheduler.scheduleWork(new DefaultPausableWork() {
            	public void execute() {
            		runnable.run();
            	}
            });
            return true;
        }

        public int blockForAvailableThreads() {
            return 5; // TODO WorkScheduler doesn't provide this functionality
        }

        public void initialize() throws SchedulerConfigException {
        }

        public void shutdown(boolean b) {
        }

        public int getPoolSize() {
            return 5;  // TODO WorkScheduler doesn't provide this functionality
        }
    }

}
