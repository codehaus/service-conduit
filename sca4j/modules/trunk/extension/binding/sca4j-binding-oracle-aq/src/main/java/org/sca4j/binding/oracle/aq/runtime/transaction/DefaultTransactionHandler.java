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
package org.sca4j.binding.oracle.aq.runtime.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.annotation.Monitor;
import org.sca4j.binding.oracle.aq.runtime.monitor.AQMonitor;


/**
 * Default Implementation to the Transaction Handler
 */
public class DefaultTransactionHandler implements TransactionHandler {

    private final TransactionManager transactionManager;
    private final AQMonitor monitor;

    /**
     * Initialise The Transaction Handler by the {@link TransactionManager}
     * 
     * @param manager
     */
    public DefaultTransactionHandler(@Reference TransactionManager transactionManager, 
                                     @Monitor AQMonitor monitor) {
        this.transactionManager = transactionManager;
        this.monitor = monitor;
    }

    /**
     * Begin the transaction
     */
    public void begin() {
        try {
            transactionManager.begin();
            
        } catch (NotSupportedException ne) {
        	monitor.onException(" TX Begin Failed Does Not Support Nested Transactions: " + ne.getMessage(), ExceptionUtils.getFullStackTrace(ne));
            throw new TxException(ne);
        } catch (SystemException se) {
        	monitor.onException(" TX Begin Failed: " + se.getMessage(), ExceptionUtils.getFullStackTrace(se));
            throw new TxException(se);
        }
    }

    /**
     * Commit on the transaction
     */
    public void commit() {
        try {
            transactionManager.commit();
            
        } catch (HeuristicMixedException hme) {  
        	monitor.onException(" TX Commit Failed: Some relevant Updates Might have been commited : " + hme.getMessage(), ExceptionUtils.getFullStackTrace(hme));
            throw new TxCommitException("Exception Tx Commit : ", hme);            
        } catch (HeuristicRollbackException hre) {            
        	monitor.onException(" TX Commit Failed: All relevant Updates have been rolled back : " + hre.getMessage(), ExceptionUtils.getFullStackTrace(hre));
            throw new TxCommitException("Exception Tx Commit : ", hre);
        } catch (RollbackException re) {            
        	monitor.onException(" TX Commit Failed: All relevant Updates have been rolled back : " + re.getMessage(), ExceptionUtils.getFullStackTrace(re));
            throw new TxCommitException("Exception Tx Commit : ", re);
        } catch (SystemException se) {            
        	monitor.onException(" TX Commit Failed: " + se.getMessage(), ExceptionUtils.getFullStackTrace(se));
            throw new TxException(se);
        }
    }

    /**
     * Rollback the transaction
     */
    public void rollback() {    	
        try {
            transactionManager.rollback();
            
        } catch (SystemException se) {
        	monitor.onException(" TX Rollback Failed: " + se.getMessage(), ExceptionUtils.getFullStackTrace(se));
            throw new TxException(se);
        }
    }

}
