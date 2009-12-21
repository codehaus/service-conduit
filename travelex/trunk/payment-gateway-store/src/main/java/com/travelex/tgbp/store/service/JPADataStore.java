package com.travelex.tgbp.store.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.service.api.DataStore;
import com.travelex.tgbp.store.service.api.Query;

public class JPADataStore implements DataStore {

    @PersistenceContext(unitName="tgbp-store")
    protected EntityManager entityManager;

    @Override
    public <T> T store(PersistentEntity<T> entity) {
        entityManager.persist(entity);
        return entity.getKey();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends PersistentEntity<?>> List<T> execute(Query query, Object... params) {
        javax.persistence.Query q = entityManager.createNamedQuery(query.getJpaName());
        if(params != null) {
            int idx = 1;
            for (Object param : params) {
                q.setParameter(idx, param);
            }
        }

        return q.getResultList();
    }

    @Override
    public <T extends PersistentEntity<?>> T lookup(Class<? extends PersistentEntity<?>> entityClass, Object pk) {
        return (T) entityManager.find(entityClass, pk);
    }

}
