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
package org.sca4j.jpa.runtime;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.sca4j.api.scope.Conversation;
import org.oasisopen.sca.ServiceRuntimeException;
import org.sca4j.pojo.PojoWorkContextTunnel;
import org.sca4j.spi.invocation.WorkContext;

/**
 * An EntityManager proxy that delegates to a cached instance. This proxy is injected on stateless and conversation-scoped components. This proxy is
 * <strong>not</strong> safe to inject on composite-scoped implementations.
 * <p/>
 * If the persistence context is transaction-scoped (as defined by JPA), the proxy will attempt to retrieve the EntityManager instance associated with
 * the current transaction context from the EntityManagerService. If the persistence context is extended (as defined by JPA), the proxy will attempt
 * to retrieve the EntityManager instance associated with the current conversation. The proxy will cache the EntityManager instance until the
 * transaction completes (or aborts) or the conversation ends.
 *
 * @version $Revision$ $Date$
 */
public class StatefulEntityManagerProxy implements EntityManagerProxy {
    private String unitName;
    private boolean extended;
    private EntityManager em;
    private EntityManagerService emService;
    private TransactionManager tm;

    public StatefulEntityManagerProxy(String unitName, boolean extended, EntityManagerService emService, TransactionManager tm) {
        this.unitName = unitName;
        this.extended = extended;
        this.emService = emService;
        this.tm = tm;
    }

    public void persist(Object entity) {
        initEntityManager();
        em.persist(entity);
    }

    public <T> T merge(T entity) {
        initEntityManager();
        return em.merge(entity);
    }

    public void remove(Object entity) {
        initEntityManager();
        em.remove(entity);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        initEntityManager();
        return em.find(entityClass, primaryKey);
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        initEntityManager();
        return em.getReference(entityClass, primaryKey);
    }

    public void flush() {
        initEntityManager();
        em.flush();
    }

    public void setFlushMode(FlushModeType flushMode) {
        initEntityManager();
        em.setFlushMode(flushMode);
    }

    public FlushModeType getFlushMode() {
        initEntityManager();
        return em.getFlushMode();
    }

    public void lock(Object entity, LockModeType lockMode) {
        initEntityManager();
        em.lock(entity, lockMode);
    }

    public void refresh(Object entity) {
        initEntityManager();
        em.remove(entity);
    }

    public void clear() {
        initEntityManager();
        em.clear();
    }

    public boolean contains(Object entity) {
        initEntityManager();
        return em.contains(entity);
    }

    public Query createQuery(String qlString) {
        initEntityManager();
        return em.createQuery(qlString);
    }

    public Query createNamedQuery(String name) {
        initEntityManager();
        return em.createNamedQuery(name);
    }

    public Query createNativeQuery(String sqlString) {
        initEntityManager();
        return em.createNativeQuery(sqlString);
    }

    public Query createNativeQuery(String sqlString, Class resultClass) {
        initEntityManager();
        return em.createNativeQuery(sqlString, resultClass);
    }

    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        initEntityManager();
        return em.createNativeQuery(sqlString, resultSetMapping);
    }

    public void joinTransaction() {
        initEntityManager();
        em.joinTransaction();
    }

    public Object getDelegate() {
        initEntityManager();
        return em.getDelegate();
    }

    public void close() {
        initEntityManager();
        em.close();
    }

    public boolean isOpen() {
        initEntityManager();
        return em.isOpen();
    }

    public EntityTransaction getTransaction() {
        initEntityManager();
        return em.getTransaction();
    }

    public void clearEntityManager() {
        em = null;
    }

    /**
     * Initalizes the delegated EntityManager. If the persistence context is transaction-scoped, the EntityManager associated with the current
     * transaction will be used. Otherwise, if the persistence context is extended, the EntityManager associated with the current conversation will be
     * used.
     */
    private void initEntityManager() {
        if (em != null) {
            return;
        }
        if (extended) {
            // an extended persistence context, associate it with the current conversation
            WorkContext context = PojoWorkContextTunnel.getThreadWorkContext();
            Conversation conversation = context.peekCallFrame().getConversation();
            if (conversation == null) {
                throw new IllegalStateException("No conversational context associated with the current component");
            }
            try {
                em = emService.getEntityManager(unitName, this, conversation);
            } catch (EntityManagerCreationException e) {
                throw new ServiceRuntimeException(e);
            }
        } else {
            // a transaction-scoped persitence context
            try {
                Transaction trx = tm.getTransaction();
                if (trx == null) {
                    throw new IllegalStateException("A transaction is not active - ensure the component is executing in a managed transaction");
                }
                em = emService.getEntityManager(unitName, this, trx);
            } catch (SystemException e) {
                throw new ServiceRuntimeException(e);
            } catch (EntityManagerCreationException e) {
                throw new ServiceRuntimeException(e);
            }
        }
    }

}
