package com.travelex.tgbp.store.service.api;

import java.util.List;

import com.travelex.tgbp.store.domain.PersistentEntity;

public interface DataStore {

    <T> T store(PersistentEntity<T> entity);

    <T extends PersistentEntity<?>> T lookup(Class<? extends PersistentEntity<?>> entityClass, Object pk);

    <T extends PersistentEntity<?>> List<T> execute(Query query, Object... params);

}
