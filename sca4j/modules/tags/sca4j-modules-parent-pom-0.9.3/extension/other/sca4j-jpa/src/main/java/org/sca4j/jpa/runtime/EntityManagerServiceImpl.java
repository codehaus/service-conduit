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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.oasisopen.sca.annotation.Reference;
import org.sca4j.api.scope.Conversation;
import org.sca4j.scdl.Scope;
import org.sca4j.spi.component.ConversationExpirationCallback;
import org.sca4j.spi.component.ScopeContainer;
import org.sca4j.spi.component.ScopeRegistry;

/**
 * Implementation that manages a cache of EntityManagers.
 *
 * @version $Revision$ $Date$
 */
public class EntityManagerServiceImpl implements EntityManagerService {
    public static final Object JOINED = new Object();
    // a chache of entity managers double keyed by scope (transaction or conversation) and persistence unit name
    private Map<Object, Map<String, EntityManager>> cache = new ConcurrentHashMap<Object, Map<String, EntityManager>>();
    // tracks which entity managers have joined transactions
    private Map<Transaction, Object> joinedTransaction = new ConcurrentHashMap<Transaction, Object>();
    private EmfCache emfCache;
    private TransactionManager tm;
    private ScopeContainer<Conversation> scopeContainer;

    public EntityManagerServiceImpl(@Reference EmfCache emfCache, @Reference TransactionManager tm, @Reference ScopeRegistry registry) {
        this.emfCache = emfCache;
        this.tm = tm;
        this.scopeContainer = registry.getScopeContainer(Scope.CONVERSATION);
    }

    public EntityManager getEntityManager(String unitName, EntityManagerProxy proxy, Transaction transaction) throws EntityManagerCreationException {
        // Note this method is threadsafe as a Transaction is only visible to a single thread at time.
        EntityManager em = null;
        Map<String, EntityManager> map = cache.get(transaction);
        if (map != null) {
            em = map.get(unitName);
        }

        if (em == null) {
            // no entity manager for the persistence unit associated with the transaction
            EntityManagerFactory emf = emfCache.getEmf(unitName);
            if (emf == null) {
                throw new EntityManagerCreationException("No EntityManagerFactory found for persistence unit: " + unitName);
            }
            em = emf.createEntityManager();
            // don't synchronize on the transaction since it can assume to be bound to a thread at this point
            registerTransactionScopedSync(proxy, unitName, transaction);
            if (map == null) {
                map = new ConcurrentHashMap<String, EntityManager>();
                cache.put(transaction, map);
            }
            map.put(unitName, em);
        }
        return em;
    }

    public EntityManager getEntityManager(String unitName, EntityManagerProxy proxy, Conversation conversation)
            throws EntityManagerCreationException {
        // synchronize on the conversation since multiple request threads may be active
        synchronized (conversation) {
            EntityManager em = null;
            Map<String, EntityManager> map = cache.get(conversation);
            if (map != null) {
                em = map.get(unitName);
            }

            if (em == null) {
                // no entity manager for the persistence unit associated with the conversation
                try {
                    EntityManagerFactory emf = emfCache.getEmf(unitName);
                    if (emf == null) {
                        throw new EntityManagerCreationException("No EntityManagerFactory found for persistence unit: " + unitName);
                    }
                    // don't synchronize on the transaction since it can assume to be bound to a thread at this point
                    em = emf.createEntityManager();
                    Transaction transaction = tm.getTransaction();
                    boolean mustJoin = !joinedTransaction.containsKey(transaction);
                    scopeContainer.registerCallback(conversation, new JPACallback(proxy, unitName, transaction, mustJoin));
                    // A transaction synchronization needs to be registered so that the proxy can clear the EM after the transaction commits.
                    // This is necessary so joinsTransaction is called for subsequent transactions
                    registerConversationScopedSync(proxy, transaction, mustJoin);
                    if (mustJoin) {
                        // join the current transaction. This only needs to be done for extended persistence conttexts
                        em.joinTransaction();
                        joinedTransaction.put(transaction, JOINED);
                    }
                    if (map == null) {
                        map = new ConcurrentHashMap<String, EntityManager>();
                        cache.put(conversation, map);
                    }
                    map.put(unitName, em);
                } catch (SystemException e) {
                    throw new EntityManagerCreationException(e);
                }
            }
            return em;
        }
    }

