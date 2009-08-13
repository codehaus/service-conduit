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
package org.sca4j.binding.aq.runtime.tx;

import javax.jms.Session;
import javax.jms.XASession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision: 4812 $ $Date: 2008-02-26 09:26:36 +0000 (Tue, 26 Feb
 *          2008) $
 * Handling the JTA transactions         
 */
public class JtaTransactionHandler implements TransactionHandler {

    private TransactionManager transactionManager;

    /**
     * Injects the Transaction Manager
     * 
     * @param transactionManager
     */
    @Reference
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Begin a transaction
     * @throws AQJmsTxException
     */
    public void enlist(final Session session) throws AQJmsTxException {

        assertOnTransactionManager();
        try {
            if (transactionManager.getTransaction() == null) {
                transactionManager.begin();
            }            
            enlistResource(session);
        } catch (SystemException e) {
            throw new AQJmsTxException(e);
        } catch (NotSupportedException e) {
            throw new AQJmsTxException("Transaction Not Supported ", e);
        } catch (IllegalStateException ie) {
            throw new AQJmsTxException(ie);
        } catch (RollbackException re) {
            throw new AQJmsTxException(re);
        }
    }    

    /**
     * Commits the Transaction
     * @throws AQJmsTxException
     */
    public void commit() throws AQJmsTxException {

        assertOnTransactionManager();
        try {
            transactionManager.commit();
        } catch (IllegalStateException e) {
            throw new AQJmsTxException(e);
        } catch (SecurityException e) {
            throw new AQJmsTxException(e);
        } catch (HeuristicMixedException e) {
            throw new AQJmsTxException(e);
        } catch (HeuristicRollbackException e) {
            throw new AQJmsTxException(e);
        } catch (RollbackException e) {
            throw new AQJmsTxException(e);
        } catch (SystemException e) {
            throw new AQJmsTxException(e);
        }

    }

    /**
     * roll's back the transaction
     * @throws AQJmsTxException
     */
    public void rollback() throws AQJmsTxException {

        assertOnTransactionManager();
        try {
            transactionManager.rollback();
        } catch (IllegalStateException e) {
            throw new AQJmsTxException(e);
        } catch (SecurityException e) {
            throw new AQJmsTxException(e);
        } catch (SystemException e) {
            throw new AQJmsTxException(e);
        }

    }

    /*
     * @throws IllegalStateException when the injected
     * {@link TransactionManager} is null
     */
    private void assertOnTransactionManager() {
        if (transactionManager == null) {
            throw new IllegalStateException("No transaction manager available");
        }
    }
    
   /*
    * Enlists the session resource
    */
   private void enlistResource(final Session session) throws RollbackException, SystemException {
       final XASession xaSession = (XASession) session;
       final XAResource xaResource = xaSession.getXAResource();       
       transactionManager.getTransaction().enlistResource(xaResource);
   }
}
