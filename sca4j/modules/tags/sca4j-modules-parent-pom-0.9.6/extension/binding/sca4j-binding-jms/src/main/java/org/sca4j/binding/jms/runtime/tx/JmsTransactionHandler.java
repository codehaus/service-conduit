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
package org.sca4j.binding.jms.runtime.tx;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.transaction.Transaction;

import org.sca4j.binding.jms.common.SCA4JJmsException;

public class JmsTransactionHandler implements TransactionHandler {
    
    private Session session;
    
    public JmsTransactionHandler(Session session) {
        this.session = session;
    }

    @Override
    public void begin() throws JmsTxException {
    }

    @Override
    public void commit() throws JmsTxException {
        try {
            session.commit();
        } catch (JMSException e) {
            throw new SCA4JJmsException(e.getMessage(), e);
        }
    }

    @Override
    public void delist(Session session, int status) throws JmsTxException {
    }

    @Override
    public void enlist(Session session) throws JmsTxException {
    }

    @Override
    public Transaction getTransaction() throws JmsTxException {
        return null;
    }

    @Override
    public void resume(Transaction transaction) throws JmsTxException {
    }

    @Override
    public void rollback() throws JmsTxException {
        try {
            session.rollback();
        } catch (JMSException e) {
            throw new SCA4JJmsException(e.getMessage(), e);
        }
    }

    @Override
    public void suspend() throws JmsTxException {
    }

}
