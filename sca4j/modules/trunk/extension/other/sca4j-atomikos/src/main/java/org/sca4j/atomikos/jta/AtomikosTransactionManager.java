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
package org.sca4j.atomikos.jta;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.oasisopen.sca.annotation.EagerInit;
import org.oasisopen.sca.annotation.Init;
import org.oasisopen.sca.annotation.Property;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.spi.resource.ResourceRegistry;

import com.atomikos.icatch.jta.UserTransactionManager;

@EagerInit
public class AtomikosTransactionManager implements TransactionManager {
    
    @Reference public ResourceRegistry resourceRegistry;
    @Property public int timeout = 30;

    private UserTransactionManager delegate;
    
    @Init
    public void start() throws SystemException {
        delegate = new UserTransactionManager();
        delegate.setTransactionTimeout(timeout);
        delegate.init();
        resourceRegistry.registerResource(TransactionManager.class, "transactionManager", this);
    }

    public void begin() throws NotSupportedException, SystemException {
        delegate.begin();
    }

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        delegate.commit();
    }

    public int getStatus() throws SystemException {
        return delegate.getStatus();
    }

    public Transaction getTransaction() throws SystemException {
        return delegate.getTransaction();
    }

    public void resume(Transaction arg0) throws InvalidTransactionException, IllegalStateException, SystemException {
        delegate.resume(arg0);
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        delegate.rollback();
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        delegate.setRollbackOnly();
    }

    public void setTransactionTimeout(int arg0) throws SystemException {
        delegate.setTransactionTimeout(arg0);
    }

    public Transaction suspend() throws SystemException {
        return delegate.suspend();
    }

}
