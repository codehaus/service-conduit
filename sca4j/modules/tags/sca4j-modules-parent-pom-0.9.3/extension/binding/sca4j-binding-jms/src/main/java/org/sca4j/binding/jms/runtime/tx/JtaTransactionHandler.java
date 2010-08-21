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

import javax.jms.Session;
import javax.jms.XASession;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

/**
 * @version $Revision$ $Date$
 */
public class JtaTransactionHandler implements TransactionHandler {
    
    private TransactionManager transactionManager;
    
    public JtaTransactionHandler(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#getTransaction()
     */
    public Transaction getTransaction() {
        try {
            return transactionManager.getTransaction();
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#enlist(javax.jms.Session)
     */
    public void enlist(Session session) throws JmsTxException {
        try {
            XASession xaSession = (XASession) session;
            XAResource xaResource = xaSession.getXAResource();
            transactionManager.getTransaction().enlistResource(xaResource);
        } catch (Exception e) {
            throw new JmsTxException(e);
        }

    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#delist(javax.jms.Session, int)
     */
    public void delist(Session session, int status) throws JmsTxException {
        try {
            XASession xaSession = (XASession) session;
            XAResource xaResource = xaSession.getXAResource();
            transactionManager.getTransaction().delistResource(xaResource, status);
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#commit()
     */
    public void commit() throws JmsTxException {
        try {
            transactionManager.commit();
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
    }
    
    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#resume(javax.transaction.Transaction)
     */
    public void resume(Transaction transaction) throws JmsTxException {
        try {
            transactionManager.resume(transaction);
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#suspend()
     */
    public void suspend() throws JmsTxException {
        try {
            transactionManager.suspend();
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#begin()
     */
    public void begin() throws JmsTxException {
        try {
            transactionManager.begin();
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
    }

    /**
     * @see org.sca4j.binding.jms.runtime.tx.TransactionHandler#rollback()
     */
    public void rollback() throws JmsTxException {
        try {
            transactionManager.rollback();
        } catch (Exception e) {
            throw new JmsTxException(e);
        }
    }

}
