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

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.PausableWork;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.management.WorkSchedulerMBean;

/**
 * Thread pool based implementation of the work scheduler.
 *
 */
@EagerInit
public class ThreadPoolWorkScheduler implements WorkScheduler, WorkSchedulerMBean {
    
    @Property(required=false) public int size = 20;
    @Property(required=false) public boolean pauseOnStart = false;

    private ThreadPoolExecutor executor;
    private final Set<DefaultPausableWork> daemonWork = new CopyOnWriteArraySet<DefaultPausableWork>(); 
    private final Set<DefaultPausableWork> nonDaemonWork = new CopyOnWriteArraySet<DefaultPausableWork>();
    private final AtomicBoolean paused = new AtomicBoolean();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

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
		    if (work.isDaemon()) {
                daemonWork.add(work);
		    }
		    if (paused.get()) {
		        if (!work.isDaemon()) {
		            nonDaemonWork.add(work);
		        }
		    } else {
		        executor.submit(work);
		    }
		} finally {
			lock.unlock();
		}
        
	}
	
	// ------------------ Management operations

	public void start() {
		
		if (!paused.get()) {
			return;
		}
		
		Lock lock = readWriteLock.writeLock();
		try {

	        lock.lock();
		    executor = new ThreadPoolExecutor(size, size, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            paused.set(false);
            
			for (PausableWork pausableWork : daemonWork) {
				executor.submit(pausableWork);
			}
			
            for (PausableWork pausableWork : nonDaemonWork) {
                executor.submit(pausableWork);
            }
            
		} finally {
			lock.unlock();
		}
		
	}

	public void stop() {
        
        if (paused.get()) {
            return;
        }
		
		Lock lock = readWriteLock.writeLock();
		try {

	        lock.lock();
            paused.set(true);
            
			for (PausableWork pausableWork : daemonWork) {
				pausableWork.stop();
			}
			
			executor.shutdown();
			executor = null;
			
		} finally {
			lock.unlock();
		}
		
	}
	
}
