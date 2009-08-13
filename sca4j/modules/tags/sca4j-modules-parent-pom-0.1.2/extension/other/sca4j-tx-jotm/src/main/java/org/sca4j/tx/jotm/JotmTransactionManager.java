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
package org.sca4j.tx.jotm;

import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.objectweb.jotm.Current;
import org.objectweb.jotm.Jotm;
import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Service;

/**
 * JOTM transaction manager with explicit service interface.
 * 
 * @version $Revision$ $Date$
 */
@Service(javax.transaction.TransactionManager.class)
public final class JotmTransactionManager implements TransactionManager {

    private Current delegate;
    private Jotm jotm;

    /**
     * Initializes JOTM.
     * 
     * @throws NamingException
     */
    @Init
    public void init() throws NamingException {

        this.delegate = Current.getCurrent();

        // If none found, create new local JOTM instance.

        if (this.delegate == null) {
            this.jotm = new Jotm(true, false);
            this.delegate = Current.getCurrent();

        }

    }
    
    /**
     * Stops JOTM.
     */
    @Destroy
    public void destroy() {
        if (this.jotm != null) {
            this.jotm.stop();
        }
    }

    /**
     * @throws SystemException 
     * @throws NotSupportedException 
     * @see javax.transaction.TransactionManager#begin()
     */
    public void begin() throws NotSupportedException, SystemException {
        delegate.begin();
    }

    /**
     * @throws SystemException 
     * @throws RollbackException 
     * @throws HeuristicRollbackException 
     * @throws HeuristicMixedException 
     * @see javax.transaction.TransactionManager#commit()
     */
    public void commit() throws HeuristicMixedException, HeuristicRollbackException, RollbackException, SystemException {
        delegate.commit();
    }

    /**
     * @throws SystemException 
     * @see javax.transaction.TransactionManager#getStatus()
     */
    public int getStatus() throws SystemException {
        return delegate.getStatus();
    }

    /**
     * @throws SystemException 
     * @see javax.transaction.TransactionManager#getTransaction()
     */
    public Transaction getTransaction() throws SystemException {
        return delegate.getTransaction();
    }

    /**
     * @throws SystemException 
     * @throws InvalidTransactionException 
     * @see javax.transaction.TransactionManager#resume(javax.transaction.Transaction)
     */
    public void resume(Transaction transaction) throws InvalidTransactionException, SystemException {
        delegate.resume(transaction);
    }

    /**
     * @throws SystemException  
     * @see javax.transaction.TransactionManager#rollback()
     */
    public void rollback() throws SystemException {
        delegate.rollback();
    }

    /**
     * @throws SystemException 
     * @see javax.transaction.TransactionManager#setRollbackOnly()
     */
    public void setRollbackOnly() throws SystemException {
        delegate.setRollbackOnly();
    }

    /**
     * @throws SystemException 
     * @see javax.transaction.TransactionManager#setTransactionTimeout(int)
     */
    public void setTransactionTimeout(int timeout) throws SystemException {
        delegate.setTransactionTimeout(timeout);
    }

    /**
     * @throws SystemException 
     * @see javax.transaction.TransactionManager#suspend()
     */
    public Transaction suspend() throws SystemException {
        return delegate.suspend();
    }
    
}
