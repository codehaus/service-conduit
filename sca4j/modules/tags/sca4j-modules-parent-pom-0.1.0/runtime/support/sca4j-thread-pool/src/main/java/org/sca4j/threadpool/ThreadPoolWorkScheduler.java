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

package org.sca4j.threadpool;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.PausableWork;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.management.WorkSchedulerMBean;
import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;

/**
 * Thread pool based implementation of the work scheduler.
 *
 */
@EagerInit
public class ThreadPoolWorkScheduler implements WorkScheduler, WorkSchedulerMBean {

    private ThreadPoolExecutor executor;
    private final Set<DefaultPausableWork> workInProgress = new CopyOnWriteArraySet<DefaultPausableWork>();
    private final AtomicBoolean paused = new AtomicBoolean();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    private int size = 20;
    private boolean pauseOnStart = false;
    
    /**
     * Sets the pool size.
     * 
     * @param size Pool size.
     */
    @Property
    public void setSize(int size) {
    	this.size = size;
    }
    
    /**
     * Indicates whether to start this in a paused state.
     * 
     * @param pauseOnStart True if we want to start this in a pased state.
     */
    @Property
    public void setPauseOnStart(boolean pauseOnStart) {
    	this.pauseOnStart = pauseOnStart;
    }

    /**
     * Initializes the thread-pool. Supports unbounded work with a fixed pool size. If all the workers 
     * are busy, work gets queued.
     */
    @Init
    public void init() {
        executor = new ThreadPoolExecutor(size, size, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        paused.set(pauseOnStart);
    }

	public <T extends DefaultPausableWork> void scheduleWork(T work) {
		
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
	        Runnable runnable = new DecoratingWork(work);
	        executor.submit(runnable);
		} finally {
			lock.unlock();
		}
        
	}
	
	private class DecoratingWork implements Runnable {

		private DefaultPausableWork work;
		
		public DecoratingWork(DefaultPausableWork work) {
			this.work = work;
		}
		
		public void run() {

			if (paused.get()) {
				work.pause();
			}
			workInProgress.add(work);
			
			try {
				work.run();
			} finally {
				workInProgress.remove(work);
			}
			
		}
		
	}
	
	// ------------------ Management operations
	public int getActiveCount() {
		return executor.getActiveCount();
	}

	public int getPoolSize() {
		return executor.getCorePoolSize();
	}

	public void pause() {
		
		if (paused.get()) {
			return;
		}
		
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			paused.set(true);
			for (PausableWork pausableWork : workInProgress) {
				pausableWork.pause();
			}
		} finally {
			lock.unlock();
		}
		
	}

	public void setPoolSize(int poolSize) {
		executor.setCorePoolSize(poolSize);
	}

	public void start() {
		
		if (!paused.get()) {
			return;
		}
		
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			paused.set(false);
			for (PausableWork pausableWork : workInProgress) {
				pausableWork.start();
			}
		} finally {
			lock.unlock();
		}
		
	}

	public void stop() {
		
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			for (PausableWork pausableWork : workInProgress) {
				pausableWork.stop();
			}
			executor.shutdown();
		} finally {
			lock.unlock();
		}
		
	}

	public Status getStatus() {
		return paused.get() ? Status.PAUSED : Status.STARTED;
	}

}
