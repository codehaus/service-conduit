package com.travelex.tgbp.rules.dynamic.capture;

import com.travelex.tgbp.store.domain.PersistentEntity;
import com.travelex.tgbp.store.service.api.DataStore;

public class DummyDataStore implements DataStore {

    public <T> T store(PersistentEntity<T> entity) {
        System.out.println("Storing");
        return entity.getKey();
    }

}
