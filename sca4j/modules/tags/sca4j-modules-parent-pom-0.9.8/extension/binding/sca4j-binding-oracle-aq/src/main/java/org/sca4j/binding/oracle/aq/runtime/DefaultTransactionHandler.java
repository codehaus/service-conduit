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
package org.sca4j.binding.oracle.aq.runtime;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.oasisopen.sca.annotation.Reference;


/**
 * Default Implementation to the Transaction Handler
 */
/**
 * @author meerajk
 *
 */
public class DefaultTransactionHandler implements TransactionHandler {

    @Reference public TransactionManager transactionManager;

    /**
     * @see org.sca4j.binding.oracle.aq.runtime.TransactionHandler#begin()
     */
    public void begin() {
        try {
            transactionManager.begin();
        } catch (NotSupportedException e) {
            throw new TxException(e);
        } catch (SystemException e) {
            throw new TxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.oracle.aq.runtime.TransactionHandler#commit()
     */
    public void commit() {
        try {
            transactionManager.commit();
        } catch (HeuristicMixedException e) {
            throw new TxException(e);       
        } catch (HeuristicRollbackException e) {
            throw new TxException(e);
        } catch (RollbackException e) {
            throw new TxException(e);
        } catch (SystemException e) {
            throw new TxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.oracle.aq.runtime.TransactionHandler#rollback()
     */
    public void rollback() {    	
        try {
            transactionManager.rollback();
        } catch (SystemException e) {
            throw new TxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.oracle.aq.runtime.TransactionHandler#getTransaction()
     */
    public Transaction getTransaction() throws TxException {
        try {
            return transactionManager.getTransaction();
        } catch (SystemException e) {
            throw new TxException(e);
        }
    }

}
