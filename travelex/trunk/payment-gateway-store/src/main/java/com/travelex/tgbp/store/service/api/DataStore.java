package com.travelex.tgbp.store.service.api;

import com.travelex.tgbp.store.domain.PersistentEntity;

public interface DataStore {

    <T> T store(PersistentEntity<T> entity);

}
