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
package org.sca4j.binding.oracle.aq.runtime.wire;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.sca4j.binding.oracle.aq.runtime.listener.MessageListener;
import org.sca4j.binding.oracle.aq.runtime.listener.MessageServiceException;
import org.sca4j.binding.oracle.aq.runtime.monitor.AQMonitor;
import org.sca4j.binding.oracle.aq.runtime.transaction.TransactionHandler;
import org.sca4j.binding.oracle.aq.runtime.transaction.TxCommitException;
import org.sca4j.binding.oracle.aq.runtime.transaction.TxException;
import org.sca4j.host.work.DefaultPausableWork;
import org.sca4j.host.work.WorkScheduler;


/**
 * Performs the processing of reading the data.
 */
public class ConsumerWorker extends DefaultPausableWork {

	/* Default time for queue errors */
	private static final long ERROR_GRACE_PERIOD = 60000;

	private final MessageListener messageListener;
	private final ClassLoader classLoader;
	private final TransactionHandler transactionHandler;
	private final AQMonitor monitor;
	private final long consumptionDelay;


	/**
	 * Creates the Consumer initialises by the given attributes.
	 *
	 * @param dataSourceKey
	 * @param queueName
	 * @param classLoader
	 * @param delay
	 * @param transactionManager
	 * @param queueManager
	 * @param monitor
	 */
	public ConsumerWorker(MessageListener messageListener, long consumerDelay, ClassLoader classLoader,
			              TransactionHandler transactionHandler,
			              AQMonitor monitor) {
		super(true);

		this.messageListener = messageListener;
		consumptionDelay = consumerDelay;
		this.classLoader = classLoader;
		this.transactionHandler = transactionHandler;
		this.monitor = monitor;
	}

	/**
	 * Will be run by the {@link WorkScheduler}.
	 */
	@Override
    public void execute() {

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            try {
                transactionHandler.begin();
            } catch (TxException e) {
                monitor.onException("Exception When commencing transaction " + e.getMessage(), ExceptionUtils.getFullStackTrace(e));
                return;
            }

            try {

                messageListener.onMessage();
                transactionHandler.commit();
                monitor.reportOnCommit("Message has been Commited");

            } catch (TxCommitException ce) {
                monitor.onException("Unexpected Commit Failure " + ce.getMessage(), ExceptionUtils.getFullStackTrace(ce));

            } catch (MessageServiceException se) {
                forceRollback();

            } catch (Throwable ex) {
                forceRollback();
                monitor.onException("Unexpected Runtime Exception " + ex.getMessage(), ExceptionUtils.getFullStackTrace(ex));
                setErrorGracePeriod();
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        
        if(consumptionDelay > 0L){
        	monitor.generalMessage("Consumer Delay is" + consumptionDelay);        	
        	waitOnDequeue(consumptionDelay);
        }
    }


	/*
	 * Waits before Dequeue
	 */
	private synchronized void waitOnDequeue(long time) {
		try {
			wait(time);
		} catch (InterruptedException e) {
			monitor.onException("Interrupted Exception " + e.getMessage(), ExceptionUtils.getFullStackTrace(e));
		}
	}

	/*
	 * Forces A rollback
	 */
	private void forceRollback() {
		try {
			transactionHandler.rollback();
		} catch (RuntimeException re) {
			monitor.onException("Tried to Rollback but failed :- " + re.getMessage(), ExceptionUtils.getFullStackTrace(re));
		}
	}

	/*
	 * Resets Default Time
	 */
	private void setErrorGracePeriod() {
		monitor.generalMessage(" ERROR GRACE PERIOD SET FOR :- " + ERROR_GRACE_PERIOD + " MILLISECONDS ");
		waitOnDequeue(ERROR_GRACE_PERIOD);
	}
}
