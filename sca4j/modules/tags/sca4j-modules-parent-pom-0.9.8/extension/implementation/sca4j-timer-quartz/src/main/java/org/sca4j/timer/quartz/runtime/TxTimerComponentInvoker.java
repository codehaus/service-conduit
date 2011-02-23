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
 */
package org.sca4j.timer.quartz.runtime;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.oasisopen.sca.ServiceRuntimeException;
import org.sca4j.host.management.ManagementService;

/**
 * Adds Transactional support to {@link TimerComponentInvoker}
 */
public class TxTimerComponentInvoker<T> extends TimerComponentInvoker<T> {
    private TransactionManager transactionManager;

    public TxTimerComponentInvoker(TimerComponent<T> component, TransactionManager transactionManager, ManagementService managementService) {
        super(component, managementService);
        this.transactionManager = transactionManager;
    }

    @Override
    public void run() {
        beginTransaction();
        try {
            super.run();
            commitTransaction();
        } catch (RuntimeException e) {
            rollbackTransaction();
        }
    }
    
    private void beginTransaction() {
        try {
            transactionManager.begin();
        } catch (NotSupportedException e) {
            throw new ServiceRuntimeException(e);
        } catch (SystemException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private void commitTransaction() {
        try {
            if (transactionManager.getStatus() != Status.STATUS_MARKED_ROLLBACK) {
                transactionManager.commit();
            } else {
                transactionManager.rollback();
            }
        } catch (SystemException e) {
            throw new ServiceRuntimeException(e);
        } catch (IllegalStateException e) {
            throw new ServiceRuntimeException(e);
        } catch (SecurityException e) {
            throw new ServiceRuntimeException(e);
        } catch (HeuristicMixedException e) {
            throw new ServiceRuntimeException(e);
        } catch (HeuristicRollbackException e) {
            throw new ServiceRuntimeException(e);
        } catch (RollbackException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private void rollbackTransaction() {
        try {
            transactionManager.rollback();
        } catch (SystemException e) {
            throw new ServiceRuntimeException(e);
        }
    }
}
