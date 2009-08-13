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
package org.sca4j.binding.jms.runtime.tx;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.XASession;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.osoa.sca.annotations.Reference;

/**
 * @version $Revision$ $Date$
 */
public class JtaTransactionHandler implements TransactionHandler {
    
    private TransactionManager transactionManager;
    
    @Reference
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#begin(javax.jms.Session)
     */
    public void enlist(Session session) throws JmsTxException {
        
        if(transactionManager == null) {
            throw new IllegalStateException("No transaction manager available");
        }
        
        try {
            
            Transaction transaction = transactionManager.getTransaction();
            if(transaction == null) {
                transactionManager.begin();
            }
            
            if(!(session instanceof XASession)) {
                throw new JmsTxException("XA session required for global transactions");
            }
            
            XASession xaSession = (XASession) session;
            XAResource xaResource = xaSession.getXAResource();
            
            transactionManager.getTransaction().enlistResource(xaResource);

        } catch (Exception e) {
            throw new JmsTxException(e);
        }
        
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#commit(javax.jms.Session)
     */
    public void commit() throws JmsTxException {
        
        if(transactionManager == null) {
            throw new IllegalStateException("No transaction manager available");
        }
        
        try {
            transactionManager.commit();
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
        
        
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#rollback(javax.jms.Session)
     */
    public void rollback() throws JmsTxException {
        
        if(transactionManager == null) {
            throw new IllegalStateException("No transaction manager available");
        }
        
        try {
            transactionManager.rollback();
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
        
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#createSession(javax.jms.Connection)
     */
    public Session createSession(Connection con) throws JmsTxException {
        
        try {   
            return con.createSession(false, Session.SESSION_TRANSACTED);
        } catch(JMSException e) {
            throw new JmsTxException(e);
        }
        
    }

}
