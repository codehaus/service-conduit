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

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.sca4j.fabric.services.work.jca;

import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkManager;
import javax.resource.spi.work.WorkRejectedException;

import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.WorkScheduler;
import org.sca4j.host.work.WorkSchedulerException;

/**
 * A work scheduler implementation based on the JCA SPI work manager.
 * <p/>
 * <p/>
 * This needs a JCA SPI work manager implementation available for scheduling work. Instances can be configured with a
 * work manager implementation that is injected in. It is the responsibility of the runtime environment to make a work
 * manager implementaion available. </p>
 */
public class JcaWorkScheduler implements WorkScheduler {

    /**
     * Underlying JCA work manager
     */
    private WorkManager jcaWorkManager;

    /**
     * Initializes the JCA work manager.
     *
     * @param jcaWorkManager JCA work manager.
     */
    public JcaWorkScheduler(WorkManager jcaWorkManager) {

        if (jcaWorkManager == null) {
            throw new IllegalArgumentException("Work manager cannot be null");
        }
        this.jcaWorkManager = jcaWorkManager;

    }

    /**
     * Schedules a unit of work for future execution. The notification listener is used to register interest in
     * callbacks regarding the status of the work.
     *
     * @param work     The unit of work that needs to be asynchronously executed.
     */
    public <T extends DefaultPausableWork> void scheduleWork(T work) {

        if (work == null) {
            throw new IllegalArgumentException("Work cannot be null");
        }

        JcaWork<T> jcaWork = new JcaWork<T>(work);
        try {
            jcaWorkManager.scheduleWork(jcaWork);
        } catch (WorkRejectedException ex) {
            throw new WorkSchedulerException(ex);
        } catch (WorkException ex) {
            throw new WorkSchedulerException(ex);
        }

    }

    /*
     * JCA work wrapper.
     */
    private class JcaWork<T extends Runnable> implements Work {

        // Work that is being executed.
        private T work;

        /*
         * Initializes the work instance.
         */
        public JcaWork(T work) {
            this.work = work;
        }

        /*
         * Releases the work.
         */
        public void release() {
        }

        /*
         * Performs the work.
         */
        public void run() {
            work.run();
        }

        /*
         * Returns the completed work.
         */
        public T getWork() {
            return work;
        }

    }

}