    private void registerTransactionScopedSync(EntityManagerProxy proxy, String unitName, Transaction transaction)
            throws EntityManagerCreationException {
        try {
            TransactionScopedSync sync = new TransactionScopedSync(proxy, unitName, transaction);
            transaction.registerSynchronization(sync);
        } catch (RollbackException e) {
            throw new EntityManagerCreationException(e);
        } catch (SystemException e) {
            throw new EntityManagerCreationException(e);
        }
    }

    private void registerConversationScopedSync(EntityManagerProxy proxy, Transaction transaction, boolean joined)
            throws EntityManagerCreationException {
        try {
            ConversationScopedSync sync = new ConversationScopedSync(proxy, transaction, joined);
            transaction.registerSynchronization(sync);
        } catch (RollbackException e) {
            throw new EntityManagerCreationException(e);
        } catch (SystemException e) {
            throw new EntityManagerCreationException(e);
        }
    }

    /**
     * Callback used with a transaction-scoped EntityManager to remove it from the cache and close it.
     */
    private class TransactionScopedSync implements Synchronization {
        private String unitName;
        private Transaction transaction;
        private EntityManagerProxy proxy;

        private TransactionScopedSync(EntityManagerProxy proxy, String unitName, Transaction transaction) {
            this.unitName = unitName;
            this.transaction = transaction;
            this.proxy = proxy;
        }

        public void beforeCompletion() {

        }

        public void afterCompletion(int status) {
            proxy.clearEntityManager();
            Map<String, EntityManager> map = cache.get(transaction);
            assert map != null;
            map.remove(unitName);
            // TODO check that the JPA provider closes the EntityManager instance, since it is not closed here
            if (map.isEmpty()) {
                cache.remove(transaction);
            }
        }
    }

    /**
     * Callback used with a conversation-scoped EntityManager to clear out EM proxies when a transaction completes (necessary for join transaction).
     */
    private class ConversationScopedSync implements Synchronization {
        private Transaction transaction;
        private EntityManagerProxy proxy;
        private boolean joined;

        private ConversationScopedSync(EntityManagerProxy proxy, Transaction transaction, boolean joined) {
            this.transaction = transaction;
            this.proxy = proxy;
            this.joined = joined;
        }

        public void beforeCompletion() {

        }

        public void afterCompletion(int status) {
            if (joined) {
                joinedTransaction.remove(transaction);
            }
            proxy.clearEntityManager();
            // note the EM cache is not cleared here as it is done when the JPACallback is invoked at conversation end
        }
    }


    /**
     * Callback used with an extended persistence context EntityManager to remove it from the cache and close it.
     */
    private class JPACallback implements ConversationExpirationCallback {
        private EntityManagerProxy proxy;
        private String unitName;
        private Transaction transaction;
        private boolean joined;

        public JPACallback(EntityManagerProxy proxy, String unitName, Transaction transaction, boolean joined) {
            this.proxy = proxy;
            this.unitName = unitName;
            this.transaction = transaction;
            this.joined = joined;
        }

        public void expire(Conversation conversation) {
            synchronized (conversation) {
                if (joined) {
                    joinedTransaction.remove(transaction);
                }
                proxy.clearEntityManager();
                Map<String, EntityManager> map = cache.get(conversation);
                assert map != null;
                EntityManager em = map.remove(unitName);
                assert em != null;
                em.close();
                if (map.isEmpty()) {
                    cache.remove(conversation);
                }
            }
        }
    }
}
