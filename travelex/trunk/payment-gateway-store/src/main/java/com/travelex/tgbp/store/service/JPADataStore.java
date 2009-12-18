package com.travelex.tgbp.store.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.service.api.DataStore;

public class JPADataStore implements DataStore {

    @PersistenceContext(unitName="tgbp-store")
    protected EntityManager entityManager;

    @Override
    public <T> T store(PersistentEntity<T> entity) {
        entityManager.persist(entity);
        return entity.getKey();
    }

}
